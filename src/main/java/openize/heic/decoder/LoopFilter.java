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
 * In-loop filters applied to a fully reconstructed intra picture: the deblocking
 * filter (ITU-T H.265 8.7.2) followed by sample adaptive offset (8.7.3).
 *
 * Only intra pictures are decoded, so every coding block is intra and the boundary
 * filtering strength of every transform block edge is 2.
 */
final class LoopFilter
{
    static final byte VERTICAL_EDGE = 1;
    static final byte HORIZONTAL_EDGE = 2;
    static final byte NO_FILTER = 4;

    private static final int INTRA_BS = 2;

    // Table 8-12, indexed by Q
    private static final int[] BETA_TABLE = {
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 20, 22, 24,
        26, 28, 30, 32, 34, 36, 38, 40, 42, 44, 46, 48, 50, 52, 54, 56, 58, 60, 62, 64
    };

    private static final int[] TC_TABLE = {
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 3,
        3, 3, 3, 4, 4, 4, 5, 5, 6, 6, 7, 8, 9, 10, 11, 13, 14, 16, 18, 20, 22, 24
    };

    // Table 8-10, qPi 30..43
    private static final int[] CHROMA_QP_TABLE = { 29, 30, 31, 32, 33, 33, 34, 34, 35, 35, 36, 36, 37, 37 };

    // Table 8-13, edge offset neighbour positions per SaoEoClass
    private static final int[][] SAO_H_POS = { { -1, 1 }, { 0, 0 }, { -1, 1 }, { 1, -1 } };
    private static final int[][] SAO_V_POS = { { 0, 0 }, { -1, 1 }, { -1, 1 }, { -1, 1 } };

    private final HeicPicture picture;
    private final seq_parameter_set_rbsp sps;
    private final pic_parameter_set_rbsp pps;
    private final int width;
    private final int height;
    private final int ctbLog2Size;
    private final int chromaCount;
    private final int subWidthC;
    private final int subHeightC;

    private byte[][][] pixels;
    private int[][][] pixelsHighColorRange;

    private LoopFilter(HeicPicture picture)
    {
        this.picture = picture;
        this.sps = picture.sps;
        this.pps = picture.pps;
        this.width = (int) sps.pic_width_in_luma_samples;
        this.height = (int) sps.pic_height_in_luma_samples;
        this.ctbLog2Size = sps.getCtbLog2SizeY() & 0xFF;
        this.chromaCount = sps.getChromaArrayType() != 0 ? 3 : 1;
        this.subWidthC = sps.getSubWidthC() & 0xFF;
        this.subHeightC = sps.getSubHeightC() & 0xFF;
        this.pixels = picture.pixels;
        this.pixelsHighColorRange = picture.pixels_high_color_range;
    }

    static void apply(HeicPicture picture)
    {
        if (picture.blockFlags == null || picture.getSliceHeaders().isEmpty())
            return;

        LoopFilter filter = new LoopFilter(picture);

        boolean deblock = false;
        boolean sao = false;
        for (slice_segment_header header : picture.getSliceHeaders().values())
        {
            deblock |= !header.slice_deblocking_filter_disabled_flag;
            sao |= header.slice_sao_luma_flag || header.slice_sao_chroma_flag;
        }

        if (deblock)
        {
            filter.deblock(true);
            filter.deblock(false);
        }

        if (sao && picture.sps.sample_adaptive_offset_enabled_flag)
            filter.sampleAdaptiveOffset();
    }

    // 8.7.2 Deblocking filter process: all vertical edges of the picture, then all horizontal ones

