/*
 * Openize.HEIC
 * Copyright (c) 2024-2026 Openize Pty Ltd.
 *
 * This file is part of Openize.HEIC.
 *
 * Openize.HEIC is available under Openize license, which is
 * available along with Openize.HEIC sources.
 */

package openize.heic.tests;

import openize.heic.decoder.HeicImage;
import openize.heic.decoder.PixelFormat;
import openize.io.IOFileStream;
import openize.io.IOMode;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * Decodes every sample with Openize.HEIC and with libheif's command line decoder, and requires the
 * same dimensions and pixels within YCbCr to RGB rounding.
 * </p>
 * <p>
 * Skipped unless libheif's decoder is available: {@code heif-dec} (libheif 1.18+) or {@code heif-convert}
 * on the PATH (Debian/Ubuntu {@code libheif-examples}, Homebrew {@code libheif}), or a command given with
 * {@code -Dopenize.libheif.decoder=/path/to/heif-dec}. {@code -Dopenize.libheif.corpus=/dir} adds the
 * {@code .heic} files of another directory.
 * </p>
 */
public class LibheifComparisonTest extends TestsCore
{
    // YCbCr to RGB conversion rounds differently in the two decoders; the decoded samples are identical
    private static final int MAX_CHANNEL_DIFFERENCE = 2;
    private static final long DECODER_TIMEOUT_SECONDS = 120;

    @DataProvider
    public Object[][] heicFiles() throws IOException
    {
        setup();
        List<Path> heicFiles = new ArrayList<>(listHeicFiles(Paths.get(getSamplesPath())));

        String extraCorpus = System.getProperty("openize.libheif.corpus");
        if (extraCorpus != null)
        {
            heicFiles.addAll(listHeicFiles(Paths.get(extraCorpus)));
        }

        Object[][] parameters = new Object[heicFiles.size()][];
        for (int i = 0; i < heicFiles.size(); i++)
        {
            parameters[i] = new Object[]{ heicFiles.get(i) };
        }
        return parameters;
    }

    @Test(dataProvider = "heicFiles")
    public void decodesLikeLibheif(Path heicFile) throws IOException, InterruptedException
    {
        String libheifDecoder = findLibheifDecoder();
        List<BufferedImage> libheifImages = decodeWithLibheif(libheifDecoder, heicFile);

        try (IOFileStream heicStream = new IOFileStream(heicFile, IOMode.READ))
        {
            HeicImage heicImage = HeicImage.load(heicStream);
            int width = (int) heicImage.getWidth();
            int height = (int) heicImage.getHeight();
            int[] openizePixels = heicImage.getInt32Array(PixelFormat.Argb32);

            // libheif writes every top-level image; the primary one has to be among them
            int smallestDifference = Integer.MAX_VALUE;
            List<String> libheifSizes = new ArrayList<>();
            for (BufferedImage libheifImage : libheifImages)
            {
                libheifSizes.add(libheifImage.getWidth() + "x" + libheifImage.getHeight());
                if (libheifImage.getWidth() == width && libheifImage.getHeight() == height)
                {
                    int[] libheifPixels = libheifImage.getRGB(0, 0, width, height, null, 0, width);
                    smallestDifference = Math.min(smallestDifference, maxChannelDifference(openizePixels, libheifPixels));
                }
            }

            Assert.assertTrue(smallestDifference != Integer.MAX_VALUE,
                String.format("%s decodes to %dx%d, libheif to %s", heicFile.getFileName(), width, height, libheifSizes));
            Assert.assertTrue(smallestDifference <= MAX_CHANNEL_DIFFERENCE,
                String.format("%s differs from libheif by up to %d levels per channel", heicFile.getFileName(), smallestDifference));
        }
    }

    private static List<Path> listHeicFiles(Path directory) throws IOException
    {
        try (Stream<Path> paths = Files.walk(directory))
        {
            return paths
                .filter(path -> path.getFileName().toString().toLowerCase().endsWith(".heic"))
                .sorted()
                .collect(Collectors.toList());
        }
    }

    private static List<Path> listFiles(Path directory, String suffix) throws IOException
    {
        try (Stream<Path> paths = Files.list(directory))
        {
            return paths
                .filter(path -> path.getFileName().toString().endsWith(suffix))
                .sorted()
                .collect(Collectors.toList());
        }
    }

    private static String findLibheifDecoder()
    {
        String configuredDecoder = System.getProperty("openize.libheif.decoder");
        if (configuredDecoder != null)
        {
            return configuredDecoder;
        }

        for (String decoder : new String[]{ "heif-dec", "heif-convert" })
        {
            for (String directory : System.getenv("PATH").split(File.pathSeparator))
            {
                if (Files.isExecutable(Paths.get(directory, decoder)))
                {
                    return decoder;
                }
            }
        }

        throw new SkipException("libheif's heif-dec / heif-convert is not installed");
    }

    private static List<BufferedImage> decodeWithLibheif(String libheifDecoder, Path heicFile) throws IOException, InterruptedException
    {
        Path outputDirectory = Files.createTempDirectory("libheif-");
        try
        {
            Path logFile = outputDirectory.resolve("decoder.log");
            Process process = new ProcessBuilder(libheifDecoder, heicFile.toString(), outputDirectory.resolve("image.png").toString())
                .redirectErrorStream(true)
                .redirectOutput(logFile.toFile())
                .start();

            boolean finished = process.waitFor(DECODER_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!finished)
            {
                process.destroyForcibly();
                Assert.fail(libheifDecoder + " timed out on " + heicFile);
            }
            String log = new String(Files.readAllBytes(logFile), StandardCharsets.UTF_8);
            Assert.assertEquals(process.exitValue(), 0, libheifDecoder + " failed on " + heicFile + ": " + log);

            // image.png for a single image, image-1.png, image-2.png... for several top-level images
            List<BufferedImage> images = new ArrayList<>();
            for (Path pngFile : listFiles(outputDirectory, ".png"))
            {
                images.add(ImageIO.read(pngFile.toFile()));
            }
            return images;
        }
        finally
        {
            for (Path outputFile : listFiles(outputDirectory, ""))
            {
                Files.delete(outputFile);
            }
            Files.delete(outputDirectory);
        }
    }

    private static int maxChannelDifference(int[] argbPixels, int[] otherArgbPixels)
    {
        int maxDifference = 0;
        for (int i = 0; i < argbPixels.length; i++)
        {
            for (int shift = 0; shift <= 16; shift += 8)
            {
                int channel = (argbPixels[i] >> shift) & 0xFF;
                int otherChannel = (otherArgbPixels[i] >> shift) & 0xFF;
                maxDifference = Math.max(maxDifference, Math.abs(channel - otherChannel));
            }
        }
        return maxDifference;
    }
}
