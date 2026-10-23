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
import org.testng.annotations.BeforeMethod;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestsCore
{
    private String samplesPath;
    private String referencePath;

    protected String getBaseTestDir()
    {
        return "TestsData";
    }

    /**
     * <p>
     * Get project path.
     * </p>
     */
    private static String getProjectPath()
    {
        try
        {
            // target/test-classes -> project root, on any OS
            Path location = Paths.get(HEICTest.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            for (Path dir = location; dir != null; dir = dir.getParent())
            {
                if (dir.getFileName() != null && dir.getFileName().toString().equals("target"))
                    return dir.getParent().toString();
            }
            return location.toString();
        }
        catch (URISyntaxException e)
        {
            throw new IllegalStateException(e);
        }
    }

    /**
     * <p>
     * Get test samples' path.
     * </p>
     */
    private String buildSamplesPath()
    {
        return Paths.get(getProjectPath(), getBaseTestDir(), "samples").toFile().getAbsolutePath();
    }

    /**
     * <p>
     * Samples path.
     * </p>
     */
    protected final void setSamplesPath(String value)
    {
        samplesPath = value;
    }

    /**
     * <p>
     * Get test reference path.
     * </p>
     */
    private String buildReferencePath()
    {
        return Paths.get(getProjectPath(), getBaseTestDir(), "references").toFile().toString();
    }

    /**
     * <p>
     * Reference path.
     * </p>
     */
    protected final void setReferencePath(String value)
    {
        referencePath = value;
    }

    /**
     * <p>
     * Samples path.
     * </p>
     */
    protected final String getSamplesPath()
    {
        return samplesPath;
    }

    /**
     * <p>
     * Reference path.
     * </p>
     */
    protected final String getReferencePath()
    {
        return referencePath;
    }

    /**
     * <p>
     * Startup setup.
     * </p>
     */
    @BeforeMethod
    public final void setup()
    {
        setSamplesPath(buildSamplesPath());
        setReferencePath(buildReferencePath());
    }

    /**
     * <p>
     * Compare color data with a reference digest. Run the tests with {@code -Dopenize.updateReferences=true}
     * to rewrite the digests after an intended change in the decoded output; check such a change against
     * libheif first with {@link LibheifComparisonTest}.
     * </p>
     *
     * @param filename File name.
     * @param data     Color data.
     */
    protected final void compareWithReference(String filename, /*Byte*/byte[] data)
    {
        Path referenceFile = Paths.get(getReferencePath(), filename + ".sha256");
        String actual = data.length + " " + sha256(data);

        try
        {
            if (Boolean.getBoolean("openize.updateReferences"))
            {
                Files.createDirectories(referenceFile.getParent());
                Files.write(referenceFile, (actual + "\n").getBytes(StandardCharsets.US_ASCII));
                return;
            }

            String expected = new String(Files.readAllBytes(referenceFile), StandardCharsets.US_ASCII).trim();
            Assert.assertEquals(actual, expected, "Decoded data does not match the reference digest of " + filename);
        }
        catch (IOException e)
        {
            throw new UncheckedIOException(e);
        }
    }

    private static String sha256(byte[] data)
    {
        try
        {
            StringBuilder hex = new StringBuilder();
            for (byte b : MessageDigest.getInstance("SHA-256").digest(data))
                hex.append(String.format("%02x", b));
            return hex.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new IllegalStateException(e);
        }
    }

    public void processImageFile(String fileName)
    {
        try (IOFileStream fs = new IOFileStream(Paths.get(getSamplesPath(), fileName), IOMode.READ))
        {
            HeicImage image = HeicImage.load(fs);
            byte[] pixels = image.getByteArray(PixelFormat.Argb32);
            compareWithReference(fileName, pixels);
        }
    }
}
