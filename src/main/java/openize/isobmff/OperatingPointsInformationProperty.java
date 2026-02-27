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
 * The 'oinf' property informs about the different operating points provided by a bitstream and their
 * constitution.Each operating point is related to an output layer set and a combination of a profile, level
 * and tier.
 * </p>
 */
public class OperatingPointsInformationProperty extends FullBox
{
    /**
     * <p>
     * Create the box object from the bitstream and box size.
     * </p>
     *
     * @param stream File stream.
     * @param size   Box size in bytes.
     */
    public OperatingPointsInformationProperty(BitStreamReader stream, /*UInt64*/long size)
    {
        super(stream, BoxType.oinf, size);

        throw new UnsupportedOperationException();
    }

    /**
     * <p>
     * Text summary of the box.
     * </p>
     */
    @Override
    public String toString()
    {
        return String.format("%s OMITTED", this.type);
    }
}
