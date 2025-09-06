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


class NeighbouringSamplesGenerator
{
    private final HeicPicture picture;
    private final int x0;
    private final int y0;
    private final int nTbS;
    private final int cIdx;
    private final int ctbLog2Size;
    private final /*UInt32*/ long picWidthInCtbs;
    private final int SubWidth;
    private final int SubHeight;
    private final int xBLuma;
    private final int yBLuma;
    private boolean availableLeft;
    private boolean availableTop;
    private boolean availableTopRight;
    private boolean availableTopLeft;
    private int currCTBSlice;
    private int currCTBTileId;

    private /*UInt16*/ int firstValue;
    //int nBottom;
    //int nRight;
    //int nAvail;

    private NeighbouringSamplesGenerator(HeicPicture picture, int x0, int y0, int nTbS, int cIdx)
    {
        this.picture = picture;
        this.x0 = x0;
        this.y0 = y0;
        this.nTbS = nTbS;
        this.cIdx = cIdx;

        ctbLog2Size = picture.sps.getCtbLog2SizeY() & 0xFF;
        picWidthInCtbs = picture.sps.getPicWidthInCtbsY();

//        if (cIdx > 0)
//        {
//
//            if ((picture.sps.getChromaArrayType() & 0xFFFFFFFFL) == 1)// for 420
//            {
//                //ctbLog2Size = picture.sps.CtbLog2SizeY * picture.sps.SubWidthC;
//            }
//        }

        SubWidth = (cIdx == 0) ? 1 : picture.sps.getSubWidthC();
        SubHeight = (cIdx == 0) ? 1 : picture.sps.getSubHeightC();

        xBLuma = this.x0 * SubWidth;
        yBLuma = this.y0 * SubHeight;
    }

    public static NeighbouringSamples generate(HeicPicture picture, int x0, int y0, int nTbS, int cIdx)
    {
        NeighbouringSamplesGenerator gen = new NeighbouringSamplesGenerator(picture, x0, y0, nTbS, cIdx);
        gen.calculateFlags();
        return gen.fillNeighbouringSamples();
    }

    private void calculateFlags()
    {
        availableLeft = true;
        availableTop = true;
        availableTopRight = true;
        availableTopLeft = true;

        if (xBLuma == 0)
        {
            availableLeft = false;
            availableTopLeft = false;
        }

        if (yBLuma == 0)
        {
            availableTop = false;
            availableTopLeft = false;
            availableTopRight = false;
        }

        if (xBLuma + (long) nTbS * SubWidth >= (picture.sps.pic_width_in_luma_samples & 0xFFFFFFFFL))
        {
            availableTopRight = false;
        }

        // check for tile and slice boundaries

        int xCurrCtb = xBLuma >> ctbLog2Size;
        int yCurrCtb = yBLuma >> ctbLog2Size;
        int xLeftCtb = (xBLuma - 1) >> ctbLog2Size;
        int xRightCtb = (xBLuma + nTbS * SubWidth) >> ctbLog2Size;
        int yTopCtb = (yBLuma - 1) >> ctbLog2Size;

        currCTBSlice = picture.SliceAddrRs[xCurrCtb][yCurrCtb];
        currCTBTileId = (int) picture.pps.TileIdFromRs[(int) (xCurrCtb + ((yCurrCtb * (picWidthInCtbs & 0xFFFFFFFFL)) & 0xFFFFFFFFL))];

        availableLeft = reCheckFlag(availableLeft, xLeftCtb, yCurrCtb);
        availableTop = reCheckFlag(availableTop, xCurrCtb, yTopCtb);
        availableTopRight = reCheckFlag(availableTopRight, xRightCtb, yTopCtb);
        availableTopLeft = reCheckFlag(availableTopLeft, xLeftCtb, yTopCtb);
    }