    private void deblock(boolean vertical)
    {
        byte edgeFlag = vertical ? VERTICAL_EDGE : HORIZONTAL_EDGE;
        int nx = vertical ? 1 : 0;
        int ny = vertical ? 0 : 1;
        int qpBdOffsetY = sps.getQpBdOffsetY() & 0xFF;

        // Luma: edges on the 8x8 grid, in segments of four lines
        for (int y = vertical ? 0 : 8; y < height; y += vertical ? 4 : 8)
        {
            for (int x = vertical ? 8 : 0; x < width; x += vertical ? 8 : 4)
            {
                if ((picture.blockFlags[x >> 2][y >> 2] & edgeFlag) == 0)
                    continue;

                int xp = x - nx;
                int yp = y - ny;
                slice_segment_header header = sliceHeaderAt(x, y);
                if (!filterEdge(header, x, y, xp, yp))
                    continue;

                int qpL = ((picture.QpY[x][y] - qpBdOffsetY) + (picture.QpY[xp][yp] - qpBdOffsetY) + 1) >> 1;
                filterLumaEdge(x, y, nx, ny, qpL, header, noFilter(xp, yp), noFilter(x, y));
            }
        }

        if (chromaCount == 1)
            return;

        // Chroma: edges on the 8x8 chroma sample grid, filtered line by line
        int widthC = width / subWidthC;
        int heightC = height / subHeightC;
        int bitDepthC = sps.getBitDepthC() & 0xFF;

        for (int cIdx = 1; cIdx < chromaCount; cIdx++)
        {
            int cQpPicOffset = cIdx == 1 ? pps.pps_cb_qp_offset : pps.pps_cr_qp_offset;

            for (int yc = vertical ? 0 : 8; yc < heightC; yc += vertical ? 1 : 8)
            {
                for (int xc = vertical ? 8 : 0; xc < widthC; xc += vertical ? 8 : 1)
                {
                    int x = xc * subWidthC;
                    int y = yc * subHeightC;
                    if ((picture.blockFlags[x >> 2][y >> 2] & edgeFlag) == 0)
                        continue;

                    int xp = x - nx;
                    int yp = y - ny;
                    slice_segment_header header = sliceHeaderAt(x, y);
                    if (!filterEdge(header, x, y, xp, yp))
                        continue;

                    int qpi = (((picture.QpY[x][y] - qpBdOffsetY) + (picture.QpY[xp][yp] - qpBdOffsetY) + 1) >> 1) + cQpPicOffset;
                    int qpC = sps.getChromaArrayType() == 1 ? chromaQp(qpi) : Math.min(qpi, 51);
                    int q = MathExtra.clip3(0, 53, qpC + 2 * (INTRA_BS - 1) + (header.slice_tc_offset_div2 << 1));
                    int tc = TC_TABLE[q] * (1 << (bitDepthC - 8));

                    filterChromaLine(cIdx, xc, yc, nx, ny, tc, bitDepthC, noFilter(xp, yp), noFilter(x, y));
                }
            }
        }
    }

    // filterEdgeFlag (8.7.2.3): picture borders are never reached by the loops above
    private boolean filterEdge(slice_segment_header header, int xq, int yq, int xp, int yp)
    {
        if (header.slice_deblocking_filter_disabled_flag)
            return false;

        int ctbQ = ctbAddrRs(xq, yq);
        int ctbP = ctbAddrRs(xp, yp);
        if (ctbQ == ctbP)
            return true;

        if (!pps.loop_filter_across_tiles_enabled_flag && tileId(ctbQ) != tileId(ctbP))
            return false;

        return header.slice_loop_filter_across_slices_enabled_flag || sliceAddr(xq, yq) == sliceAddr(xp, yp);
    }

