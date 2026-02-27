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
 * This box provides extra information about one entry.
 * </p>
 */
public class ItemInfoEntry extends FullBox
{
    /**
     * <p>
     * Contains either 0 for the primary resource (e.g., the XML contained in an 'xml ' box) or
     * the ID of the item for which the following information is defined.
     * </p>
     */
    public final /*UInt32*/ long item_ID;

    /**
     * <p>
     * Contains either 0 for an unprotected item, or the one‐based index
     * into the item protection  box defining the protection applied to this item(the first box in the item
     * protection box has the index 1).
     * </p>
     */
    public final /*UInt16*/ int item_protection_index;

    /**
     * <p>
     * A 32‐bit value, typically 4 printable characters, that is a defined valid item type indicator, such as 'mime'.
     * </p>
     */
    public /*UInt32*/ long item_type;

    /**
     * <p>
     * A null‐terminated string in UTF‐8 characters containing a symbolic name of the item (source file for file delivery transmissions).
     * </p>
     */
    public final String item_name;

    /**
     * <p>
     * A null‐terminated string in UTF‐8 characters with the MIME type of the item.
     * If the item is content encoded (see below), then the content type refers to the item after content decoding.
     * </p>
     */
    public String content_type;

    /**
     * <p>
     * A string that is an absolute URI, that is used as a type indicator.
     * </p>
     */
    public String item_uri_type;

    /**
     * <p>
     * An optional null‐terminated string in UTF‐8 characters used to indicate
     * that the binary file is encoded and needs to be decoded before interpreted.
     * The values are as defined for Content‐Encoding for HTTP/1.1.
     * </p>
     */
    public String content_encoding;

    /**
     * <p>
     * A bool value, that shows if item is hidden.
     * </p>
     */
    public final boolean item_hidden;

    /**
     * <p>
     * Create the box object from the bitstream.
     * </p>
     *
     * @param stream File stream.
     */
    ItemInfoEntry(BitStreamReader stream)
    {
        super(stream, BoxType.infe);

        item_hidden = flags.get(23);

        if ((version & 0xFF) < 2)
        {
            item_ID = stream.read(16) & 0xFFFFFFFFL;
            item_protection_index = stream.read(16) & 0xFFFF;

            item_name = stream.readString();
            content_type = stream.readString();
            content_encoding = stream.readString(); //optional

            if ((version & 0xFF) == 1)
            {
                /*UInt32*/
                /*long extension_type =*/ stream.read(32); // & 0xFFFFFFFFL; //optional
                //ItemInfoExtension(extension_type); //optional
                throw new UnsupportedOperationException();
            }
        }
        else
        {
            item_ID = stream.read((version & 0xFF) == 2 ? 16 : 32) & 0xFFFFFFFFL;
            item_protection_index = stream.read(16) & 0xFFFF;
            item_type = stream.read(32) & 0xFFFFFFFFL;

            item_name = stream.readString();

            if ((item_type & 0xFFFFFFFFL) == 0x6d696d65 /*mime*/)
            {
                content_type = stream.readString();
                //content_encoding = stream.ReadString(); //optional
            }
            else if ((item_type & 0xFFFFFFFFL) == 0x75726920 /*uri */)
            {
                item_uri_type = stream.readString();
            }
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
        String nn = item_name == null ? "" : item_name;
        String n2 = content_type == null ? "" : content_type;
        return String.format("%s id: %d type: %s name: %s%s", this.type, item_ID, uintToString(item_type), nn, n2);
    }
}
