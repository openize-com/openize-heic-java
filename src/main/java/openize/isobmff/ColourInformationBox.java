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
 * Contains colour information about the image.
 * If colour information is supplied in both this box, and also in the video bitstream,
 * this box takes precedence, and over‐rides the information in the bitstream.
 * </p>
 */
public class ColourInformationBox extends Box
{
    /**
     * <p>
     * An indication of the type of colour information supplied.
     * </p>
     */
    public final /*UInt32*/ long colour_type;

    /**
     * <p>
     * Indicates the chromaticity coordinates of the source primaries.
     * </p>
     */
    public /*UInt16*/ int colour_primaries;

    /**
     * <p>
     * Indicates the reference opto-electronic transfer characteristic.
     * </p>
     */
    public /*UInt16*/ int transfer_characteristics;

    /**
     * <p>
     * Describes the matrix coefficients used in deriving luma and chroma signals from the green, blue, and red.
     * </p>
     */
    public /*UInt16*/ int matrix_coefficients;

    /**
     * <p>
     * Indicates the black level and range of the luma and chroma signals as derived from E′Y, E′PB, and E′PR or E′R, E′G, and E′B real-valued component signals.
     * </p>
     */
    public boolean full_range_flag;

    /**
     * <p>
     * Create the box object from the bitstream and box size.
     * </p>
     *
     * @param stream File stream.
     * @param size   Box size in bytes.
     */
    public ColourInformationBox(BitStreamReader stream, /*UInt64*/long size)
    {
        super(BoxType.colr, size);

        colour_type = stream.read(32) & 0xFFFFFFFFL;

        if ((colour_type & 0xFFFFFFFFL) == 0x6e636c78) /* nclx; on-screen colours */
        {
            colour_primaries = stream.read(16) & 0xFFFF;
            transfer_characteristics = stream.read(16) & 0xFFFF;
            matrix_coefficients = stream.read(16) & 0xFFFF;
            full_range_flag = stream.readFlag();
            stream.skipBits(7);
        }
      /*  else if ((colour_type & 0xFFFFFFFFL) == 0x72494343) // rICC
        {
            //restricted ICC profile
            //ICC_profile; 
        }
        else if ((colour_type & 0xFFFFFFFFL) == 0x70726f66) // prof
        {
            //unrestricted ICC profile
            //ICC_profile; 
        }*/
    }

    /**
     * <p>
     * Text summary of the box.
     * </p>
     */
    @Override
    public String toString()
    {
        return String.format("%s Color type: %s", this.type, uintToString(colour_type));
    }

    /**
     * <p>
     * Color type code as a string.
     * </p>
     */
    public final String getColourType()
    {
        return uintToString(colour_type);
    }
}
