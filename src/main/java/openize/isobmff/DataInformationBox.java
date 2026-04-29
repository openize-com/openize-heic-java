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
 * The data information box contains objects that declare the location of the media information in a track.
 * </p>
 */
public class DataInformationBox extends Box
{
    /**
     * <p>
     * The data reference object contains a table of data references (normally URLs) that declare the
     * location(s) of the media data used within the presentation.
     * </p>
     */
    private final DataReferenceBox dref;
    private ObservableCollection<Box> children;

    /**
     * <p>
     * Create the box object from the bitstream and box size.
     * </p>
     *
     * @param stream File stream.
     * @param size   Box size in bytes.
     */
    public DataInformationBox(BitStreamReader stream, /*UInt64*/long size)
    {
        super(BoxType.dinf, size);

        dref = new DataReferenceBox(stream);
        ObservableCollection<Box> tmp0 = new ObservableCollection<>();
        tmp0.add(dref);

        setChildren(tmp0);
    }

    /**
     * <p>
     * Text summary of the box.
     * </p>
     */
    @Override
    public String toString()
    {
        return this.type.name();
    }

    /**
     * <p>
     * Observable collection of the nested boxes.
     * </p>
     */
    public final ObservableCollection<Box> getChildren()
    {
        return children;
    }

    /**
     * <p>
     * Observable collection of the nested boxes.
     * </p>
     */
    public final void setChildren(ObservableCollection<Box> value)
    {
        children = value;
    }
}
