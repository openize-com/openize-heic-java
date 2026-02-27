/*
 * Openize.HEIC
 * Copyright (c) 2024-2025 Openize Pty Ltd.
 *
 * This file is part of Openize.HEIC.
 *
 * Openize.HEIC is available under Openize license, which is
 * available along with Openize.HEIC sources.
 */

package openize.heic.decoder;

import openize.heic.decoder.io.BitStreamWithNalSupport;
import openize.isobmff.*;

import java.util.*;


/**
 * <p>
 * Heic image frame class.
 * Contains hevc coded data or meta data.
 * </p>
 */
public class HeicImageFrame
{
    private static final List<String> gStringSwitchMap = Arrays.asList(
                    "urn:mpeg:hevc:2015:auxid:1\u0000",
                    "urn:mpeg:hevc:2015:auxid:2\u0000",
                    "portraiteffectsmatte\u0000",
                    "semanticskinmatte\u0000",
                    "semantichairmatte\u0000",
                    "semanticteethmatte\u0000",
                    "semanticglassesmatte\u0000",
                    "semanticskymatte\u0000",
                    "hdrgainmap\u0000",
                    "styledeltamap\u0000",
                    "linearthumbnail\u0000"
                    );
    private final /*UInt32*/ long id;
    private final IlocItem locationBox;
    private final HeicImage parent;
    private final BitStreamWithNalSupport stream;
    private final ImageFrameType imageType;
    /**
     * <p>
     * Indicates the type of derivative content if the frame is derived.
     * </p>
     */
    private final BoxType derivativeType;
    /**
     * <p>
     * Hevc decoder configuration information from Isobmff container.
     * </p>
     */
    HEVCDecoderConfigurationRecord hvcConfig;

    /**
     * <p>
     * Raw YUV pixel data, used when a bit's depth is less or equal 8.
     * Multidimensional array: chroma or luma index, then two-dimensional array with x and y navigation.
     * </p>
     */
    /*Byte*/byte[][][] rawPixels;

    /**
     * <p>
     * Raw YUV pixel data, used when a bit's depth is greater than 8.
     * Multidimensional array: chroma or luma index, then two-dimensional array with x and y navigation.
     * </p>
     */
    /*UInt16*/int[][][] rawPixelsHighColorRange;

    /**
     * <p>
     * Contains the pixel aspect ratio information.
     * This is an unused field that is created for debugging.
     * </p>
     */
    private PixelAspectRatioBox pasp = null;

    /**
     * <p>
     * Contains colour information.
     * This is an unused field that is created for debugging.
     * </p>
     */
    private ColourInformationBox colr = null;

    /**
     * <p>
     * The unique identifier of the image frame.
     * </p>
     */
    public final /*UInt32*/long getID() { return id; }

    private boolean cashed;
    private /*UInt32*/ long ispeWidth;
    private /*UInt32*/ long ispeHeight;
    private final boolean isHidden;

    /**
     * <p>
     * Indicates the type of auxiliary reference layer if the frame type is auxiliary.
     * </p>
     */
    private AuxiliaryReferenceType auxiliaryReferenceType;

    /**
     * <p>
     * Number of channels with color data.
     * </p>
     */
    private  byte numberOfChannels;

    /**
     * <p>
     * Bits per channel with color data.
     * </p>
     */
    private  byte[] bitsPerChannel;

    /**
     * <p>
     * Indicates the presence of transparency of layer.
     * </p>
     */
    private Long alphaReference = null;
    private  byte imageRotationAngle;
    private  byte imageMirrorAxis;

    /**
     * <p>
     * Create Heic image frame object.
     * </p>
     *
     * @param stream     File stream.
     * @param parent     Parent image.
     * @param id         Frame identifier.
     */
    HeicImageFrame(BitStreamWithNalSupport stream, HeicImage parent, /*UInt32*/long id)
    {
        derivativeType = (parent.getHeader().getDerivedType(id));
        ItemInfoEntry informationBox = parent.getHeader().getInfoBoxById(id);
        locationBox = parent.getHeader().getLocationBoxById(id);

        this.auxiliaryReferenceType = AuxiliaryReferenceType.Undefined;
        imageType = ImageFrameType.codeToType(informationBox.item_type & 0xFFFFFFFFL);
        isHidden = informationBox.item_hidden;

        this.id = id;
        this.stream = stream;
        this.parent = parent;
        cashed = false;
    }

