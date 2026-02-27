/*
 * Openize.HEIC
 * Copyright (c) 2024-2025 Openize Pty Ltd.
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

import java.nio.file.Paths;
import java.util.Arrays;

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
        String path = HEICTest.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        int index = path.lastIndexOf("target");
        if (index > 0)
        {
            return path.substring(1, index);
        }
        return path.substring(1);
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
     * Create a reference file.
     * </p>
     *
     * @param filename File name.
     * @param data     Color data.
     */
    protected final void createReference(String filename, /*Byte*/byte[] data)
    {
        String outputFilename = filename + ".bin";

        try (IOFileStream stream = new IOFileStream(Paths.get(getReferencePath(), outputFilename), IOMode.READ_WRITE))
        {
            stream.write(data, 0, data.length);
        }
    }

    /**
     * <p>
     * Compare color data with a reference file.
     * </p>
     *
     * @param filename File name.
     * @param data     Color data.
     */
    protected final void compareWithReference(String filename, /*Byte*/byte[] data)
    {
        String outputFilename = filename + ".bin";

        try (IOFileStream stream = new IOFileStream(Paths.get(getReferencePath(), outputFilename), IOMode.READ))
        {
            final int bytesToRead = 4096;
            int index = 0;

            Assert.assertEquals(stream.getLength(), data.length, String.format("Reference length do not match. " +
                    "Reference length equals %d, read data length equals %d", stream.getLength(), data.length));

            /*Byte*/
            byte[] one = new /*Byte*/byte[bytesToRead];
            /*Byte*/
            byte[] two = new /*Byte*/byte[bytesToRead];

            boolean canRead = true;
            while (canRead)
            {
                int read_bytes_from_stream = stream.read(one, 0, bytesToRead);

                if (index + bytesToRead <= data.length)
                {
                    System.arraycopy(data, index, two, 0, bytesToRead);
                }
                else
                {
                    System.arraycopy(data, index, two, 0, data.length - index);
                }

                index += bytesToRead;

                if (!Arrays.equals(one, two))
                {
                    Assert.fail("Data does not match reference");
                    return;
                }

                canRead = read_bytes_from_stream == bytesToRead && index < data.length;
            }
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