    private boolean reCheckFlag(boolean available, int x, int y)
    {
        if (available)
        {
            if (currCTBSlice != (picture.SliceAddrRs[x][y] & 0xFFFFFFFFL) ||
                    currCTBTileId != (picture.pps.TileIdFromRs[(int) (x + ((y * (picWidthInCtbs & 0xFFFFFFFFL)) & 0xFFFFFFFFL))] & 0xFFFFFFFFL))
            {
                return false;
            }
        }

        return available;
    }

    private NeighbouringSamples fillNeighbouringSamples()
    {
        int nBottom = (int) picture.sps.pic_height_in_luma_samples - y0 * SubHeight;
        nBottom = (nBottom + SubHeight - 1) / SubHeight;
        if (nBottom > 2 * nTbS)
        {
            nBottom = 2 * nTbS;
        }

        int nRight = (int) picture.sps.pic_width_in_luma_samples - x0 * SubWidth;
        nRight = (nRight + SubWidth - 1) / SubWidth;
        if (nRight > 2 * nTbS)
        {
            nRight = 2 * nTbS;
        }

        int nAvail = 0;
        boolean availableN;

        /*UInt32*/
        long currBlockAddr = picture.pps.MinTbAddrZs[
                xBLuma >> (picture.sps.getMinTbLog2SizeY() & 0xFF)][yBLuma >> (picture.sps.getMinTbLog2SizeY() & 0xFF)] & 0xFFFFFFFFL;


        NeighbouringSamples samples = new NeighbouringSamples(nTbS);
        NeighbouringSamplesAvailability available = new NeighbouringSamplesAvailability(nTbS);

        if (availableLeft)
        {
            for (int y = nBottom - 1; y >= 0; y -= 4)
            {
                /*UInt32*/
                long NBlockAddr = picture.pps.MinTbAddrZs[
                        ((x0 - 1) * SubWidth) >> (picture.sps.getMinTbLog2SizeY() & 0xFF)][((y0 + y) * SubHeight) >> (picture.sps.getMinTbLog2SizeY() & 0xFF)] & 0xFFFFFFFFL;

                availableN = (NBlockAddr & 0xFFFFFFFFL) <= (currBlockAddr & 0xFFFFFFFFL);

                if (picture.pps.constrained_intra_pred_flag)
                {
                    if (picture.CuPredMode[(x0 - 1) * SubWidth][(y0 + y) * SubHeight] != PredMode.MODE_INTRA)
                    {
                        availableN = false;
                    }
                }

                if (availableN)
                {
                    if ((picture.sps.getBitDepthY() & 0xFF) > 8 || (picture.sps.getBitDepthC() & 0xFF) > 8)
                    {
                        if (nAvail == 0)
                            firstValue = picture.pixels_high_color_range[cIdx][x0 - 1][y0 + y];

                        for (int i = 0; i < 4; i++)
                        {
                            available.set(-1, y - i, true);
                            samples.set(-1, y - i, picture.pixels_high_color_range[cIdx][x0 - 1][y0 + y - i]);
                            nAvail++;
                        }
                    }
                    else
                    {
                        if (nAvail == 0)
                            firstValue = picture.pixels[cIdx][x0 - 1][y0 + y] & 0xFF;

                        for (int i = 0; i < 4; i++)
                        {
                            available.set(-1, y - i, true);
                            samples.set(-1, y - i, picture.pixels[cIdx][x0 - 1][y0 + y - i] & 0xFF);
                            nAvail++;
                        }
                    }
                }
            }
        }

        if (availableTopLeft)
        {
            /*UInt32*/
            long NBlockAddr = picture.pps.MinTbAddrZs[
                    ((x0 - 1) * SubWidth) >> (picture.sps.getMinTbLog2SizeY() & 0xFF)][((y0 - 1) * SubHeight) >> (picture.sps.getMinTbLog2SizeY() & 0xFF)] & 0xFFFFFFFFL;

            availableN = (NBlockAddr & 0xFFFFFFFFL) <= (currBlockAddr & 0xFFFFFFFFL);

            if (picture.pps.constrained_intra_pred_flag)
            {
                if (picture.CuPredMode[(x0 - 1) * SubWidth][(y0 - 1) * SubHeight] != PredMode.MODE_INTRA)
                {
                    availableN = false;
                }
            }

            if (availableN)
            {
                if ((picture.sps.getBitDepthY() & 0xFF) > 8 || (picture.sps.getBitDepthC() & 0xFF) > 8)
                {
                    if (nAvail == 0)
                        firstValue = picture.pixels_high_color_range[cIdx][x0 - 1][y0 - 1];

                    available.set(-1, -1, true);
                    samples.set(-1, -1, picture.pixels_high_color_range[cIdx][x0 - 1][y0 - 1]);
                    nAvail++;
                }
                else
                {
                    if (nAvail == 0)
                        firstValue = picture.pixels[cIdx][x0 - 1][y0 - 1] & 0xFF;

                    available.set(-1, -1, true);
                    samples.set(-1, -1, picture.pixels[cIdx][x0 - 1][y0 - 1] & 0xFF);
                    nAvail++;
                }
            }
        }

        for (int x = 0; x < nRight; x += 4)
        {
            if (x < nTbS ? availableTop : availableTopRight)
            {
                /*UInt32*/
                long NBlockAddr = picture.pps.MinTbAddrZs[
                        ((x0 + x) * SubWidth) >> (picture.sps.getMinTbLog2SizeY() & 0xFF)][((y0 - 1) * SubHeight) >> (picture.sps.getMinTbLog2SizeY() & 0xFF)] & 0xFFFFFFFFL;

                availableN = (NBlockAddr & 0xFFFFFFFFL) <= (currBlockAddr & 0xFFFFFFFFL);

                if (picture.pps.constrained_intra_pred_flag)
                {
                    if (picture.CuPredMode[(x0 + x) * SubWidth][(y0 - 1) * SubHeight] != PredMode.MODE_INTRA)
                    {
                        availableN = false;
                    }
                }

                if (availableN)
                {
                    if ((picture.sps.getBitDepthY() & 0xFF) > 8 || (picture.sps.getBitDepthC() & 0xFF) > 8)
                    {
                        if (nAvail == 0) firstValue = picture.pixels_high_color_range[cIdx][x0 + x][y0 - 1];

                        for (int i = 0; i < 4; i++)
                        {
                            available.set(x + i, -1, true);
                            samples.set(x + i, -1, picture.pixels_high_color_range[cIdx][x0 + x + i][y0 - 1]);
                            nAvail++;
                        }
                    }
                    else
                    {
                        if (nAvail == 0) firstValue = picture.pixels[cIdx][x0 + x][y0 - 1] & 0xFF;

                        for (int i = 0; i < 4; i++)
                        {
                            available.set(x + i, -1, true);
                            samples.set(x + i, -1, picture.pixels[cIdx][x0 + x + i][y0 - 1] & 0xFF);
                            nAvail++;
                        }
                    }
                }
            }
        }


        if (nAvail != 4 * nTbS + 1)
        {
            if (nAvail == 0)
            {
                samples.reset((cIdx == 0 ?
                        picture.sps.getBitDepthY() :
                        picture.sps.getBitDepthC()) & 0xFF);
            }
            else
            {
                if (!available.get(-1, 2 * nTbS - 1))
                {
                    samples.set(-1, 2 * nTbS - 1, firstValue);
                }

                for (int i = 2 * nTbS - 2; i >= -1; i--)
                    if (!available.get(-1, i))
                    {
                        samples.set(-1, i, samples.get(-1, i + 1));
                    }

                for (int i = 0; i <= 2 * nTbS - 1; i++)
                    if (!available.get(i, -1))
                    {
                        samples.set(i, -1, samples.get(i - 1, -1));
                    }
            }
        }

        return samples;
    }
}
