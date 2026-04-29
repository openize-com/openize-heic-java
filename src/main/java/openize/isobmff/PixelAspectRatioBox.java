/*
 * FileFormat.IsoBmff
 * Copyright (c) 2024-2026 Openize Pty Ltd.
 *
 * This file is part of FileFormat.IsoBmff.
 *
 * FileFormat.IsoBmff is available under MIT license, which is
 * available along with FileFormat.IsoBmff sources.
 */

package openize.isobmff;

import openize.isobmff.io.BitStreamReader;


/**
 * <p>
 * Contains the pixel aspect ratio information.
 * </p>
 */
public class PixelAspectRatioBox extends Box
{
    /**
     * <p>
     * Define the relative height of a pixel.
     * </p>
     */
    public final /*UInt32*/ long hSpacing;

    /**
     * <p>
     * Define the relative width of a pixel.
     * </p>
     */
    public final /*UInt32*/ long vSpacing;

    /**
     * <p>
     * Create the box object from the bitstream and box size.
     * </p>
     *
     * @param stream File stream.
     * @param size   Box size in bytes.
     */
    public PixelAspectRatioBox(BitStreamReader stream, /*UInt64*/long size)
    {
        super(BoxType.pasp, size);

        hSpacing = stream.read(32) & 0xFFFFFFFFL;
        vSpacing = stream.read(32) & 0xFFFFFFFFL;
    }

    /**
     * <p>
     * Text summary of the box.
     * </p>
     */
    @Override
    public String toString()
    {
        return String.format("%s hSpacing: %d vSpacing: %d", this.type, hSpacing, vSpacing);
    }
}
