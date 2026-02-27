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
 * This box declares media type of the track, and thus the process by which the media‐data in the track is presented.
 * </p>
 */
public class HandlerBox extends FullBox
{
    /**
     * <p>
     * When present in a media box, contains a value as defined in clause 12, or a value from a derived specification, or registration.
     * When present in a meta box, contains an appropriate value to indicate the format of the meta box contents.
     * The value 'null' can be used in the primary meta box to indicate that it is merely being used to hold resources.
     * </p>
     */
    public final /*UInt32*/ long handler_type;

    /**
     * <p>
     * A null‐terminated string in UTF‐8 characters which gives a human‐readable name for the track type (for debugging and inspection purposes).
     * </p>
     */
    public final String name;

    /**
     * <p>
     * Create the box object from the bitstream.
     * </p>
     *
     * @param stream File stream.
     */
    public HandlerBox(BitStreamReader stream)
    {
        super(stream, BoxType.hdlr);

        long pos = stream.getBitPosition();
        stream.read(32);

        handler_type = stream.read(32) & 0xFFFFFFFFL;

        for (int i = 0; i < 3; i++)
            stream.read(32);

        name = stream.readString();

        while (size - 12 > (stream.getBitPosition() - pos) / 8)
            stream.read(8);
    }

    /**
     * <p>
     * Text summary of the box.
     * </p>
     */
    @Override
    public String toString()
    {
        return String.format("%s\ndata: %s", this.type, uintToString(handler_type));
    }
}