    /**
     * <p>
     * Type of image frame content.
     * </p>
     * @return The type of image frame content.
     */
    public final ImageFrameType getImageType()
    {
        return imageType;
    }

    /**
     * <p>
     * Width of the image frame in pixels.
     * </p>
     * @return The width of the image frame in pixels.
     */
    public final /*UInt32*/long getWidth()
    {
        return (imageRotationAngle & 0xFF) % 2 == 0 ? ispeWidth : ispeHeight;
    }

    /**
     * <p>
     * Height of the image frame in pixels.
     * </p>
     * @return The height of the image frame in pixels.
     */
    public final /*UInt32*/long getHeight()
    {
        return (imageRotationAngle & 0xFF) % 2 == 0 ? ispeHeight : ispeWidth;
    }

    /**
     * <p>
     * Indicates the presence of transparency of layer.
     * </p>
     *
     * @return True if frame is linked with alpha data frame, false otherwise.
     */
    public final boolean hasAlpha()
    {
        return alphaReference != null;
    }

    /**
     * <p>
     * Indicates the fact that frame is marked as hidden.
     * </p>
     *
     * @return True if frame is hidden, false otherwise.
     */
    public final boolean isHidden()
    {
        return isHidden;
    }

    /**
     * <p>
     * Indicates the fact that frame contains image data.
     * </p>
     *
     * @return True if frame is image, false otherwise.
     */
    public final boolean isImage()
    {
        return getImageType() == ImageFrameType.hvc1 || isDerived();
    }

    /**
     * <p>
     * Indicates the fact that frame contains image transform data and is inherited from another frame(-s).
     * </p>
     *
     * @return True if frame is derived, false otherwise.
     */
    public final boolean isDerived()
    {
        return getImageType() == ImageFrameType.iden || getImageType() == ImageFrameType.iovl || getImageType() == ImageFrameType.grid;
    }

    /**
     * <p>
     * Returns the type of derivative content if the frame is derived.
     * </p>
     * @return The type of derivative content if the frame is derived.
     */
    public final BoxType getDerivativeType()
    {
        return derivativeType;
    }

    /**
     * <p>
     * Returns the type of auxiliary reference layer if the frame type is auxiliary.
     * </p>
     * @return The type of auxiliary reference layer if the frame type is auxiliary.
     */
    public final AuxiliaryReferenceType getAuxiliaryReferenceType()
    {
        return auxiliaryReferenceType;
    }

    /**
     * <p>
     * Number of channels with color data.
     * </p>
     * @return The number of channels with color data.
     */
    public final byte getNumberOfChannels()
    {
        return numberOfChannels;
    }

    /**
     * <p>
     * Bits per channel with color data.
     * </p>
     * @return The bits per channel with color data.
     */
    public final byte[] getBitsPerChannel()
    {
        return bitsPerChannel;
    }

    /**
     * <p>
     * Get pixel data in the format of byte array.
     * </p>
     * <p>Each three or four bytes (the count depends on the pixel format) refer to one pixel left to right top to bottom line by line.</p>
     *
     * @param pixelFormat     Pixel format that defines the order of colors and the presence of alpha byte.
     * @return Byte array, null if frame does not contain image data.
     */
    public final byte[] getByteArray(PixelFormat pixelFormat)
    {
        return getByteArray(pixelFormat, new Rectangle(), null);
    }

    /**
     * <p>
     * Get pixel data in the format of byte array.
     * </p>
     * <p>Each three or four bytes (the count depends on the pixel format) refer to one pixel left to right top to bottom line by line.</p>
     *
     * @param pixelFormat     Pixel format that defines the order of colors and the presence of alpha byte.
     * @param dstArray        Byte array for storing the pixel values. If {@code null} or its length is less than
     *                        necessary the new array will be allocated and returned.
     * @return Byte array, null if frame does not contain image data. In general, it equals to {@code dstArray}.
     */
    public final byte[] getByteArray(PixelFormat pixelFormat, byte[] dstArray)
    {
        return getByteArray(pixelFormat, new Rectangle(), dstArray);
    }

