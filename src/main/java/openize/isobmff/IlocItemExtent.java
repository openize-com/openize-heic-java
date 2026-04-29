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
 * Data class for organized storage on location data extents.
 * </p>
 */
public class IlocItemExtent
{
    /**
     * <p>
     * An index as defined for the construction method.
     * </p>
     */
    public /*UInt32*/ long index;

    /**
     * <p>
     * The absolute offset, in bytes from the data origin of the container, of this extent data.
     * If offset_size is 0, extent_offset takes the value 0.
     * </p>
     */
    public /*UInt32*/ long offset;

    /**
     * <p>
     * The absolute length in bytes of this metadata item extent.
     * If length_size is 0, extent_length takes the value 0.
     * If the value is 0, then length of the extent is the length of the entire referenced container.
     * </p>
     */
    public /*UInt32*/ long length;

    /**
     * <p>
     * Text summary of the box.
     * </p>
     */
    @Override
    public String toString()
    {
        return String.format("extent:: index: %d offset: %d length: %d", index, offset, length);
    }
}
