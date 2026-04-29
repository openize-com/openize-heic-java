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
 * AuxiliaryTypeProperty box includes a URN identifying the type of the auxiliary image.
 * AuxiliaryTypeProperty may additionally include other fields, as required by the URN.
 * </p>
 */
public class AuxiliaryTypeProperty extends FullBox
{
    /**
     * <p>
     * A null-terminated UTF-8 character string of the Uniform Resource Name (URN) used to identify the type of the associated auxiliary image item.
     * </p>
     */
    public final String aux_type;

    /**
     * <p>
     * Zero or more bytes until the end of the box. The semantics of these bytes depend on the value of aux_type.
     * </p>
     */
    public final byte[] aux_subtype;

    /**
     * <p>
     * Create the box object from the bitstream and box size.
     * </p>
     *
     * @param stream File stream.
     * @param size   Box size in bytes.
     */
    public AuxiliaryTypeProperty(BitStreamReader stream, /*UInt64*/long size)
    {
        super(stream, BoxType.auxC, size);

        aux_type = stream.readString();

        /*UInt64*/
        long count = size - (long) aux_type.length() - 12;
        aux_subtype = new byte[(int) (count)];
        for (/*UInt64*/long i = 0; i < count; i++)
            aux_subtype[(int) (i)] = (byte) stream.read(8);
    }

    /**
     * <p>
     * Text summary of the box.
     * </p>
     */
    @Override
    public String toString()
    {
        return String.format("%s type: %s", this.type, aux_type);
    }
}