    /**
     * <p>
     * Get pixel data in the format of byte array.
     * </p>
     * <p>Each three or four bytes (the count depends on the pixel format) refer to one pixel left to right top to bottom line by line.</p>
     *
     * @param pixelFormat     Pixel format that defines the order of colors and the presence of alpha byte.
     * @param boundsRectangle Bounds of the requested area.
     * @param dstArray        Byte array for storing the pixel values. If {@code null} or its length is less than
     *                        necessary the new array will be allocated and returned.
     * @return Byte array, null if frame does not contain image data. In general, it equals to {@code dstArray}.
     */
    public final byte[] getByteArray(PixelFormat pixelFormat, Rectangle boundsRectangle, byte[] dstArray)
    {
        if (!isImage())
        {
            return null;
        }

        boundsRectangle = validateBounds(boundsRectangle);

        
        byte[][][] threeDim = getMultidimArray(boundsRectangle);
        applyPixelFormat(threeDim, pixelFormat);

        int bpp = pixelFormat == PixelFormat.Rgb24 ? 3 : 4;
        
        byte[] output = dstArray;

        final int reqArraySize = boundsRectangle.getHeight() * boundsRectangle.getWidth() * bpp;
        if (output == null || output.length < reqArraySize)
        {
            output = new byte[reqArraySize];
        }

        int index = 0;
        final int rectangleBottom = boundsRectangle.getBottom();
        final int boundsRectangleLeft = boundsRectangle.getLeft();
        final int boundsRectangleRight = boundsRectangle.getRight();
        for (int row = boundsRectangle.getTop(); row < rectangleBottom; row++)
        {
            for (int col = boundsRectangleLeft; col < boundsRectangleRight; col++)
            {
                output[index++] = threeDim[col][row][0];
                output[index++] = threeDim[col][row][1];
                output[index++] = threeDim[col][row][2];
                if (bpp == 4)
                {
                    output[index++] = threeDim[col][row][3];
                }
            }
        }

        return output;
    }

    /**
     * <p>
     * Get pixel data in the format of integer array.
     * </p>
     * <p>Each int value refers to one pixel left to right top to bottom line by line.</p>
     *
     * @param pixelFormat     Pixel format that defines the order of colors.
     * @return Integer array, null if frame does not contain image data.
     */
    public final int[] getInt32Array(PixelFormat pixelFormat)
    {
        return getInt32Array(pixelFormat, new Rectangle(), null);
    }

    /**
     * <p>
     * Get pixel data in the format of integer array.
     * </p>
     * <p>Each int value refers to one pixel left to right top to bottom line by line.</p>
     *
     * @param pixelFormat     Pixel format that defines the order of colors.
     * @param dstArray        Integer array for storing the argb values. If {@code null} or its length is less than
     *                        necessary the new array will be allocated and returned.
     * @return Integer array, null if frame does not contain image data.  In general, it equals to {@code dstArray}.
     */
    public final int[] getInt32Array(PixelFormat pixelFormat, int[] dstArray)
    {
        return getInt32Array(pixelFormat, new Rectangle(), dstArray);
    }

    /**
     * <p>
     * Get pixel data in the format of integer array.
     * </p>
     * <p>Each int value refers to one pixel left to right top to bottom line by line.</p>
     *
     * @param pixelFormat     Pixel format that defines the order of colors.
     * @param boundsRectangle Bounds of the requested area.
     * @param dstArray        Integer array for storing the argb values. If {@code null} or its length is less than
     *                        necessary the new array will be allocated and returned.
     * @return Integer array, null if frame does not contain image data. In general, it equals to {@code dstArray}.
     */
    public final int[] getInt32Array(PixelFormat pixelFormat, Rectangle boundsRectangle, int[] dstArray)
    {
        if (!isImage())
        {
            return null;
        }

        boundsRectangle = validateBounds(boundsRectangle);

        byte[][][] threeDim = getMultidimArray(boundsRectangle);
        applyPixelFormat(threeDim, pixelFormat);

        final int reqArraySize = boundsRectangle.getHeight() * boundsRectangle.getWidth();

        int[] output = dstArray;
        if (output == null || output.length < reqArraySize)
        {
            output = new int[reqArraySize];
        }

        int index = 0;
        final int rectangleBottom = boundsRectangle.getBottom();
        final int boundsRectangleLeft = boundsRectangle.getLeft();
        final int boundsRectangleRight = boundsRectangle.getRight();
        for (int row = boundsRectangle.getTop(); row < rectangleBottom; row++)
        {
            for (int col = boundsRectangleLeft; col < boundsRectangleRight; col++)
            {
                output[index++] =
                        (threeDim[col][row][0] & 0xFF) << 24 |
                                (threeDim[col][row][1] & 0xFF) << 16 |
                                (threeDim[col][row][2] & 0xFF) << 8 |
                                (threeDim[col][row][3] & 0xFF);
            }
        }

        return output;
    }

