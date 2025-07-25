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


/**
 * <p>
 * Type of an image frame.
 * </p>
 */
public enum ImageFrameType /*: uint*/
{
    /**
     * <p>
     * HEVC coded image frame.
     * </p>
     */
    hvc1(0x68766331L),

    /**
     * <p>
     * Identity transformation.
     * Cropping and/or rotation by 90, 180, or 270 degrees, imposed through the respective transformative properties.
     * </p>
     */
    iden(0x6964656eL),

    /**
     * <p>
     * Image Overlay.
     * Overlaying any number of input images in indicated order and locations onto the canvas of the output image.
     * </p>
     */
    iovl(0x696f766cL),

    /**
     * <p>
     * Image Grid.
     * Reconstructing a grid of input images of the same width and height.
     * </p>
     */
    grid(0x67726964L),

    /**
     * <p>
     * Exif metadata.
     * Exchangeable image file format metadata.
     * </p>
     */
    Exif(0x45786966L),

    /**
     * <p>
     * MIME metadata.
     * Resource Description Framework metadata.
     * </p>
     */
    mime(0x6d696d65L);

    private final long code;

    ImageFrameType(long code)
    {
        this.code = code;
    }

    public static ImageFrameType codeToType(long code)
    {
        for (ImageFrameType type : _values)
        {
            if (type.code == code)
            {
                return type;
            }
        }

        throw new IllegalArgumentException("code is incorrect " + code);
    }

    public long getCode()
    {
        return code;
    }

    private static final ImageFrameType[] _values = ImageFrameType.values();
}
