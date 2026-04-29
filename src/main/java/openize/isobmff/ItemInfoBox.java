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
 * The item information box provides extra information about file entries.
 * </p>
 */
public class ItemInfoBox extends FullBox
{
    /**
     * <p>
     * A count of the number of entries in the info entry array.
     * </p>
     */
    public final /*UInt32*/ long entry_count;

    /**
     * <p>
     * Array of entries of extra information, each entry is formatted as a box.
     * This array is sorted by increasing item_ID in the entry records.
     * </p>
     */
    public final ItemInfoEntry[] item_infos;
    private ObservableCollection<ItemInfoEntry> children;

    /**
     * <p>
     * Create the box object from the bitstream and box size.
     * </p>
     *
     * @param stream File stream.
     * @param size   Box size in bytes.
     */
    public ItemInfoBox(BitStreamReader stream, /*UInt64*/long size)
    {
        super(stream, BoxType.iinf, size);

        entry_count = stream.read((version & 0xFF) == 0 ? 16 : 32) & 0xFFFFFFFFL;

        item_infos = new ItemInfoEntry[(int) (entry_count & 0xFFFFFFFFL)];
        for (int i = 0; i < (entry_count & 0xFFFFFFFFL); i++)
            item_infos[i] = new ItemInfoEntry(stream);

        setChildren(new ObservableCollection<>(item_infos));
    }

    /**
     * <p>
     * Text summary of the box.
     * </p>
     */
    @Override
    public String toString()
    {
        return String.format("%s count: %d", this.type, entry_count);
    }

    /**
     * <p>
     * Observable collection of entries of extra information.
     * </p>
     */
    public final ObservableCollection<ItemInfoEntry> getChildren()
    {
        return children;
    }

    /**
     * <p>
     * Observable collection of entries of extra information.
     * </p>
     */
    public final void setChildren(ObservableCollection<ItemInfoEntry> value)
    {
        children = value;
    }
}