    /**
     * <p>
     * Get frame text data.
     * </p>
     * <p>Exists only for mime frame types.</p>
     *
     * @return The frame text data.
     */
    public final String getTextData()
    {
        final ImageFrameType frameType = getImageType();
        if (frameType != ImageFrameType.mime && frameType != ImageFrameType.uri)
        {
            return "";
        }

        IlocItem location = null;
        for (IlocItem item : parent.getHeader().getMeta().getiloc().items)
        {
            if (item.item_ID == id)
            {
                location = item;
                break;
            }
        }

        if (location == null)
        {
            return "";
        }

        stream.setBytePosition(location.base_offset + location.extents[0].offset);
        return stream.readString();
    }

    /**
     * <p>
     * Add layer reference to the frame.
     * Used to link reference layers that are reverse-linked (alpha layer, depth map, hdr).
     * </p>
     *
     * @param id   A layer identifier.
     * @param type A layer type.
     */
    final void addLayerReference(/*UInt32*/long id, AuxiliaryReferenceType type)
    {
        if (Objects.requireNonNull(type) == AuxiliaryReferenceType.Alpha)
        {
            this.alphaReference = id;
        }
    }

    /**
     * <p>
     * Load frame properties from Box and stream.
     * </p>
     * @param stream File stream.
     * @param properties Frame properties described in container.
     */
    final void loadProperties(BitStreamWithNalSupport stream, List<Box> properties)
    {
        for (Box item : properties)
        {
            switch (item.type)
            {
                case hvcC: // hvcC
                    HEVCConfigurationBox config = (HEVCConfigurationBox) item;
                    stream.createNewImageContext(id);
                    stream.setBytePosition(config.offset);
                    hvcConfig = new HEVCDecoderConfigurationRecord(stream);
                    config.record = hvcConfig; // gui
                    break;
                case ispe:
                    ImageSpatialExtentsProperty ispe = (ImageSpatialExtentsProperty) item;
                    ispeWidth = ispe.image_width;
                    ispeHeight = ispe.image_height;
                    break;
                case pasp:
                    pasp = (PixelAspectRatioBox) item;
                    break;
                case colr:
                    colr = (ColourInformationBox) item;
                    break;
                case pixi:
                    PixelInformationProperty pixi = (PixelInformationProperty) item;
                    this.numberOfChannels = pixi.num_channels;
                    this.bitsPerChannel = pixi.bits_per_channel;
                    break;
//                case rloc:
//                    RelativeLocationProperty rloc = (RelativeLocationProperty) item;
//                    break;
                case auxC:
                    AuxiliaryTypeProperty auxC = (AuxiliaryTypeProperty) item;

                    this.auxiliaryReferenceType = AuxiliaryReferenceType.Undefined;

                    switch (gStringSwitchMap.indexOf(auxC.aux_type))
                    {
                        case /*"urn:mpeg:hevc:2015:auxid:1\u0000"*/0:
                            this.auxiliaryReferenceType = AuxiliaryReferenceType.Alpha;
                            break;
                        case /*"urn:mpeg:hevc:2015:auxid:2\u0000"*/1:
                            this.auxiliaryReferenceType = AuxiliaryReferenceType.DepthMap;
                            break;
                        default:
                            if (auxC.aux_type.contains("apple") && auxC.aux_type.contains(":aux:"))
                            {
                                switch (gStringSwitchMap.indexOf(auxC.aux_type.substring(auxC.aux_type.indexOf(":aux:") + 5)))
                                {
                                    case /*"portraiteffectsmatte\u0000"*/2:
                                        this.auxiliaryReferenceType = AuxiliaryReferenceType.PortraitEffectsMatte;
                                        break;
                                    case /*"semanticskinmatte\u0000"*/3:
                                        this.auxiliaryReferenceType = AuxiliaryReferenceType.SemanticSkinMatte;
                                        break;
                                    case /*"semantichairmatte\u0000"*/4:
                                        this.auxiliaryReferenceType = AuxiliaryReferenceType.SemanticHairMatte;
                                        break;
                                    case /*"semanticteethmatte\u0000"*/5:
                                        this.auxiliaryReferenceType = AuxiliaryReferenceType.SemanticTeethMatte;
                                        break;
                                    case /*"semanticglassesmatte\u0000"*/6:
                                        this.auxiliaryReferenceType = AuxiliaryReferenceType.SemanticGlassesMatte;
                                        break;
                                    case /*"semanticskymatte\u0000"*/7:
                                        auxiliaryReferenceType = AuxiliaryReferenceType.SemanticSkyMatte;
                                        break;
                                    case /*"hdrgainmap\u0000"*/8:
                                        this.auxiliaryReferenceType = AuxiliaryReferenceType.HdrGainMap;
                                        break;
                                    case /*"styledeltamap\u0000"*/9:
                                        this.auxiliaryReferenceType = AuxiliaryReferenceType.StyleDeltaMap;
                                        break;
                                    case /*"linearthumbnail\u0000"*/10:
                                        this.auxiliaryReferenceType = AuxiliaryReferenceType.LinearThumbnail;
                                        break;
                                    default:
                                        // Do nothing
                                }
                            }
                            break;
                    }

                    /*UInt32*/
                    long[] derived = parent.getHeader()
                                           .getDerivedList(id);
                    for (/*UInt32*/long derivedId : derived)
                    {
                        parent.getAllFrames()
                              .get(derivedId)
                              .addLayerReference(id, this.auxiliaryReferenceType);
                    }
                    break;
//                case clap:
//                    CleanApertureBox clap = (CleanApertureBox) item;
//                    break;
                case irot:
                    ImageRotation irot = (ImageRotation) item;
                    this.imageRotationAngle = irot.angle;
                    break;
//                case lsel:
//                    LayerSelectorProperty lsel = (LayerSelectorProperty) item;
//                    break;
                case imir:
                    ImageMirror imir = (ImageMirror) item;
                    this.imageMirrorAxis = (/*Byte*/byte) ((imir.axis & 0xFF) + 1);
                    break;
                default:
                    break;
            }
        }
    }

