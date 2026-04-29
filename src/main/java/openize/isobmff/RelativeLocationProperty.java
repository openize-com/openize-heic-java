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
 * The RelativeLocationProperty descriptive item property is used to describe the horizontal and
 * vertical position of the reconstructed image of the associated image item relative to the reconstructed
 * image of the related image item identified through the 'tbas' item reference.
 * </p>
 */
public class RelativeLocationProperty extends FullBox
{
    /**
     * <p>
     * Specifies the horizontal offset in pixels of the left-most pixel column of the reconstructed image
     * of the associated image item in the reconstructed image of the related image item. The left-most
     * pixel column of the reconstructed image of the related image item has a horizontal offset equal to 0.
     * </p>
     */
    public final /*UInt32*/ long horizontal_offset;

    /**
     * <p>
     * Specifies the vertical offset in pixels of the top-most pixel row of the reconstructed image
     * of the associated image item in the reconstructed image of the related image item. The top-most
     * pixel row of the reconstructed image of the related image item has a vertical offset equal to 0.
     * </p>
     */
    public final /*UInt32*/ long vertical_offset;

    /**
     * <p>
     * Create the box object from the bitstream and box size.
     * </p>
     *
     * @param stream File stream.
     * @param size   Box size in bytes.
     */
    public RelativeLocationProperty(BitStreamReader stream, /*UInt64*/long size)
    {
        super(stream, BoxType.rloc, size);

        horizontal_offset = stream.read(32) & 0xFFFFFFFFL;
        vertical_offset = stream.read(32) & 0xFFFFFFFFL;
    }

    /**
     * <p>
     * Text summary of the box.
     * </p>
     */
    @Override
    public String toString()
    {
        return String.format("%s horiz offset: %d vert offset: %d", this.type, horizontal_offset, vertical_offset);
    }
}
