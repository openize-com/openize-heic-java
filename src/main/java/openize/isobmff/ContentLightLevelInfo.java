/*
 * Openize.IsoBmff
 * Copyright (c) 2024-2025 Openize Pty Ltd.
 *
 * This file is part of Openize.IsoBmff.
 *
 * Openize.IsoBmff is available under MIT license, which is
 * available along with Openize.IsoBmff sources.
 */

package openize.isobmff;

import openize.isobmff.io.BitStreamReader;


/**
 * <p>
 * Contains light level information about the image.
 * </p>
 */
public class ContentLightLevelInfo extends Box
{
    /**
     * <p>
     * Indicates the max picture ligth level.
     * </p>
     */
    public /*UInt16*/ int max_content_light_level;

    /**
     * <p>
     * Indicates the picture average ligth level.
     * </p>
     */
    public /*UInt16*/ int max_pic_average_light_level;

    /**
     * <p>
     * Create the box object from the bitstream and box size.
     * </p>
     *
     * @param stream File stream.
     * @param size   Box size in bytes.
     */
    public ContentLightLevelInfo(BitStreamReader stream, /*UInt64*/long size)
    {
        super(BoxType.clli, size);

        max_content_light_level = stream.read(16) & 0xFFFF;
        max_pic_average_light_level = stream.read(16) & 0xFFFF;
    }

    /**
     * <p>
     * Text summary of the box.
     * </p>
     */
    @Override
    public final String toString()
    {
        return String.format("%s Max: %d Avg: %d", type.name(), max_content_light_level, max_pic_average_light_level);
    }
}
