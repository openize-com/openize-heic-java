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
 * A common base structure that contains general metadata.
 * </p>
 */
public class MetaBox extends FullBox
{
    /**
     * <p>
     * Dictionary of nested boxes.
     * </p>
     */
    private final HashMap<BoxType, Box> boxes;
    private ObservableCollection<Box> children;

    /**
     * <p>
     * Create the box object from the bitstream, box size and start position.
     * </p>
     *
     * @param stream   File stream.
     * @param size     Box size in bytes.
     * @param startPos Start position in bits.
     */
    public MetaBox(BitStreamReader stream, /*UInt64*/long size, /*UInt64*/long startPos)
    {
        super(stream, BoxType.meta, size);

        boxes = new HashMap<>();

        boxes.put(BoxType.hdlr, new HandlerBox(stream));

        while (stream.getBitPosition() < startPos + size * 8)
        {
            Box box = Box.parseBox(stream);
            boxes.put(box.type, box);
        }

        setChildren(new ObservableCollection<>(boxes.values()));
    }

    /**
     * <p>
     * Handler box.
     * </p>
     */
    public final HandlerBox gethdlr()
    {
        return (HandlerBox) tryGetBox(BoxType.hdlr);
    }

    /**
     * <p>
     * Primary item box.
     * </p>
     */
    public final PrimaryItemBox getpitm()
    {
        return (PrimaryItemBox) tryGetBox(BoxType.pitm);
    }

    /**
     * <p>
     * Item location box.
     * </p>
     */
    public final ItemLocationBox getiloc()
    {
        return (ItemLocationBox) tryGetBox(BoxType.iloc);
    }

    /**
     * <p>
     * Item info box.
     * </p>
     */
    public final ItemInfoBox getiinf()
    {
        return (ItemInfoBox) tryGetBox(BoxType.iinf);
    }

    /**
     * <p>
     * Item properties box.
     * </p>
     */
    public final ItemPropertiesBox getiprp()
    {
        return (ItemPropertiesBox) tryGetBox(BoxType.iprp);
    }

    /**
     * <p>
     * Item reference box.
     * </p>
     */
    public final ItemReferenceBox getiref()
    {
        return (ItemReferenceBox) tryGetBox(BoxType.iref);
    }

    /**
     * <p>
     * Item data box.
     * </p>
     */
    public final ItemDataBox getidat()
    {
        return (ItemDataBox) tryGetBox(BoxType.idat);
    }

    /**
     * <p>
     * Item group box.
     * </p>
     */
    public final GroupsListBox getgrpl()
    {
        return (GroupsListBox) tryGetBox(BoxType.grpl);
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
     * Try to get specified box. Return null if required box not available.
     * </p>
     *
     * @param type Box type.
     * @return Box.
     */
    private Box tryGetBox(BoxType type)
    {
        return boxes.get(type);
    }
}

