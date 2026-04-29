/*
 * Openize.HEIC
 * Copyright (c) 2024-2026 Openize Pty Ltd.
 *
 * This file is part of Openize.HEIC.
 *
 * Openize.HEIC is available under Openize license, which is
 * available along with Openize.HEIC sources.
 */


package openize.heic.decoder;

import openize.isobmff.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * <p>
 * Heic image header class. Grants convenient access to IsoBmff metadata container.
 * </p>
 */
public class HeicHeader
{
    /**
     * <p>
     * Meta data IsoBmff box.
     * </p>
     * @return The meta data IsoBmff box.
     */
    public final MetaBox getMeta(){ return meta; }
    /**
     * <p>
     * Meta data IsoBmff box.
     * </p>
     */
    private final MetaBox meta;

    /**
     * <p>
     * The identifier of the default frame.
     * </p>
     * @return The identifier of the default frame.
     */
    public final /*UInt32*/long getDefaultFrameId() {
        return (meta.getpitm() != null ? meta.getpitm().item_ID : 0);
    }

    /**
     * <p>
     * Initializes a new instance of the heic image header.
     * </p>
     * @param meta {@link MetaBox} data.
     */
    HeicHeader(MetaBox meta)
    {
        this.meta = meta;
    }

    /**
     * <p>
     * Returns properties grouped by frames.
     * </p>
     * @return Dictionary filled with lists of properties that can be accessed by frame id.
     */
    final Map<Long, List<Box>> getProperties()
    {
        return meta.getiprp().getProperties();
    }

    /**
     * <p>
     * Returns frame type and name.
     * </p>
     * @return {@link ItemInfoEntry} that contains type information.
     * @param id Identifier of the frame.
     */
    final ItemInfoEntry getInfoBoxById(/*UInt32*/long id)
    {
        for (ItemInfoEntry info : meta.getiinf().item_infos)
        {
            if (info.item_ID == id)
                return info;
        }
        throw new IllegalStateException();
    }

    /**
     * <p>
     * Returns frame location.
     * </p>
     * @return {@link IlocItem} that contains location information.
     * @param id Identifier of the frame.
     */
    final IlocItem getLocationBoxById(/*UInt32*/long id)
    {
        for (IlocItem item : meta.getiloc().items)
        {
            if (item.item_ID == id)
            {
                return item;
            }
        }
        return null;
    }

    /**
     * <p>
     * Returns content from idat (item data) box by offset and length.
     * </p>
     * @return Byte array.
     * @param offset The offset from the start on the idat box.
     * @param length The length of the data.
     */
    final byte[] getItemDataBoxContent(/*UInt32*/long offset, /*UInt32*/long length)
    {
        if (meta.getidat() == null)
            return new byte[0];

        byte[] data = new byte[(int)(length & 0xFFFFFFFFL)];
        System.arraycopy(meta.getidat().data, (int) offset, data, 0, (int) length);
        return data;
    }

    /**
     * <p>
     * Returns the list of the frames that are used in calculation of the current frame if exists.
     * </p>
     * @return Unsigned integer array.
     * @param id Identifier of the parent frame.
     */
    final /*UInt32*/long[] getDerivedList(/*UInt32*/long id)
    {
        final ItemReferenceBox getiref = meta.getiref();
        if (getiref == null)
        {
            return new /*UInt32*/long[0];
        }
        final Optional<SingleItemTypeReferenceBox> firstItem =
                getiref.references.stream().filter((i) -> i.from_item_ID == id).findFirst();
        if (!firstItem.isPresent())
        {
            return new /*UInt32*/long[0];
        }

        return firstItem.get().to_item_ID;
    }

    /**
     * <p>
     * Returns the derivative type of the frame if exists.
     * </p>
     * @return BoxType enum value.
     * @param id Identifier of the frame.
     */
    final BoxType getDerivedType(/*UInt32*/long id)
    {
        final ItemReferenceBox getiref = meta.getiref();
        if (getiref == null)
        {
            return null;
        }
        final Optional<SingleItemTypeReferenceBox> firstItem = getiref.references.stream().filter((i) -> i.from_item_ID == id).findFirst();
        return firstItem.map(singleItemTypeReferenceBox -> singleItemTypeReferenceBox.type).orElse(null);
    }

    /**
     * <p>
     * Returns the list of EntityToGroupBox's if they exist.
     * </p>
     * @return List of {@link EntityToGroupBox}. Can be `null`.
     */
    final List<EntityToGroupBox> getGroupsIfPresent()
    {
        final GroupsListBox grplBox = this.meta.getgrpl();
        if (grplBox == null)
            return null;

        return grplBox.boxes;
    }
}
