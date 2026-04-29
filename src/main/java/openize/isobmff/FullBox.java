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

import java.util.BitSet;


/**
 * <p>
 * Structure for storing data in IsoBmff files with specified box version and flags.
 * </p>
 */
public class FullBox extends Box
{
    /**
     * <p>
     * An integer that specifies the version of this format of the box.
     * </p>
     */
    protected final byte version;

    /**
     * <p>
     * A map of flags.
     * </p>
     */
    protected BitSet flags = new BitSet(24);

    /**
     * <p>
     * Create the box object from the bitstream.
     * </p>
     *
     * @param stream File stream.
     */
    public FullBox(BitStreamReader stream)
    {
        super(stream);

        version = (byte) stream.read(8);

        for (int i = 0; i < 24; i++)
            flags.set(i, stream.readFlag());
    }

    /**
     * <p>
     * Create the box object from the bitstream and box type.
     * </p>
     *
     * @param stream  File stream.
     * @param boxtype Box type integer.
     */
    public FullBox(BitStreamReader stream, BoxType boxtype)
    {
        super(stream, boxtype);

        version = (byte) stream.read(8);

        for (int i = 0; i < 24; i++)
            flags.set(i, stream.readFlag());
    }

    /**
     * <p>
     * Create the box object from the bitstream, box type and size.
     * </p>
     *
     * @param stream  File stream.
     * @param boxtype Box type integer.
     * @param size    Box size in bytes.
     */
    public FullBox(BitStreamReader stream, BoxType boxtype, /*UInt64*/long size)
    {
        super(boxtype, size);

        version = (byte) stream.read(8);

        for (int i = 0; i < 24; i++)
            flags.set(i, stream.readFlag());
    }

    /**
     * <p>
     * Create the box object from the box type, size, version and flags.
     * This constructor doesn't read data from the stream.
     * </p>
     *
     * @param boxtype Box type integer.
     * @param size    Box size in bytes.
     * @param version The version of this format of the box.
     * @param flags   The map of flags.
     */
    public FullBox(BoxType boxtype, /*UInt64*/long size, byte version, int flags)
    {
        super(boxtype, size);

        this.version = version;
        this.flags = BitSet.valueOf(new long[]{flags});
    }
}