    // 8.7.2.5.3 Decision process for luma block edges, 8.7.2.5.7 luma sample filtering
    private void filterLumaEdge(int x, int y, int nx, int ny, int qpL, slice_segment_header header,
                                boolean noFilterP, boolean noFilterQ)
    {
        int bitDepth = sps.getBitDepthY() & 0xFF;
        int beta = BETA_TABLE[MathExtra.clip3(0, 51, qpL + (header.slice_beta_offset_div2 << 1))] * (1 << (bitDepth - 8));
        int tc = TC_TABLE[MathExtra.clip3(0, 53, qpL + 2 * (INTRA_BS - 1) + (header.slice_tc_offset_div2 << 1))] * (1 << (bitDepth - 8));

        // along the edge: perpendicular to the normal
        int ax = ny;
        int ay = nx;

        int[][] p = new int[4][4];
        int[][] q = new int[4][4];
        for (int k = 0; k < 4; k++)
        {
            for (int i = 0; i < 4; i++)
            {
                p[k][i] = get(0, x - (i + 1) * nx + k * ax, y - (i + 1) * ny + k * ay);
                q[k][i] = get(0, x + i * nx + k * ax, y + i * ny + k * ay);
            }
        }

        int dp0 = Math.abs(p[0][2] - 2 * p[0][1] + p[0][0]);
        int dp3 = Math.abs(p[3][2] - 2 * p[3][1] + p[3][0]);
        int dq0 = Math.abs(q[0][2] - 2 * q[0][1] + q[0][0]);
        int dq3 = Math.abs(q[3][2] - 2 * q[3][1] + q[3][0]);
        int dpq0 = dp0 + dq0;
        int dpq3 = dp3 + dq3;
        int dp = dp0 + dp3;
        int dq = dq0 + dq3;
        int d = dpq0 + dpq3;

        if (d >= beta)
            return;

        boolean strong = strongDecision(p[0], q[0], 2 * dpq0, beta, tc) && strongDecision(p[3], q[3], 2 * dpq3, beta, tc);
        boolean dEp = dp < ((beta + (beta >> 1)) >> 3);
        boolean dEq = dq < ((beta + (beta >> 1)) >> 3);
        int maxValue = (1 << bitDepth) - 1;

        for (int k = 0; k < 4; k++)
        {
            int[] pk = p[k];
            int[] qk = q[k];
            int p0 = pk[0], p1 = pk[1], p2 = pk[2], p3 = pk[3];
            int q0 = qk[0], q1 = qk[1], q2 = qk[2], q3 = qk[3];
            int[] np = { p0, p1, p2 };
            int[] nq = { q0, q1, q2 };

            if (strong)
            {
                np[0] = MathExtra.clip3(p0 - 2 * tc, p0 + 2 * tc, (p2 + 2 * p1 + 2 * p0 + 2 * q0 + q1 + 4) >> 3);
                np[1] = MathExtra.clip3(p1 - 2 * tc, p1 + 2 * tc, (p2 + p1 + p0 + q0 + 2) >> 2);
                np[2] = MathExtra.clip3(p2 - 2 * tc, p2 + 2 * tc, (2 * p3 + 3 * p2 + p1 + p0 + q0 + 4) >> 3);
                nq[0] = MathExtra.clip3(q0 - 2 * tc, q0 + 2 * tc, (p1 + 2 * p0 + 2 * q0 + 2 * q1 + q2 + 4) >> 3);
                nq[1] = MathExtra.clip3(q1 - 2 * tc, q1 + 2 * tc, (p0 + q0 + q1 + q2 + 2) >> 2);
                nq[2] = MathExtra.clip3(q2 - 2 * tc, q2 + 2 * tc, (p0 + q0 + q1 + 3 * q2 + 2 * q3 + 4) >> 3);
            }
            else
            {
                int delta = (9 * (q0 - p0) - 3 * (q1 - p1) + 8) >> 4;
                if (Math.abs(delta) >= tc * 10)
                    continue;

                delta = MathExtra.clip3(-tc, tc, delta);
                np[0] = MathExtra.clip3(0, maxValue, p0 + delta);
                nq[0] = MathExtra.clip3(0, maxValue, q0 - delta);

                if (dEp)
                {
                    int deltaP = MathExtra.clip3(-(tc >> 1), tc >> 1, (((p2 + p0 + 1) >> 1) - p1 + delta) >> 1);
                    np[1] = MathExtra.clip3(0, maxValue, p1 + deltaP);
                }

                if (dEq)
                {
                    int deltaQ = MathExtra.clip3(-(tc >> 1), tc >> 1, (((q2 + q0 + 1) >> 1) - q1 - delta) >> 1);
                    nq[1] = MathExtra.clip3(0, maxValue, q1 + deltaQ);
                }
            }

            for (int i = 0; i < 3; i++)
            {
                if (!noFilterP)
                    set(0, x - (i + 1) * nx + k * ax, y - (i + 1) * ny + k * ay, np[i]);
                if (!noFilterQ)
                    set(0, x + i * nx + k * ax, y + i * ny + k * ay, nq[i]);
            }
        }
    }

    // 8.7.2.5.6 Decision process for a luma sample
    private static boolean strongDecision(int[] p, int[] q, int dpq, int beta, int tc)
    {
        return dpq < (beta >> 2) &&
            Math.abs(p[3] - p[0]) + Math.abs(q[0] - q[3]) < (beta >> 3) &&
            Math.abs(p[0] - q[0]) < ((5 * tc + 1) >> 1);
    }

    // 8.7.2.5.8 Filtering process for a chroma sample
    private void filterChromaLine(int cIdx, int xc, int yc, int nx, int ny, int tc, int bitDepth,
                                  boolean noFilterP, boolean noFilterQ)
    {
        int p0 = get(cIdx, xc - nx, yc - ny);
        int p1 = get(cIdx, xc - 2 * nx, yc - 2 * ny);
        int q0 = get(cIdx, xc, yc);
        int q1 = get(cIdx, xc + nx, yc + ny);

        int delta = MathExtra.clip3(-tc, tc, ((((q0 - p0) << 2) + p1 - q1 + 4) >> 3));
        int maxValue = (1 << bitDepth) - 1;

        if (!noFilterP)
            set(cIdx, xc - nx, yc - ny, MathExtra.clip3(0, maxValue, p0 + delta));
        if (!noFilterQ)
            set(cIdx, xc, yc, MathExtra.clip3(0, maxValue, q0 - delta));
    }

