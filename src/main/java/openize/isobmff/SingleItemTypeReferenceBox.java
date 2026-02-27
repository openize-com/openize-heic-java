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
 * Collects all references for one item of a specific type.
 * </p>
 */
public class SingleItemTypeReferenceBox extends Box
{
    /**
     * <p>
     * The ID of the item that refers to other items.
     * </p>
     */
    public final /*UInt32*/ long from_item_ID;

    /**
     * <p>
     * The number of references.
     * </p>
     */
    public final /*UInt32*/ long reference_count;

    /**
     * <p>
     * The array of the IDs of the item referred to.
     * </p>
     */
    public final /*UInt32*/ long[] to_item_ID;

    /**
     * <p>
     * Create the box object from the bitstream and item id size.
     * </p>
     *
     * @param stream  File stream.
     * @param id_size Item id size in bytes.
     */
    public SingleItemTypeReferenceBox(BitStreamReader stream, int id_size)
    {
        super(stream);

        from_item_ID = stream.read(id_size) & 0xFFFFFFFFL;
        reference_count = stream.read(16) & 0xFFFFFFFFL;

        to_item_ID = new /*UInt32*/long[(int) (reference_count & 0xFFFFFFFFL)];

        for (int j = 0; j < (reference_count & 0xFFFFFFFFL); j++)
        {
            to_item_ID[j] = stream.read(id_size) & 0xFFFFFFFFL;
        }
    }

    /**
     * <p>
     * Text summary of the box.
     * </p>
     */
    @Override
    public String toString()
    {
        StringBuilder str = new StringBuilder(50);
        str.append(String.format("%1$s from_item_ID: %2$d refs: ", type, from_item_ID));
        for (/*UInt32*/long id : to_item_ID)
        {
            str.append(id).append(' ');
        }

        return str.toString();
    }
}
