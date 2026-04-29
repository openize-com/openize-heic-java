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

import java.util.HashMap;


/**
 * <p>
 * Structure for storing data in IsoBmff files.
 * </p>
 */
public class Box
{
    /**
     * <p>
     * Dictionary of external box constructors.
     * </p>
     */
    private static final HashMap<BoxType, ExternalBoxConstructor> _externalParsersDictionary
            = new HashMap<>();
    /**
     * <p>
     * An integer that specifies the number of bytes in this box, including all its fields and
     * contained boxes; if size is 1 then the actual size is in the field large size; if size is 0, then this
     * box is the last one in the file, and its contents extend to the end of the file
     * </p>
     */
    public /*UInt64*/ long size;
    /**
     * <p>
     * Identifies the box type; standard boxes use a compact type, which is normally four printable
     * characters, to permit ease of identification, and is shown so in the boxes below. User extensions
     * use an extended type; in this case, the type field is set to 'uuid'.
     * </p>
     */
    public BoxType type;

    /**
     * <p>
     * Create the box object from the bitstream.
     * </p>
     *
     * @param stream File stream.
     */
    public Box(BitStreamReader stream)
    {
        size = stream.read(32);
        type = BoxType.codeToType(stream.read(32));

        if (size == 1)
        {
            size = (stream.read(32) & 0xFFFFFFFFL) << 32 | (stream.read(32) & 0xFFFFFFFFL);
        }
    }

    /**
     * <p>
     * Create the box object from the box type and box size in bytes.
     * This constructor doesn't read data from the stream.
     * </p>
     *
     * @param boxtype Box type integer.
     * @param size    Box size in bytes.
     */
    public Box(BoxType boxtype, /*UInt64*/long size)
    {
        this.size = size;
        type = boxtype;
    }

    /**
     * <p>
     * Create the box object from the bitstream and box type.
     * </p>
     *
     * @param stream  File stream.
     * @param boxtype Box type integer.
     */
    public Box(BitStreamReader stream, BoxType boxtype)
    {
        size = stream.read(32) & 0xFFFFFFFFL;
        BoxType readType = BoxType.codeToType(stream.read(32));

        if (readType != boxtype)
        {
            throw new IllegalStateException();
        }

        type = readType;

        if (size == 1)
        {
            size = (stream.read(32) & 0xFFFFFFFFL) << 32 | (stream.read(32) & 0xFFFFFFFFL);
        }
    }

    /**
     * <p>
     * Read next box from stream.
     * </p>
     *
     * @param stream File stream reader.
     * @return The parsed box.
     */
    public static Box parseBox(BitStreamReader stream)
    {
        /*UInt64*/
        long startPosition = stream.getBitPosition();
        /*UInt64*/
        long size = stream.read(32) & 0xFFFFFFFFL;
        BoxType type = BoxType.codeToType(stream.read(32));

        if (size == 1)
        {
            size = (stream.read(32) & 0xFFFFFFFFL) << 32 | (stream.read(32) & 0xFFFFFFFFL);
        }

        Box box;
        switch (type)
        {
            case ftyp:
                box = new FileTypeBox(stream, size);
                break;
            case meta:
                box = new MetaBox(stream, size, startPosition);
                break;
            case pitm:
                box = new PrimaryItemBox(stream, size);
                break;
            case iloc:
                box = new ItemLocationBox(stream, size);
                break;
            case ipro:
                box = new ItemProtectionBox(stream, size);
                break;
            case iinf:
                box = new ItemInfoBox(stream, size);
                break;
            case iref:
                box = new ItemReferenceBox(stream, size, startPosition);
                break;
            case idat:
                box = new ItemDataBox(stream, size, startPosition);
                break;
            case iprp:
                box = new ItemPropertiesBox(stream, size, startPosition);
                break;
            case dinf:
                box = new DataInformationBox(stream, size);
                break;
            case grpl:
                box = new GroupsListBox(stream, size, startPosition);
                break;

            case ispe:
                box = new ImageSpatialExtentsProperty(stream, size);
                break;
            case pasp:
                box = new PixelAspectRatioBox(stream, size);
                break;
            case colr:
                box = new ColourInformationBox(stream, size);
                break;
            case clli:
                box = new ContentLightLevelInfo(stream, size);
                break;
            case pixi:
                box = new PixelInformationProperty(stream, size);
                break;
            case rloc:
                box = new RelativeLocationProperty(stream, size);
                break;
            case auxC:
                box = new AuxiliaryTypeProperty(stream, size);
                break;
            case clap:
                box = new CleanApertureBox(stream, size);
                break;
            case irot:
                box = new ImageRotation(stream, size);
                break;
            case lsel:
                box = new LayerSelectorProperty(stream, size);
                break;
            case imir:
                box = new ImageMirror(stream, size);
                break;

            case oinf:
                box = new OperatingPointsInformationProperty(stream, size);
                break;
            case udes:
                box = new UserDescriptionBox(stream, size, startPosition);
                break;

            case moov:
                box = new MovieBox(stream, size, startPosition);
                break;

            case hvcC:
            case av1C:
            default:
                if (_externalParsersDictionary.containsKey(type))
                {
                    box = _externalParsersDictionary.get(type).invoke(stream, size);
                }
                else
                {
                    box = new Box(type, size);
                }
                break;
        }

        /*UInt64*/
        long currentPosition = stream.getBitPosition();

        if (currentPosition - startPosition > size * 8L)
            throw new IndexOutOfBoundsException();

        if (currentPosition - startPosition < size * 8)
        {
            stream.skipBits((int) (size * 8 - (currentPosition - startPosition)));
        }

        return box;
    }

    /**
     * <p>
     * Add external constructor for unimplemented box type.
     * </p>
     *
     * @param type   Box type.
     * @param parser External box constructor.
     */
    public static void setExternalConstructor(BoxType type, ExternalBoxConstructor parser)
    {
        synchronized (_externalParsersDictionary)
        {
            _externalParsersDictionary.putIfAbsent(type, parser);
        }
    }

    /**
     * <p>
     * Convert uint value to string with ASCII coding.
     * </p>
     *
     * @param value Unsigned integer.
     * @return ASCII string.
     */
    protected static String uintToString(/*UInt32*/long value)
    {
        StringBuilder str = new StringBuilder(50);

        for (int i = 24; i >= 0; i -= 8)
        {
            /*UInt32*/
            long b = ((((value & 0xFFFFFFFFL) >> i) & 0xFFFFFFFFL) & 0xFF) & 0xFFFFFFFFL;
            str.append((char) b);
        }
        return str.toString();
    }

    /**
     * <p>
     * Text summary of the box.
     * </p>
     */
    @Override
    public String toString()
    {
        return String.format("%s size: %d [Box to string]", this.type, size);
    }

    /**
     * <p>
     * External box constructor for unimplemented box types.
     * </p>
     */
    public interface ExternalBoxConstructor
    {
        /**
         * <p>
         * External box constructor for unimplemented box types.
         * </p>
         *
         * @param stream Stream reader.
         * @param size   Box size in bytes.
         */
        Box invoke(BitStreamReader stream, /*UInt64*/long size);
    }
}