    private byte[][][] getMultidimArray(Rectangle boundsRectangle)
    {
        
        byte[][][] rgba;

        
        byte[] databox = (locationBox.construction_method & 0xFF) == 1 ?
                parent.getHeader().getItemDataBoxContent(
                        ((locationBox.base_offset & 0xFFFFFFFFL) + (locationBox.extents[0].offset & 0xFFFFFFFFL)) & 0xFFFFFFFFL,
                        locationBox.extents[0].length) :
                new byte[0];

        switch (getImageType())
        {
            case hvc1:
                if (!cashed)
                {
                    loadHvcRawPixels();
                }

                YuvConverter converter = new YuvConverter(this);
                rgba = converter.getRgbaByteArray();
                break;
            case iden:
                /*UInt32*/
                long[] derived = parent.getHeader().getDerivedList(id);
                HeicImageFrame frame = parent.getAllFrames().get(derived[0]);
                rgba = frame.getMultidimArray(new Rectangle(0, 0, (int) frame.getWidth(), (int) frame.getHeight()));
                break;
            case iovl:
                rgba = getByteArrayForOverlay(databox);
                break;
            case grid:
                rgba = getByteArrayForGrid(databox);
                break;
            default:
                throw new IllegalStateException("Unknown image type.");
        }

        if (rgba == null)
        {
            throw new IllegalStateException("Unknown image type.");
        }

        rgba = transformImage(rgba);
        addAlphaLayer(rgba, boundsRectangle);

        return rgba;
    }

    private Rectangle validateBounds(Rectangle boundsRectangle)
    {
        if (boundsRectangle == null || boundsRectangle.isEmpty())
        {
            return new Rectangle(0, 0, (int) getWidth(), (int) getHeight());
        }

        if (boundsRectangle.getLeft() < 0 || boundsRectangle.getTop() < 0 ||
                boundsRectangle.getRight() > (int) getWidth() || boundsRectangle.getBottom() > (int) getHeight())
        {
            throw new IndexOutOfBoundsException("The specification of selection area cannot exceed the size of an image.");
        }

        return boundsRectangle;
    }

