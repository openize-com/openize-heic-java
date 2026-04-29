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
 * This box contains the data of metadata items that use the construction method indicating that an item’s
 * data extents are stored within this box.
 * </p>
 */
public class ItemDataBox extends Box
{
    /**
     * <p>
     * The contained meta data in raw format.
     * </p>
     */
    public final byte[] data;

    /**
     * <p>
     * Create the box object from the bitstream, box size and start position.
     * </p>
     *
     * @param stream   File stream.
     * @param size     Box size in bytes.
     * @param startPos Start position in bits.
     */
    public ItemDataBox(BitStreamReader stream, /*UInt64*/long size, /*UInt64*/long startPos)
    {
        super(BoxType.idat, size);

        /*UInt64*/
        long count = size - (stream.getBitPosition() - startPos) / 8;
        data = new byte[(int) (count)];
        for (/*UInt64*/long i = 0; i < count; i++)
            data[(int) (i)] = (byte) stream.read(8);
    }

    /**
     * <p>
     * Text summary of the box.
     * </p>
     */
    @Override
    public String toString()
    {
        StringBuilder hexSB = new StringBuilder(50);
        hexSB.append(this.type.name()).append(' ');
        for (byte b : data)
        {
            hexSB.append(String.format("%02x", b)).append(' ');
        }
        return hexSB.toString();
    }
}