    private static int chromaQp(int qpi)
    {
        if (qpi < 30)
            return qpi;
        if (qpi > 43)
            return qpi - 6;
        return CHROMA_QP_TABLE[qpi - 30];
    }

    // 8.7.3 Sample adaptive offset process, reading the deblocked picture

    private void sampleAdaptiveOffset()
    {
        byte[][][] deblocked = null;
        int[][][] deblockedHighColorRange = null;
        if (pixels != null)
        {
            deblocked = new byte[chromaCount][][];
            for (int c = 0; c < chromaCount; c++)
            {
                deblocked[c] = new byte[pixels[c].length][];
                for (int x = 0; x < pixels[c].length; x++)
                    deblocked[c][x] = pixels[c][x].clone();
            }
        }
        else
        {
            deblockedHighColorRange = new int[chromaCount][][];
            for (int c = 0; c < chromaCount; c++)
            {
                deblockedHighColorRange[c] = new int[pixelsHighColorRange[c].length][];
                for (int x = 0; x < pixelsHighColorRange[c].length; x++)
                    deblockedHighColorRange[c][x] = pixelsHighColorRange[c][x].clone();
            }
        }

        byte[][][] output = pixels;
        int[][][] outputHighColorRange = pixelsHighColorRange;
        // reads go to the deblocked copy, writes to the picture
        pixels = deblocked;
        pixelsHighColorRange = deblockedHighColorRange;

        int widthInCtbs = (int) sps.getPicWidthInCtbsY();
        int heightInCtbs = (int) sps.getPicHeightInCtbsY();

        for (int ry = 0; ry < heightInCtbs; ry++)
        {
            for (int rx = 0; rx < widthInCtbs; rx++)
            {
                slice_segment_header header = sliceHeaderAt(rx << ctbLog2Size, ry << ctbLog2Size);

                for (int cIdx = 0; cIdx < chromaCount; cIdx++)
                {
                    if (!(cIdx == 0 ? header.slice_sao_luma_flag : header.slice_sao_chroma_flag))
                        continue;

                    int saoType = picture.SaoTypeIdx[cIdx][rx][ry];
                    if (saoType == 0)
                        continue;

                    saoCtb(rx, ry, cIdx, saoType, output, outputHighColorRange);
                }
            }
        }

        pixels = output;
        pixelsHighColorRange = outputHighColorRange;
    }

    // 8.7.3.2 CTB modification process
    private void saoCtb(int rx, int ry, int cIdx, int saoType, byte[][][] output, int[][][] outputHighColorRange)
    {
        int subW = cIdx == 0 ? 1 : subWidthC;
        int subH = cIdx == 0 ? 1 : subHeightC;
        int bitDepth = (cIdx == 0 ? sps.getBitDepthY() : sps.getBitDepthC()) & 0xFF;
        int maxValue = (1 << bitDepth) - 1;
        int compWidth = width / subW;
        int compHeight = height / subH;
        int ctbWidth = (1 << ctbLog2Size) / subW;
        int ctbHeight = (1 << ctbLog2Size) / subH;
        int xCtb = rx * ctbWidth;
        int yCtb = ry * ctbHeight;
        int xEnd = Math.min(xCtb + ctbWidth, compWidth);
        int yEnd = Math.min(yCtb + ctbHeight, compHeight);

        int log2OffsetScale = 0;
        if (pps.pps_range_ext != null)
        {
            log2OffsetScale = (int) (cIdx == 0 ?
                pps.pps_range_ext.log2_sao_offset_scale_luma :
                pps.pps_range_ext.log2_sao_offset_scale_chroma);
        }

        int[] offsetAbs = picture.sao_offset_abs[cIdx][rx][ry];
        int[] offsetSign = picture.sao_offset_sign[cIdx][rx][ry];
        int[] offsetVal = new int[5];
        for (int i = 0; i < 4; i++)
            offsetVal[i + 1] = ((1 - 2 * offsetSign[i]) * offsetAbs[i]) << log2OffsetScale;

        int[] bandTable = null;
        int bandShift = bitDepth - 5;
        int eoClass = picture.SaoEoClass[cIdx][rx][ry];
        if (saoType == 1)
        {
            bandTable = new int[32];
            int bandPosition = picture.sao_band_position[cIdx][rx][ry];
            for (int k = 0; k < 4; k++)
                bandTable[(k + bandPosition) & 31] = k + 1;
        }

        int currentCtb = ctbAddrRs(rx << ctbLog2Size, ry << ctbLog2Size);

        for (int ys = yCtb; ys < yEnd; ys++)
        {
            for (int xs = xCtb; xs < xEnd; xs++)
            {
                if (noFilter(xs * subW, ys * subH))
                    continue;

                int sample = get(cIdx, xs, ys);
                int offset;

                if (saoType == 1)
                {
                    offset = offsetVal[bandTable[sample >> bandShift]];
                }
                else
                {
                    int edgeIdx = 2;
                    boolean skip = false;
                    for (int k = 0; k < 2; k++)
                    {
                        int xn = xs + SAO_H_POS[eoClass][k];
                        int yn = ys + SAO_V_POS[eoClass][k];
                        if (xn < 0 || yn < 0 || xn >= compWidth || yn >= compHeight ||
                            !saoNeighbourUsable(currentCtb, xn * subW, yn * subH))
                        {
                            skip = true;
                            break;
                        }
                        edgeIdx += Integer.signum(sample - get(cIdx, xn, yn));
                    }

                    if (skip)
                        continue;

                    if (edgeIdx <= 2)
                        edgeIdx = edgeIdx == 2 ? 0 : edgeIdx + 1;

                    offset = offsetVal[edgeIdx];
                }

                if (offset == 0)
                    continue;

                int value = MathExtra.clip3(0, maxValue, sample + offset);
                if (output != null)
                    output[cIdx][xs][ys] = (byte) value;
                else
                    outputHighColorRange[cIdx][xs][ys] = value;
            }
        }
    }

