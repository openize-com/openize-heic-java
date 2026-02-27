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
 * The EntityToGroupBox specifies an entity group.
 * </p>
 */
public class EntityToGroupBox extends FullBox
{
    /**
     * <p>
     * A non-negative integer assigned to the particular grouping that shall not be equal
     * to any group_id value of any other EntityToGroupBox, any item_ID value of the hierarchy
     * level(file, movie. or track) that contains the GroupsListBox, or any track_ID value(when the
     * GroupsListBox is contained in the file level).
     * </p>
     */
    public final /*UInt32*/ long group_id;

    /**
     * <p>
     * The number of entity_id values mapped to this entity group.
     * </p>
     */
    public final /*UInt32*/ long num_entities_in_group;

    /**
     * <p>
     * Array of identifiers of items that are present in the hierarchy level(file, movie or track)
     * that contains the GroupsListBox, or to a track, when a track with track_ID equal to entity_id
     * is present and the GroupsListBox is contained in the file level.
     * </p>
     */
    public final /*UInt32*/ long[] entities;

    /**
     * <p>
     * Create the box object from the bitstream.
     * </p>
     *
     * @param stream File stream.
     */
    public EntityToGroupBox(BitStreamReader stream)
    {
        super(stream);

        group_id = stream.read(32) & 0xFFFFFFFFL;
        num_entities_in_group = stream.read(32) & 0xFFFFFFFFL;
        entities = new /*UInt32*/long[(int) (num_entities_in_group & 0xFFFFFFFFL)];

        for (int i = 0; i < (num_entities_in_group & 0xFFFFFFFFL); i++)
            entities[i] = stream.read(32) & 0xFFFFFFFFL;
    }

    /**
     * <p>
     * Text summary of the box.
     * </p>
     */
    @Override
    public String toString()
    {
        StringBuilder str = new StringBuilder(50);
        str.append(String.format("%s group: %d count: %d", this.type, group_id, num_entities_in_group));
        for (/*UInt32*/long id : entities)
        {
            str.append(" ").append(id);
        }
        return str.toString();
    }
}