    private void addAlphaLayer(byte[][][] pixels, Rectangle boundsRectangle)
    {
        if (alphaReference == null)
        {
            return;
        }
        long tmp0 = alphaReference & 0xFFFFFFFFL;

        byte[][][] alpha = parent.getAllFrames().get(tmp0).getMultidimArray(boundsRectangle);

        final long height = getHeight() & 0xFFFFFFFFL;
        final long width = getWidth() & 0xFFFFFFFFL;
        for (/*UInt32*/long row = 0; row < height; row++)
        {
            for (/*UInt32*/long col = 0; col < width; col++)
            {
                pixels[(int) (col)][(int) (row)][3] = alpha[(int) (col)][(int) (row)][0];
            }
        }
    }

    private byte[][][] transformImage(byte[][][] pixels)
    {
        final int rotationAngle = imageRotationAngle & 0xFF;
        if (rotationAngle == 0 && (imageMirrorAxis & 0xFF) == 0)
        {
            return pixels;
        }

        final long width = getWidth() & 0xFFFFFFFFL;
        final long height = getHeight() & 0xFFFFFFFFL;
        byte[][][] rotated = new byte[(int) width][(int) height][4];

        /*UInt32*/
        long oldCol, oldRow;

        for (/*UInt32*/long newRow = 0; newRow < height; newRow++)
        {
            for (/*UInt32*/long newCol = 0; newCol < width; newCol++)
            {
                switch (imageRotationAngle)
                {
                    case 3:
                        oldCol = newRow;
                        oldRow = width - newCol - 1;
                        break;
                    case 2:
                        oldCol = width - newCol - 1;
                        oldRow = width - newRow - 1;
                        break;
                    case 1:
                        oldCol = height - newRow - 1;
                        oldRow = newCol;
                        break;
                    case 0:
                    default:
                        oldCol = newCol;
                        oldRow = newRow;
                        break;
                }

                if ((imageMirrorAxis & 0xFF) == 1) // vertical
                {
                    if (rotationAngle == 0 || rotationAngle == 2)
                        oldRow = height - oldRow - 1;
                    else
                        oldCol = height - oldCol - 1;
                }
                else if ((imageMirrorAxis & 0xFF) == 2) // horizontal
                {
                    if (rotationAngle == 0 || rotationAngle == 2)
                        oldCol = width - oldCol - 1;
                    else
                        oldRow = width - oldRow - 1;
                }

                rotated[(int) (newCol)][(int) (newRow)][0] = pixels[(int) (oldCol)][(int) (oldRow)][0];
                rotated[(int) (newCol)][(int) (newRow)][1] = pixels[(int) (oldCol)][(int) (oldRow)][1];
                rotated[(int) (newCol)][(int) (newRow)][2] = pixels[(int) (oldCol)][(int) (oldRow)][2];
                rotated[(int) (newCol)][(int) (newRow)][3] = pixels[(int) (oldCol)][(int) (oldRow)][3];
            }
        }
        return rotated;
    }

    private void applyPixelFormat(byte[][][] rgba, PixelFormat pixelFormat)
    {
        if (pixelFormat == PixelFormat.Rgba32 || pixelFormat == PixelFormat.Rgb24)
        {
            return;
        }

        byte r, g, b, a;

        final long height = getHeight() & 0xFFFFFFFFL;
        final long width = getWidth() & 0xFFFFFFFFL;
        for (/*UInt32*/long row = 0; row < height; row++)
        {
            for (/*UInt32*/long col = 0; col < width; col++)
            {
                byte[] bytesPtr = rgba[(int) (col)][(int) (row)];
                r = bytesPtr[0];
                g = bytesPtr[1];
                b = bytesPtr[2];
                a = bytesPtr[3];

                switch (pixelFormat)
                {
                    case Argb32:
                        bytesPtr[0] = a;
                        bytesPtr[1] = r;
                        bytesPtr[2] = g;
                        bytesPtr[3] = b;
                        break;
                    case Bgra32:
                        bytesPtr[0] = b;
                        //bytesPtr[1] = g;
                        bytesPtr[2] = r;
                        //bytesPtr[3] = a;
                        break;
                }
            }
        }
    }

