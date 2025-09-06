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

import openize.MathUtils;


/**
 * <p>
 * Class used to convert colors from YUV colorspace to RGB.
 * </p>
 */
class YuvConverter
{
    /**
     * <p>
     * Full or TV range flag.
     * </p>
     */
    private final boolean fullRangeFlag;

    /**
     * <p>
     * Coded image.
     * </p>
     */
    private final HeicImageFrame picture;

    private double cooffCrToR;
    private double cooffCrToG;
    private double cooffCbToG;
    private double cooffCbToB;

    public YuvConverter(HeicImageFrame picture)
    {
        fullRangeFlag = picture.hvcConfig.getSPS().vui_parameters.video_full_range_flag;

        this.picture = picture;

        defineCoefficients(picture.hvcConfig.getSPS().vui_parameters);
    }


    // 8 bit!

    /**
     * <p>
     * Convert YUV byte array to RGBA.
     * </p>
     *
     * @return RGBA byte array.
     */
    public final byte[][][] getRgbaByteArray()
    {
        /*UInt32*/
        int width = (int) (picture.hvcConfig.getSPS().pic_width_in_luma_samples & 0xFFFFFFFFL);
        /*UInt32*/
        int height = (int) (picture.hvcConfig.getSPS().pic_height_in_luma_samples & 0xFFFFFFFFL);

        byte[][][] pixels = new byte[width][height][4];

        if (picture.rawPixels == null && picture.rawPixelsHighColorRange == null)
            return pixels;

        double Y, Cr, Cb, R, G, B;
        double[] rgb = new double[3];

        int chromaHalfRange = 1 << ((picture.hvcConfig.getSPS().getBitDepthC() & 0xFF) - 1);
        int lumaOffset = 16 << ((picture.hvcConfig.getSPS().getBitDepthY() & 0xFF) - 8);

        double tvRangeCoeffLuma = 255.0 / 219;
        double tvRangeCoeffChroma = 255.0 / 224;

        // conversion to 8 bit
        double BitKoefY = 256.0 / (1 << (picture.hvcConfig.getSPS().getBitDepthY() & 0xFF));
        double BitKoefC = 256.0 / (1 << (picture.hvcConfig.getSPS().getBitDepthC() & 0xFF));
        final boolean isNotChroma = (picture.hvcConfig.getSPS()
                                                      .getChromaArrayType() & 0xFFFFFFFFL) == 0;
        final int configSpsSubWidthC = picture.hvcConfig.getSPS()
                                                        .getSubWidthC() & 0xFF;
        final int configSpsSubHeightC = picture.hvcConfig.getSPS()
                                                         .getSubHeightC() & 0xFF;

        boolean highColorRange = (picture.hvcConfig.getSPS().getBitDepthY() & 0xFF) > 8 || (picture.hvcConfig.getSPS().getBitDepthC() & 0xFF) > 8;

        for (int row = 0; row < height; row++)
        {
            for (int col = 0; col < width; col++)
            {
                if (highColorRange)
                    Y = picture.rawPixelsHighColorRange[0][col][row] & 0xFFFF;
                else
                    Y = picture.rawPixels[0][col][row] & 0xFF;

                if (!fullRangeFlag)
                {
                    Y = tvRangeCoeffLuma * (Y - lumaOffset);
                }

                if (isNotChroma)
                {
                    R = Y;
                    G = Y;
                    B = Y;
                }
                else
                {
                    if (highColorRange)
                    {
                        Cb = (picture.rawPixelsHighColorRange[1][col / (picture.hvcConfig.getSPS().getSubWidthC() & 0xFF)][row / (picture.hvcConfig.getSPS().getSubHeightC() & 0xFF)] & 0xFFFF) - chromaHalfRange;
                        Cr = (picture.rawPixelsHighColorRange[2][col / (picture.hvcConfig.getSPS().getSubWidthC() & 0xFF)][row / (picture.hvcConfig.getSPS().getSubHeightC() & 0xFF)] & 0xFFFF) - chromaHalfRange;
                    }
                    else
                    {
                        Cb = (picture.rawPixels[1][col / (picture.hvcConfig.getSPS().getSubWidthC() & 0xFF)][row / (picture.hvcConfig.getSPS().getSubHeightC() & 0xFF)] & 0xFF) - chromaHalfRange;
                        Cr = (picture.rawPixels[2][col / (picture.hvcConfig.getSPS().getSubWidthC() & 0xFF)][row / (picture.hvcConfig.getSPS().getSubHeightC() & 0xFF)] & 0xFF) - chromaHalfRange;
                    }

                    if (!fullRangeFlag)
                    {
                        Cb = tvRangeCoeffChroma * Cb;
                        Cr = tvRangeCoeffChroma * Cr;
                    }

                    YCrCb2RGB(Y * BitKoefY, Cb * BitKoefC, Cr * BitKoefC, rgb);
                    R = rgb[0];
                    G = rgb[1];
                    B = rgb[2];
                }

                pixels[col][row][0] = (byte) MathExtra.clip3(0, 255, MathUtils.f64_s32(R));
                pixels[col][row][1] = (byte) MathExtra.clip3(0, 255, MathUtils.f64_s32(G));
                pixels[col][row][2] = (byte) MathExtra.clip3(0, 255, MathUtils.f64_s32(B));
                pixels[col][row][3] = (byte) 255;
            }
        }

        return pixels;
    }

