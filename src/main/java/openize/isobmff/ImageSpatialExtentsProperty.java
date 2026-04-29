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
 * The ImageSpatialExtentsProperty documents the width and height of the associated image item.
 * Every image item shall be associated with one property of this type, prior to the association of all
 * transformative properties.
 * </p>
 */
public class ImageSpatialExtentsProperty extends FullBox
{
    /**
     * <p>
     * The width of the reconstructed image in pixels.
     * </p>
     */
    public final /*UInt32*/ long image_width;

    /**
     * <p>
     * The height of the reconstructed image in pixels.
     * </p>
     */
    public final /*UInt32*/ long image_height;

    /**
     * <p>
     * Create the box object from the bitstream and box size.
     * </p>
     *
     * @param stream File stream.
     * @param size   Box size in bytes.
     */
    public ImageSpatialExtentsProperty(BitStreamReader stream, /*UInt64*/long size)
    {
        super(stream, BoxType.ispe, size);

        image_width = stream.read(32) & 0xFFFFFFFFL;
        image_height = stream.read(32) & 0xFFFFFFFFL;
    }

    /**
     * <p>
     * Text summary of the box.
     * </p>
     */
    @Override
    public String toString()
    {
        return String.format("%s width: %d height: %d", this.type, image_width, image_height);
    }
}