    private void loadHvcRawPixels()
    {
        stream.setCurrentImageId(id);
        stream.setBytePosition(((locationBox.base_offset & 0xFFFFFFFFL) + (locationBox.extents[0].offset & 0xFFFFFFFFL)) & 0xFFFFFFFFL);

        /*UInt32*/long end = ((((locationBox.base_offset & 0xFFFFFFFFL)
            + (locationBox.extents[0].offset & 0xFFFFFFFFL)) & 0xFFFFFFFFL)
            + (locationBox.extents[0].length & 0xFFFFFFFFL)) & 0xFFFFFFFFL;

        while (stream.getBitPosition()/8 < (end & 0xFFFFFFFFL))
        {
            NalUnit.parseUnit(stream);
        }

        if (stream.getContext().getPictures().containsKey(id))
        {
            rawPixels = stream.getContext().getPictures().get(id).pixels;
            rawPixelsHighColorRange = stream.getContext().getPictures().get(id).pixels_high_color_range;
        }
        else
        {
            rawPixels = null;
            rawPixelsHighColorRange = null;
            throw new NoSuchElementException(String.format("Image #%d was not loaded [NalUnit not found].", id));
        }

        cashed = true;
        stream.deleteImageContext(id);
    }

    private byte[][][] getByteArrayForGrid(byte[] databox)
    {
        int gridFieldLength = (((databox[1] & 0xFF) & 1) + 1) * 2;

        
        int rows = databox[2] & 0xFF;
        
        int columns = databox[3] & 0xFF;

        /*UInt32*/
        long localWidth = getByteFromIdatField(databox, 4, gridFieldLength) & 0xFFFFFFFFL;
        /*UInt32*/
        long localHeight = getByteFromIdatField(databox, 4 + gridFieldLength, gridFieldLength) & 0xFFFFFFFFL;

        int index;
        /*UInt32*/
        long[] derived = parent.getHeader().getDerivedList(id);

        HeicImageFrame frame;
        
        byte[][][] framePixels;
        
        byte[][][] output = new byte[(int) localWidth][(int) localHeight][4];

        final Map<Long, HeicImageFrame> allFrames = parent.getAllFrames();
        for (int i = 0; i <= rows; i++)
        {
            for (int j = 0; j <= columns; j++)
            {
                index = i * (columns + 1) + j;
                frame = allFrames.get(derived[index]);
                final long frameWidth = frame.getWidth();
                final long frameHeight = frame.getHeight();
                framePixels = frame.getMultidimArray(new Rectangle(0, 0, (int) frameWidth, (int) frameHeight));

                for (int k = 0; k < (frameHeight & 0xFFFFFFFFL); k++)
                {
                    for (int l = 0; l < (frameWidth & 0xFFFFFFFFL); l++)
                    {
                        if (((j * frameWidth) & 0xFFFFFFFFL) + l < localWidth
                                && ((i * frameHeight) & 0xFFFFFFFFL) + k < localHeight)
                        {
                            output[(int) (((j * frameWidth) & 0xFFFFFFFFL) + l)][(int) (((i * frameHeight) & 0xFFFFFFFFL) + k)][0] = framePixels[l][k][0];
                            output[(int) (((j * frameWidth) & 0xFFFFFFFFL) + l)][(int) (((i * frameHeight) & 0xFFFFFFFFL) + k)][1] = framePixels[l][k][1];
                            output[(int) (((j * frameWidth) & 0xFFFFFFFFL) + l)][(int) (((i * frameHeight) & 0xFFFFFFFFL) + k)][2] = framePixels[l][k][2];
                            output[(int) (((j * frameWidth) & 0xFFFFFFFFL) + l)][(int) (((i * frameHeight) & 0xFFFFFFFFL) + k)][3] = framePixels[l][k][3];
                        }
                    }
                }
            }
        }
        return output;
    }

