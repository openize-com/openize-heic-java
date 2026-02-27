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
 * Data reference URL that declare the location of the media data used within the presentation.
 * The data reference index in the sample description ties entries in this table to the samples in the track.
 * A track may be split over several sources in this way.
 * </p>
 */
public class DataEntryUrlBox extends FullBox
{
    /**
     * <p>
     * Name of the entry.
     * </p>
     */
    public String name;

    /**
     * <p>
     * Location of the entry.
     * </p>
     */
    public String location;

    /**
     * <p>
     * Create the box object from the bitstream.
     * </p>
     *
     * @param stream File stream.
     */
    public DataEntryUrlBox(BitStreamReader stream)
    {
        super(stream);

        if (flags.get(0))
        {
            if (type == BoxType.urn)
            {
                name = stream.readString();
            }

            location = stream.readString();
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
        return String.format("%s %s %s", this.type, location, name == null ? "" : name);
    }
}
