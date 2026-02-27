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
 * Associates items with item properties.
 * </p>
 */
public class ItemPropertyAssociation extends FullBox
{
    /**
     * <p>
     * Count of entries.
     * </p>
     */
    public final /*UInt32*/ long entry_count;

    /**
     * <p>
     * Property entrie array.
     * </p>
     */
    public final ItemPropertyEntry[] entries;
    private ObservableCollection<ItemPropertyEntry> children;

    /**
     * <p>
     * Create the box object from the bitstream.
     * </p>
     *
     * @param stream File stream.
     */
    public ItemPropertyAssociation(BitStreamReader stream)
    {
        super(stream, BoxType.ipma);

        entry_count = stream.read(32) & 0xFFFFFFFFL;
        entries = new ItemPropertyEntry[(int) (entry_count & 0xFFFFFFFFL)];

        for (int i = 0; i < (entry_count & 0xFFFFFFFFL); i++)
        {
            entries[i] = new ItemPropertyEntry();

            entries[i].item_ID = stream.read((version & 0xFF) < 1 ? 16 : 32) & 0xFFFFFFFFL;
            entries[i].association_count = (byte) stream.read(8);

            entries[i].associations = new ItemPropertyEntryAssociation[entries[i].association_count];
            for (int j = 0; j < (entries[i].association_count & 0xFF); j++)
            {
                entries[i].associations[j] = new ItemPropertyEntryAssociation();
                entries[i].associations[j].essential = stream.readFlag();
                entries[i].associations[j].property_index = stream.read(flags.get(0) ? 15 : 7) & 0xFFFF;
            }
        }

        setChildren(new ObservableCollection<>(entries));
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
     * Observable collection of the property entries.
     * </p>
     */
    public final ObservableCollection<ItemPropertyEntry> getChildren()
    {
        return children;
    }

    /**
     * <p>
     * Observable collection of the property entries.
     * </p>
     */
    public final void setChildren(ObservableCollection<ItemPropertyEntry> value)
    {
        children = value;
    }

    /**
     * <p>
     * Item property entry data.
     * </p>
     */
    public static class ItemPropertyEntry
    {
        /**
         * <p>
         * Identifies the item with which property is associated.
         * </p>
         */
        public /*UInt32*/ long item_ID;

        /**
         * <p>
         * Count of associations.
         * </p>
         */
        public  byte association_count;

        /**
         * <p>
         * Array of associations.
         * </p>
         */
        public ItemPropertyEntryAssociation[] associations;

        /**
         * <p>
         * Text summary of the box.
         * </p>
         */
        @Override
        public String toString()
        {
            StringBuilder str = new StringBuilder(500);
            str.append(String.format("entry:: id: %d count: %d data: ", item_ID, association_count));
            for (ItemPropertyAssociation.ItemPropertyEntryAssociation item : associations)
                str.append(item.toString()).append(", ");
            return str.toString();
        }
    }

    /**
     * <p>
     * Item property association entry data.
     * </p>
     */
    public static class ItemPropertyEntryAssociation
    {
        /**
         * <p>
         * Set to 1 indicates that the associated property is essential to the item, otherwise it is non-essential.
         * </p>
         */
        public boolean essential;

        /**
         * <p>
         * Is either 0 indicating that no property is associated (the essential indicator shall also be 0),
         * or is the 1-based index of the associated property box in the ItemPropertyContainerBox
         * contained in the same ItemPropertiesBox.
         * </p>
         */
        public /*UInt16*/ int property_index;

        /**
         * <p>
         * Text summary of the box.
         * </p>
         */
        @Override
        public String toString()
        {
            return String.format("%d %d", (essential ? 1 : 0), property_index);
        }
    }
}
