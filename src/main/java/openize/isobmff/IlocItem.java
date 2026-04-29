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


/**
 * <p>
 * Data class for organized storage on location data.
 * </p>
 */
public class IlocItem
{
    /**
     * <p>
     * An arbitrary integer 'name' for this resource which can be used to refer to it.
     * </p>
     */
    public /*UInt32*/ long item_ID;

    /**
     * <p>
     * Indicates the location of data:
     * 0 means data are located in the current file in mdat box;
     * 1 means data are located in the current file in idat box;
     * 2 means data are located in the external file.
     * </p>
     */
    public  byte construction_method;

    /**
     * <p>
     * Contains either zero (‘this file’) or a 1‐based index into the data references in the data information box.
     * </p>
     */
    public /*UInt32*/ long data_reference_index;

    /**
     * <p>
     * A base value for offset calculations within the referenced data.
     * If base_offset_size equals 0, base_offset takes the value 0, i.e. it is unused.
     * </p>
     */
    public /*UInt32*/ long base_offset;

    /**
     * <p>
     * Provides the count of the number of extents into which the resource is fragmented;
     * it must have the value 1 or greater.
     * </p>
     */
    public /*UInt32*/ long extent_count;

    /**
     * <p>
     * Array of extent data.
     * </p>
     */
    public IlocItemExtent[] extents;

    /**
     * <p>
     * Text summary of the box.
     * </p>
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder(300);
        sb.append(String.format("item id: %d method: %d offset : %d\n", item_ID, construction_method, base_offset));
        for (IlocItemExtent extent : extents)
        {
            sb.append(extent.toString()).append('\n');
        }
        return sb.toString();
    }
}
