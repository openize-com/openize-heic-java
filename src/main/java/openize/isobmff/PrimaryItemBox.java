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
 * This box contains the identifier of the primary item.
 * </p>
 */
public class PrimaryItemBox extends FullBox
{
    /**
     * <p>
     * The identifier of the primary item.
     * </p>
     */
    public final /*UInt32*/ long item_ID;

    /**
     * <p>
     * Create the box object from the bitstream and box size.
     * </p>
     *
     * @param stream File stream.
     * @param size   Box size in bytes.
     */
    public PrimaryItemBox(BitStreamReader stream, /*UInt64*/long size)
    {
        super(stream, BoxType.pitm, size);

        item_ID = stream.read((version & 0xFF) == 0 ? 16 : 32) & 0xFFFFFFFFL;
    }

    /**
     * <p>
     * Text summary of the box.
     * </p>
     */
    @Override
    public String toString()
    {
        return String.format("%s \nitem_ID: %d", this.type, item_ID);
    }
}
