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
 * An entity group is a grouping of items, which may also group tracks. The entities in an entity group
 * share a particular characteristic or have a particular relationship, as indicated by the grouping type.
 * </p>
 */
public class GroupsListBox extends Box
{
    /**
     * <p>
     * List of nested boxes.
     * </p>
     */
    public final List<EntityToGroupBox> boxes;

    /**
     * <p>
     *     Observable collection of the nested boxes.
     * </p>
     */
    private ObservableCollection<EntityToGroupBox> children;

    /**
     * <p>
     * Create the box object from the bitstream, box size and start position.
     * </p>
     *
     * @param stream   File stream.
     * @param size     Box size in bytes.
     * @param startPos Start position in bits.
     */
    public GroupsListBox(BitStreamReader stream, /*UInt64*/long size, /*UInt64*/long startPos)
    {
        super(BoxType.grpl, size);

        boxes = new ArrayList<>((int) size);

        while (stream.getBitPosition() < startPos + size * 8)
        {
            boxes.add(new EntityToGroupBox(stream));
        }

        setChildren(new ObservableCollection<>(boxes));
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
    public final ObservableCollection<EntityToGroupBox> getChildren()
    {
        return children;
    }

    /**
     * <p>
     * Observable collection of the nested boxes.
     * </p>
     */
    public final void setChildren(ObservableCollection<EntityToGroupBox> value)
    {
        children = value;
    }
}
