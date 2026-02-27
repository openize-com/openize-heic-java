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

import java.util.HashMap;


/**
 * <p>
 * Contains an implicitly indexed list of item properties.
 * </p>
 */
public class ItemPropertyContainerBox extends Box
{
    /**
     * <p>
     * Dictionary of properties.
     * </p>
     */
    public final HashMap<Integer, Box> items;
    private ObservableCollection<Box> children;

    /**
     * <p>
     * Create the box object from the bitstream and start position.
     * </p>
     *
     * @param stream   File stream.
     * @param startPos Start position in bits.
     */
    public ItemPropertyContainerBox(BitStreamReader stream, /*UInt64*/long startPos)
    {
        super(stream, BoxType.ipco);

        items = new HashMap<>();
        int i = 1;
        while (stream.getBitPosition() < startPos + size * 8)
        {
            items.put(i, Box.parseBox(stream));
            i++;
        }

        setChildren(new ObservableCollection<>(items.values()));
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

    /**
     * <p>
     * Returns property by index.
     * </p>
     *
     * @param id Property index.
     * @return Box with property data.
     */
    public final Box getPropertyByIndex(int id)
    {
        return items.get(id);
    }
}
