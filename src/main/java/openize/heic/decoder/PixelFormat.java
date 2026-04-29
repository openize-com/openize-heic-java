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

/**
 * <p>
 * Specifies the format of the color data for each pixel in the image.
 * </p>
 */
public enum PixelFormat
{
    /**
     * <p>
     * Specifies that the format is 24 bits per pixel; 8 bits each are used for the red, green, and blue components.
     * </p>
     */
    Rgb24,

    /**
     * <p>
     * Specifies that the format is 32 bits per pixel; 8 bits each are used for the red, green, blue, and alpha components.
     * </p>
     */
    Rgba32,

    /**
     * <p>
     * Specifies that the format is 32 bits per pixel; 8 bits each are used for the alpha, red, green, and blue components.
     * </p>
     */
    Argb32,

    /**
     * <p>
     * Specifies that the format is 32 bits per pixel; 8 bits each are used for the blue, green, red, and alpha components.
     * </p>
     */
    Bgra32,
}
