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

import java.util.ArrayList;
import java.util.List;


/**
 * <p>
 * Contains text description of the image.
 * </p>
 */
public class UserDescriptionBox extends FullBox
{
    /**
     * <p>
     * List of text descriptions of the image.
     * </p>
     */
    public final List<String> description;

    /**
     * <p>
     * Create the box object from the bitstream, box size and start position.
     * </p>
     *
     * @param stream   File stream.
     * @param size     Box size in bytes.
     * @param startPos Start position in bits.
     */
    public UserDescriptionBox(BitStreamReader stream, /*UInt64*/long size, /*UInt64*/long startPos)
    {
        super(stream, BoxType.udes, size);

        description = new ArrayList<>((int) size);

        while (stream.getBitPosition() < startPos + size * 8)
        {
            description.add(stream.readString());
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
        StringBuilder sb = new StringBuilder(500);
        sb.append(String.format("%s description:\n", this.type));
        for (String it : description)
        {
            sb.append(it).append('\n');
        }
        return sb.toString();
    }
}

