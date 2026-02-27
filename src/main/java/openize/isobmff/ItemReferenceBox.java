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
 * Contains all the linking of one item to others via typed references.
 * All references for one item of a specific type are collected into a single item type reference box.
 * </p>
 */
public class ItemReferenceBox extends FullBox
{
    /**
     * <p>
     * List of references.
     * </p>
     */
    public final List<SingleItemTypeReferenceBox> references;
    private ObservableCollection<SingleItemTypeReferenceBox> children;

    /**
     * <p>
     * Create the box object from the bitstream, box size and start position.
     * </p>
     *
     * @param stream   File stream.
     * @param size     Box size in bytes.
     * @param startPos Start position in bits.
     */
    public ItemReferenceBox(BitStreamReader stream, /*UInt64*/long size, /*UInt64*/long startPos)
    {
        super(stream, BoxType.iref, size);

        references = new ArrayList<>((int) size);

        while (stream.getBitPosition() < startPos + size * 8)
        {
            SingleItemTypeReferenceBox box = new SingleItemTypeReferenceBox(stream, (version & 0xFF) == 0 ? 16 : 32);
            references.add(box);
        }

        setChildren(new ObservableCollection<>(references));
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
     * Observable collection of the references.
     * </p>
     */
    public final ObservableCollection<SingleItemTypeReferenceBox> getChildren()
    {
        return children;
    }

    /**
     * <p>
     * Observable collection of the references.
     * </p>
     */
    public final void setChildren(ObservableCollection<SingleItemTypeReferenceBox> value)
    {
        children = value;
    }
}