    private byte[][][] getByteArrayForOverlay(byte[] databox)
    {
        /*UInt32*/
        long[] derived = parent.getHeader().getDerivedList(id);

        int iovlFieldLength = (((databox[1] & 0xFF) & 1) + 1) * 2;
        int index = 2;

        /*UInt16*/
        //    int[] canvas_fill_value = new /*UInt16*/int[4];
        for (int j = 0; j < 4; j++)
        {
     //       canvas_fill_value[j] = getByteFromIdatField(databox, index, 2) & 0xFFFF;
            index += 2;
        }

        /*UInt32*/
        long outputWidth = getByteFromIdatField(databox, index, iovlFieldLength) & 0xFFFFFFFFL;
        /*UInt32*/
        long outputHeight = getByteFromIdatField(databox, index + iovlFieldLength, iovlFieldLength) & 0xFFFFFFFFL;
        index += 2 * iovlFieldLength;

        int[] horizontal_offset = new int[derived.length];
        int[] vertical_offset = new int[derived.length];

        for (int i = 0; i < derived.length; i++)
        {
            horizontal_offset[i] = getByteFromIdatField(databox, index, iovlFieldLength);
            vertical_offset[i] = getByteFromIdatField(databox, index + iovlFieldLength, iovlFieldLength);
            index += 2 * iovlFieldLength;
        }

        HeicImageFrame frame;
        
        byte[][][] framePixels;
        
        byte[][][] output = new byte[(int) outputWidth][(int) outputHeight][4];

        final long height = getHeight() & 0xFFFFFFFFL;
        final long width = getWidth() & 0xFFFFFFFFL;
        for (/*UInt32*/long row = 0; (row & 0xFFFFFFFFL) < height; row++)
        {
            for (/*UInt32*/long col = 0; (col & 0xFFFFFFFFL) < width; col++)
            {
                output[(int) (col)][(int) (row)][3] = (byte) 255;
            }
        }

        for (int i = 0; i < derived.length; i++)
        {
            frame = parent.getAllFrames().get(derived[i]);
            final long frameWidth = frame.getWidth();
            final long frameHeight = frame.getHeight();
            framePixels = frame.getMultidimArray(new Rectangle(0, 0, (int) frameWidth, (int) frameHeight));

            for (int k = 0; k < frameHeight; k++)
            {
                for (int l = 0; l < frameWidth; l++)
                {
                    if (horizontal_offset[i] + l < outputWidth && vertical_offset[i] + k < outputHeight)
                    {
                        output[horizontal_offset[i] + l][vertical_offset[i] + k][0] = framePixels[l][k][0];
                        output[horizontal_offset[i] + l][vertical_offset[i] + k][1] = framePixels[l][k][1];
                        output[horizontal_offset[i] + l][vertical_offset[i] + k][2] = framePixels[l][k][2];
                        output[horizontal_offset[i] + l][vertical_offset[i] + k][3] = framePixels[l][k][3];
                    }
                }
            }
        }
        return output;
    }

    private int getByteFromIdatField(byte[] arr, int offset, int length)
    {
        int value = arr[offset] & 0xFF;

        for (int i = 1; i < length; i++)
        {
            value = (value << 8) | (arr[offset + i] & 0xFF);
        }

        return value;
    }

    @Override
    public String toString()
    {
        StringBuilder line = new StringBuilder(255);
        line.append('[').append(id).append(',').append(this.imageType).append("] ");

        if (colr != null)
            line.append(colr.getColourType()).append(' ');

        final byte[] bitsPerChannel1 = getBitsPerChannel();
        if (bitsPerChannel1 != null)
            line.append('(').append(Arrays.toString(bitsPerChannel1)).append(") ");

        if (ispeWidth > 0)
            line.append(ispeWidth).append('x').append(ispeHeight).append(' ');

        if (imageRotationAngle > 0)
            line.append(imageRotationAngle*90).append("° ");

        if (auxiliaryReferenceType != AuxiliaryReferenceType.Undefined)
            line.append(auxiliaryReferenceType).append(' ');

        if (derivativeType != null)
        {
            switch (derivativeType)
            {
                case dimg:
                    line.append("Derived: ");
                    break;
                case auxl:
                    line.append("Auxiliary: ");
                    break;
                case cdsc:
                    line.append("Content desc.: ");
                    break;
                case thmb:
                    line.append("Thumbnail: ");
                    break;
            }

            long[] derived = parent.getHeader().getDerivedList(id);
            line.append(Arrays.toString(derived));
        }

        return line.toString();
    }
}
