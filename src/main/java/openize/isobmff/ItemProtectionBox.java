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
 * The item protection box provides an array of item protection information, for use by the Item Information Box.
 * </p>
 */
public class ItemProtectionBox extends FullBox
{
    /**
     * <p>
     * Count of protection information schemas.
     * </p>
     */
    private final /*UInt16*/ int protection_count;

    /**
     * <p>
     * Array of protection information schemas.
     * </p>
     */
    private final ProtectionSchemeInfoBox[] protection_information;

    /**
     * <p>
     * Create the box object from the bitstream and box size.
     * </p>
     *
     * @param stream File stream.
     * @param size   Box size in bytes.
     */
    public ItemProtectionBox(BitStreamReader stream, /*UInt64*/long size)
    {
        super(stream, BoxType.ipro, size);

        protection_count = stream.read(16) & 0xFFFF;
        protection_information = new ProtectionSchemeInfoBox[protection_count & 0xFFFF];

        for (int i = 1; i <= (protection_count & 0xFFFF); i++)
            protection_information[i - 1] = new ProtectionSchemeInfoBox(stream);
    }

    /**
     * <p>
     * Text summary of the box.
     * </p>
     */
    @Override
    public String toString()
    {
        return type.name();
    }
}