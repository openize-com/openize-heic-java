/*
 * Openize.HEIC
 * Copyright (c) 2024-2026 Openize Pty Ltd.
 *
 * This file is part of Openize.HEIC.
 *
 * Openize.HEIC is available under Openize license, which is
 * available along with Openize.HEIC sources.
 */

package openize.io;

/**
 * <p>
 *     Specifies the stream/file access mode
 * </p>
 */
public enum IOMode
{
    /**
     * <p>
     *     Read only mode.
     * </p>
     */
    READ("r"),
    /**
     * <p>
     *     Read and write mode.
     * </p>
     */
    READ_WRITE("rw");

    /**
     * <p>
     *     The access mode for {@link java.io.RandomAccessFile}
     * </p>
     */
    private final String mode;

    /**
     * <p>
     *     Initialize
     * </p>
     * @param mode The access mode for {@link java.io.RandomAccessFile}
     */
    IOMode(String mode)
    {
        this.mode = mode;
    }

    /**
     * <p>
     *     Returns the access mode.
     * </p>
     */
    public String getMode()
    {
        return mode;
    }
}
