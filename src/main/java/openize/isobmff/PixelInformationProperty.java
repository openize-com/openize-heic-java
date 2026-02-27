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
 * The PixelInformationProperty descriptive item property indicates the number and bit depth of
 * colour components in the reconstructed image of the associated image item.
 * </p>
 */
public class PixelInformationProperty extends FullBox
{
    /**
     * <p>
     * This field signals the number of channels by each pixel of the reconstructed image ofthe associated image item.
     * </p>
     */
    public final byte num_channels;

    /**
     * <p>
     * This field indicates the bits per channel for the pixels of the reconstructed image of the associated image item.
     * </p>
     */
    public final byte[] bits_per_channel;

    /**
     * <p>
     * Create the box object from the bitstream and box size.
     * </p>
     *
     * @param stream File stream.
     * @param size   Box size in bytes.
     */
    public PixelInformationProperty(BitStreamReader stream, /*UInt64*/long size)
    {
        super(stream, BoxType.pixi, size);

        num_channels = (byte) stream.read(8);
        bits_per_channel = new byte[num_channels & 0xFF];
        for (int i = 0; i < (num_channels & 0xFF); i++)
            bits_per_channel[i] = (byte) stream.read(8);
    }

    /**
     * <p>
     * Text summary of the box.
     * </p>
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s ch: %d bits:", type, num_channels));
        for (byte b : bits_per_channel)
        {
            sb.append(' ').append(String.format("%x", b));
        }
        return sb.toString();
    }
}

