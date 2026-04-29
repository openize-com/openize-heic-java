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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * The ItemPropertiesBox enables the association of any item with an ordered set of item properties.
 * Item properties are small data records.
 * </p>
 */
public class ItemPropertiesBox extends Box
{
    /**
     * <p>
     * Contains an implicitly indexed list of item properties.
     * </p>
     */
    private final ItemPropertyContainerBox property_container;

    /**
     * <p>
     * Associates items with item properties.
     * </p>
     */
    private final List<ItemPropertyAssociation> association;
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
    public ItemPropertiesBox(BitStreamReader stream, /*UInt64*/long size, /*UInt64*/long startPos)
    {
        super(BoxType.iprp, size);

        property_container = new ItemPropertyContainerBox(stream, stream.getBitPosition());
        ObservableCollection<Box> tmp0 = new ObservableCollection<>();
        tmp0.add(property_container);

        setChildren(tmp0);

        association = new ArrayList<>();
        while (stream.getBitPosition() < startPos + size * 8)
        {
            ItemPropertyAssociation impa = new ItemPropertyAssociation(stream);
            association.add(impa);
            getChildren().add(impa);
        }
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
     * Returns properties in a convinient form factor.
     * </p>
     *
     * @return Dictionary with Lists of boxes that can be accessed by item id.
     */
    public final Map<Long, List<Box>> getProperties()
    {
        HashMap<Long, List<Box>> properties = new HashMap<>();
//        HashMap<Long, List<Box>> essentialProperties = new HashMap<>();
//        HashMap<Long, List<Box>> nonEssentialProperties = new HashMap<>();

        //foreach to while statements conversion
        for (ItemPropertyAssociation item : association)
        {

            for (ItemPropertyAssociation.ItemPropertyEntry itemProperty : item.entries)
            {
                List<Box> all = new ArrayList<>();
//                List<Box> essential = new ArrayList<>();
//                List<Box> nonEssential = new ArrayList<>();

                for (ItemPropertyAssociation.ItemPropertyEntryAssociation prop : itemProperty.associations)
                {
                    all.add(property_container.getPropertyByIndex(prop.property_index & 0xFFFF));
//                    if (prop.essential)
//                    {
//                        essential.add(property_container.getPropertyByIndex(prop.property_index & 0xFFFF));
//                    }
//                    else
//                    {
//                        nonEssential.add(property_container.getPropertyByIndex(prop.property_index & 0xFFFF));
//                    }
                }

                properties.put(itemProperty.item_ID, all);
            //    essentialProperties.put(itemProperty.item_ID, essential);
                //nonEssentialProperties.put(itemProperty.item_ID, nonEssential);
            }
        }

        return properties;
    }
}

