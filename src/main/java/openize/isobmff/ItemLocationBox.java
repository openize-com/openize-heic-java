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
 * The item location box provides a directory of resources in this or other files, by locating their container,
 * their offset within that container, and their length.
 * </p>
 */
public class ItemLocationBox extends FullBox
{
    /**
     * <p>
     * Indicates the length in bytes of the offset field.
     * </p>
     */
    public final byte offset_size;

    /**
     * <p>
     * Indicates the length in bytes of the length field.
     * </p>
     */
    public final byte length_size;

    /**
     * <p>
     * Indicates the length in bytes of the base_offset field.
     * </p>
     */
    public final byte base_offset_size;

    /**
     * <p>
     * Indicates the length in bytes of the extent.index field.
     * </p>
     */
    public byte index_size;

    /**
     * <p>
     * Counts the number of items in the location item array.
     * </p>
     */
    public /*UInt32*/ long item_count;

    /**
     * <p>
     * Array of the location items.
     * </p>
     */
    public final IlocItem[] items;
    private ObservableCollection<IlocItem> children;

    /**
     * <p>
     * Create the box object from the bitstream and box size.
     * </p>
     *
     * @param stream File stream.
     * @param size   Box size in bytes.
     */
    public ItemLocationBox(BitStreamReader stream, /*UInt64*/long size)
    {
        super(stream, BoxType.iloc, size);

        offset_size = (byte) stream.read(4);
        length_size = (byte) stream.read(4);
        base_offset_size = (byte) stream.read(4);

        if (((version & 0xFF) == 1) || ((version & 0xFF) == 2))
        {
            index_size = (byte) stream.read(4);
        }
        else
        {
            stream.skipBits(4);
        }

        if ((version & 0xFF) < 2)
        {
            item_count = stream.read(16) & 0xFFFFFFFFL;
        }
        else if ((version & 0xFF) == 2)
        {
            item_count = stream.read(32) & 0xFFFFFFFFL;
        }

        items = new IlocItem[(int) (item_count & 0xFFFFFFFFL)];

        for (int i = 0; i < (item_count & 0xFFFFFFFFL); i++)
        {
            items[i] = new IlocItem();
            if ((version & 0xFF) < 2)
            {
                items[i].item_ID = stream.read(16) & 0xFFFFFFFFL;
            }
            else if ((version & 0xFF) == 2)
            {
                items[i].item_ID = stream.read(32) & 0xFFFFFFFFL;
            }

            if (((version & 0xFF) == 1) || ((version & 0xFF) == 2))
            {
                stream.skipBits(12); //0x000
                items[i].construction_method = (byte) stream.read(4);
            }

            items[i].data_reference_index = stream.read(16) & 0xFFFFFFFFL;
            items[i].base_offset = stream.read((base_offset_size & 0xFF) * 8) & 0xFFFFFFFFL;
            items[i].extent_count = stream.read(16) & 0xFFFFFFFFL;
            items[i].extents = new IlocItemExtent[(int) (items[i].extent_count)];

            for (int j = 0; j < (items[i].extent_count & 0xFFFFFFFFL); j++)
            {
                items[i].extents[j] = new IlocItemExtent();

                if ((((version & 0xFF) == 1) || ((version & 0xFF) == 2)) && ((index_size & 0xFF) > 0))
                {
                    items[i].extents[j].index = stream.read((index_size & 0xFF) * 8) & 0xFFFFFFFFL;
                }

                items[i].extents[j].offset = stream.read((offset_size & 0xFF) * 8) & 0xFFFFFFFFL;
                items[i].extents[j].length = stream.read((length_size & 0xFF) * 8) & 0xFFFFFFFFL;
            }
        }

        setChildren(new ObservableCollection<>(items));
    }

    /**
     * <p>
     * Text summary of the box.
     * </p>
     */
    @Override
    public String toString()
    {
        return String.format("%s count: %d", this.type, item_count);
    }

    /**
     * <p>
     * Observable collection of the location items.
     * </p>
     */
    public final ObservableCollection<IlocItem> getChildren()
    {
        return children;
    }

    /**
     * <p>
     * Observable collection of the location items.
     * </p>
     */
    public final void setChildren(ObservableCollection<IlocItem> value)
    {
        children = value;
    }
}