    // A neighbouring sample across a slice or tile boundary that filtering may not cross leaves the sample unmodified
    private boolean saoNeighbourUsable(int currentCtb, int xn, int yn)
    {
        int neighbourCtb = ctbAddrRs(xn, yn);
        if (neighbourCtb == currentCtb)
            return true;

        if (!pps.loop_filter_across_tiles_enabled_flag && tileId(neighbourCtb) != tileId(currentCtb))
            return false;

        int currentSlice = sliceAddrOfCtb(currentCtb);
        int neighbourSlice = sliceAddrOfCtb(neighbourCtb);
        if (currentSlice == neighbourSlice)
            return true;

        // the later slice in decoding order owns the boundary between the two
        boolean neighbourFirst = pps.CtbAddrRsToTs[neighbourSlice] < pps.CtbAddrRsToTs[currentSlice];
        int widthInCtbs = (int) sps.getPicWidthInCtbsY();
        int laterCtb = neighbourFirst ? currentCtb : neighbourCtb;
        slice_segment_header later = sliceHeaderAt((laterCtb % widthInCtbs) << ctbLog2Size, (laterCtb / widthInCtbs) << ctbLog2Size);
        return later.slice_loop_filter_across_slices_enabled_flag;
    }

    private boolean noFilter(int x, int y)
    {
        return (picture.blockFlags[x >> 2][y >> 2] & NO_FILTER) != 0 ||
            (sps.pcm_loop_filter_disabled_flag && picture.pcm_flag[x][y]);
    }

    private slice_segment_header sliceHeaderAt(int x, int y)
    {
        int xCtb = (x >> ctbLog2Size) << ctbLog2Size;
        int yCtb = (y >> ctbLog2Size) << ctbLog2Size;
        return picture.getSliceHeaders().get(picture.SliceHeaderIndex[xCtb][yCtb]);
    }

    private int ctbAddrRs(int x, int y)
    {
        return (y >> ctbLog2Size) * (int) sps.getPicWidthInCtbsY() + (x >> ctbLog2Size);
    }

    private int tileId(int ctbAddrRs)
    {
        return (int) pps.TileId[(int) pps.CtbAddrRsToTs[ctbAddrRs]];
    }

    private int sliceAddr(int x, int y)
    {
        return picture.SliceAddrRs[x >> ctbLog2Size][y >> ctbLog2Size];
    }

    private int sliceAddrOfCtb(int ctbAddrRs)
    {
        int widthInCtbs = (int) sps.getPicWidthInCtbsY();
        return picture.SliceAddrRs[ctbAddrRs % widthInCtbs][ctbAddrRs / widthInCtbs];
    }

    private int get(int cIdx, int x, int y)
    {
        return pixels != null ? pixels[cIdx][x][y] & 0xFF : pixelsHighColorRange[cIdx][x][y];
    }

    private void set(int cIdx, int x, int y, int value)
    {
        if (pixels != null)
            pixels[cIdx][x][y] = (byte) value;
        else
            pixelsHighColorRange[cIdx][x][y] = value;
    }
}