    /// <summary>
    /// Calculate RGB values from YCrCb based on curred converter settings.
    /// </summary>
    /// <param name="Y">Luma value.</param>
    /// <param name="Cb">Chroma blue value.</param>
    /// <param name="Cr">Chroma red value.</param>
    /// <returns>Red, Green and Blue values.</returns>
    private void YCrCb2RGB(double Y, double Cb, double Cr, double[] outRef)
    {
        outRef[0] = Y + cooffCrToR * Cr;
        outRef[1] = Y - cooffCrToG * Cr - cooffCbToG * Cb;
        outRef[2] = Y + cooffCbToB * Cb;
    }

    /**
     * <p>
     * Define converter coefficients based on parameters of image meta data.
     * </p>
     *
     * @param vui_parameters Image usability information data.
     */
    private void defineCoefficients(vui_parameters vui_parameters)
    {
        //vui_parameters.video_full_range_flag;
        //vui_parameters.colour_primaries;
        //vui_parameters.transfer_characteristics;
        //vui_parameters.matrix_coeffs;

        double kr;
        double kb;
        switch (vui_parameters.matrix_coeffs)
        {
            case 1:
                kr = 0.2126;
                kb = 0.0722;
                break;
            case 4:
                kr = 0.30;
                kb = 0.11;
                break;
            case 5:
            case 6:
                kr = 0.299;
                kb = 0.114;
                break;
            case 7:
                kr = 0.212;
                kb = 0.087;
                break;
            case 9:
            case 10:
                kr = 0.2627;
                kb = 0.0593;
                break;

            case 12:
            case 13:
                ColorPrimaries p = ColorPrimaries.getSpecified(vui_parameters.colour_primaries & 0xFF);

                double zR = 1 - (p.xR + p.yR);
                double zG = 1 - (p.xG + p.yG);
                double zB = 1 - (p.xB + p.yB);
                double zW = 1 - (p.xW + p.yW);

                final double tmpGmB_BmG = p.yG * zB - p.yB * zG;
                final double tmpRmG_GmR = p.yR * zG - p.yG * zR;
                double denom = p.yW * (p.xR * tmpGmB_BmG + p.xG * (p.yB * zR - p.yR * zB) + p.xB * tmpRmG_GmR);

                try
                {
                    kr = (p.yR * (p.xW * tmpGmB_BmG + p.yW * (p.xB * zG - p.xG * zB) + zW * (p.xG * p.yB - p.xB * p.yG))) / denom;
                    kb = (p.yB * (p.xW * tmpRmG_GmR + p.yW * (p.xG * zR - p.xR * zG) + zW * (p.xR * p.yG - p.xG * p.yR))) / denom;
                }
                catch (java.lang.RuntimeException e)
                {
                    kr = 0.299;
                    kb = 0.114;
                }
                break;

            case 0:  // Identity; See Equations E-31 to E-33
            case 2:  // Unspecified
            case 3:  // Reserved
            case 8:  // YCgCo;    See Equations E-28 to E-30
            case 11: // Y′D′ZD′X; See Equations E-59 to E-61
            case 14: // ICTCP
            default:
                kr = 0.299;
                kb = 0.114;
                break;

        }

        double kg = 1 - kr - kb;

        cooffCrToR = 2 * (1 - kr);
        cooffCrToG = 2 * kr * (1 - kr) / kg;
        cooffCbToG = 2 * kb * (1 - kb) / kg;
        cooffCbToB = 2 * (1 - kb);
    }


    static class ColorPrimaries
    {
        public final double xG;
        public final double yG;
        public final double xB;
        public final double yB;
        public final double xR;
        public final double yR;
        public final double xW;
        public final double yW;

        public ColorPrimaries(double xG, double yG, double xB, double yB, double xR, double yR, double xW, double yW)
        {
            this.xG = xG;
            this.yG = yG;
            this.xB = xB;
            this.yB = yB;
            this.xR = xR;
            this.yR = yR;
            this.xW = xW;
            this.yW = yW;
        }

        public static ColorPrimaries getSpecified(int colourPrimaries)
        {
            switch (colourPrimaries)
            {
                case 1:
                    return new ColorPrimaries(0.300, 0.600, 0.150, 0.060, 0.640, 0.330, 0.3127, 0.3290);
                case 4:
                    return new ColorPrimaries(0.210, 0.710, 0.140, 0.080, 0.670, 0.330, 0.3100, 0.3160);
                case 5:
                    return new ColorPrimaries(0.290, 0.600, 0.150, 0.060, 0.640, 0.330, 0.3127, 0.3290);
                case 6:
                case 7:
                    return new ColorPrimaries(0.310, 0.595, 0.155, 0.070, 0.630, 0.340, 0.3127, 0.3290);
                case 8:
                    return new ColorPrimaries(0.243, 0.692, 0.145, 0.049, 0.681, 0.319, 0.3100, 0.3160);
                case 9:
                    return new ColorPrimaries(0.170, 0.797, 0.131, 0.046, 0.708, 0.292, 0.3127, 0.3290);
                case 10:
                    return new ColorPrimaries(0.000, 1.000, 0.000, 0.000, 1.000, 0.000, 0.333333, 0.33333);
                case 11:
                    return new ColorPrimaries(0.265, 0.690, 0.150, 0.060, 0.680, 0.320, 0.3140, 0.3510);
                case 12:
                    return new ColorPrimaries(0.265, 0.690, 0.150, 0.060, 0.680, 0.320, 0.3127, 0.3290);
                case 22:
                    return new ColorPrimaries(0.295, 0.605, 0.155, 0.077, 0.630, 0.340, 0.3127, 0.3290);
                case 2:  // Unspecified
                default: // Reserved
                    throw new UnsupportedOperationException("Unsupported colourPrimaries = " + colourPrimaries);
            }
        }
    }
}

