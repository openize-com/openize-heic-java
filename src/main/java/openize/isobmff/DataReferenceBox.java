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
 * The data reference object contains a table of data references (normally URLs) that declare the 
 * location(s) of the media data used within the presentation.
 * </p>
 */
public class DataReferenceBox extends FullBox
{
    /**
     * <p>
     * The count of data references.
     * </p>
     */
    public final /*UInt32*/long entry_count;

    /**
     * <p>
     * The list of data references.
     * </p>
     */
    public final List<DataEntryUrlBox> entries;

    /**
     * <p>
     * Text summary of the box.
     * </p>
     */
    @Override
    public String toString() { return String.format("%s count: %d",this.type, entry_count); }

    /**
     * <p>
     * Observable collection of the nested boxes.
     * </p>
     */
    public final ObservableCollection<Box> getChildren(){ return children; }
    /**
     * <p>
     * Observable collection of the nested boxes.
     * </p>
     */
    public final void setChildren(ObservableCollection<Box> value){ children = value; }
    private ObservableCollection<Box> children;

    /**
     * <p>
     * Create the box object from the bitstream.
     * </p>
     * @param stream File stream.
     */
    public DataReferenceBox(BitStreamReader stream)
    {
    	super (stream, BoxType.dref);
	
        entry_count = stream.read(32) & 0xFFFFFFFFL;
        entries = new ArrayList<>((int)entry_count);
        for (int i = 1; i <= (entry_count & 0xFFFFFFFFL); i++)
        {
            entries.add(new DataEntryUrlBox(stream));
        }
        
        setChildren(new ObservableCollection<>(entries));
    }
}
