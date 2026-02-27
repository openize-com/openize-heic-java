/*
 * FileFormat.IsoBmff
 * Copyright (c) 2024-2025 Openize Pty Ltd.
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
 * The image mirroring item property mirrors the image about either a vertical or horizontal axis.
 * </p>
 */
public class ImageMirror extends Box
{
    /**
     * <p>
     * Specifies a vertical (axis = 0) or horizontal (axis = 1) axis for the mirroring operation.
     * </p>
     */
    public final byte axis;

    /**
     * <p>
     * Create the box object from the bitstream and box size.
     * </p>
     *
     * @param stream File stream.
     * @param size   Box size in bytes.
     */
    public ImageMirror(BitStreamReader stream, /*UInt64*/long size)
    {
        super(BoxType.imir, size);

        stream.skipBits(7);
        axis = (byte) stream.read(1);
    }

    /**
     * <p>
     * Text summary of the box.
     * </p>
     */
    @Override
    public String toString()
    {
        return this.type.name() + ((axis & 0xFF) == 0 ? "vertically" : "horizontal") + " mirrored";
    }
}
