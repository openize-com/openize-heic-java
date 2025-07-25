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

import java.util.HashMap;

public class HeicPicture
{
    public final HashMap<Integer, slice_segment_header> getSliceHeaders(){ return sliceHeaders; }
    private final HashMap<Integer,slice_segment_header> sliceHeaders;
    public final HashMap<Integer, slice_segment_data> getSliceUnits(){ return sliceUnits; }
    private final HashMap<Integer,slice_segment_data> sliceUnits;

    public final video_parameter_set_rbsp vps;
    public final seq_parameter_set_rbsp sps;
    public final pic_parameter_set_rbsp pps;
    public DecoderContext Context;
    public final NalHeader NalHeader = new NalHeader();

    public int PicOrderCntVal;
    public int picture_order_cnt_lsb;

    public final int[][][] SaoTypeIdx;
    public final int[][][][] SaoOffsetVal;
    public final int[][][] SaoEoClass;

    public final int[][][] ResScaleVal;

    public final int[][][][] sao_offset_abs;
    public final int[][][][] sao_offset_sign;
    public final int[][][] sao_band_position;

    public /*UInt32*/long[][] SliceAddrRs;
    public int[][] SliceHeaderIndex;
    public final IntraPredMode[][] IntraPredModeY;
    public final IntraPredMode[][] IntraPredModeC;

    public final int[][][] TransCoeffLevel;

    public final int[][] CtDepth;
    public final PredMode[][] CuPredMode;
    public final int[][] QpY;
    public final int[][] Log2CbSize;
    public final boolean[][] merge_flag;

    public final boolean[][] cu_skip_flag;
    public final boolean[][] palette_mode_flag;
    public final boolean[][] pcm_flag;
    public final boolean[][] prev_intra_luma_pred_flag;
    public final int[][] mpm_idx;
    public final IntraPredMode[][] rem_intra_luma_pred_mode;
    public final byte[][] intra_chroma_pred_mode;

    public final boolean[][] tu_residual_act_flag;

    //public bool[,][] split_transform_flag;
    public final boolean[][][] cbf_cb;
    public final boolean[][][] cbf_cr;
    public final boolean[][][] cbf_luma;

    //public bool[,][] transform_skip_flag;
    public final boolean[][][] explicit_rdpcm_flag;
    public final boolean[][][] explicit_rdpcm_dir_flag;

    public int[] pcm_sample_luma;
    public int[] pcm_sample_chroma;

    public final /*UInt16*/int[][][] pixels;

    public HeicPicture(slice_segment_header slice_header, NalHeader nal_header)
    {
        sliceHeaders = new HashMap<>();
        sliceUnits = new HashMap<>();

        pps = slice_header.pps;
        sps = pps.sps;
        vps = sps.vps;
        nal_header.cloneTo(NalHeader);

        /*UInt32*/long widthMax;
        /*UInt32*/long heightMax;

        widthMax = ((sps.getPicWidthInCtbsY() & 0xFFFFFFFFL) << ( (sps.getCtbLog2SizeY() & 0xFF) & 0x1F)) & 0xFFFFFFFFL;
        heightMax = ((sps.getPicHeightInCtbsY() & 0xFFFFFFFFL) << ( (sps.getCtbLog2SizeY() & 0xFF) & 0x1F)) & 0xFFFFFFFFL;

        int chroma_count = (sps.getChromaArrayType() & 0xFFFFFFFFL) != 0 ? 3 : 1;

        SaoTypeIdx = new int[chroma_count][][];
        SaoEoClass = new int[chroma_count][][];
        ResScaleVal = new int[chroma_count][][];
        SaoOffsetVal = new int[chroma_count][][][];
        sao_offset_abs = new int[chroma_count][][][];
        sao_offset_sign = new int[chroma_count][][][];
        sao_band_position = new int[chroma_count][][];
        pixels = new /*UInt16*/int[chroma_count][][];
        TransCoeffLevel = new int[chroma_count][][];

        for (int i = 0; i < chroma_count; i++)
        {
            SaoTypeIdx[i] = new int[(int)(sps.getPicWidthInCtbsY())][(int)(sps.getPicHeightInCtbsY())];
            SaoEoClass[i] = new int[(int)(sps.getPicWidthInCtbsY())][(int)(sps.getPicHeightInCtbsY())];
            ResScaleVal[i] = new int[(int)(sps.getPicWidthInCtbsY())][(int)(sps.getPicHeightInCtbsY())];
            SaoOffsetVal[i] = new int[(int)(sps.getPicWidthInCtbsY())][(int)(sps.getPicHeightInCtbsY())][];
            sao_offset_abs[i] = new int[(int)(sps.getPicWidthInCtbsY())][(int)(sps.getPicHeightInCtbsY())][];
            sao_offset_sign[i] = new int[(int)(sps.getPicWidthInCtbsY())][(int)(sps.getPicHeightInCtbsY())][];
            sao_band_position[i] = new int[(int)(sps.getPicWidthInCtbsY())][(int)(sps.getPicHeightInCtbsY())];

            pixels[i] = new /*UInt16*/int[(int)(widthMax & 0xFFFFFFFFL)][(int)(heightMax & 0xFFFFFFFFL)];
            TransCoeffLevel[i] = new int[(int)(widthMax & 0xFFFFFFFFL)][(int)(heightMax & 0xFFFFFFFFL)];
        }

        CtDepth = new int[(int)(widthMax & 0xFFFFFFFFL)][(int)(heightMax & 0xFFFFFFFFL)];
        CuPredMode = PredMode.createArray2D((int)(widthMax & 0xFFFFFFFFL), (int)(heightMax & 0xFFFFFFFFL));
        QpY = new int[(int)(widthMax & 0xFFFFFFFFL)][(int)(heightMax & 0xFFFFFFFFL)];
        Log2CbSize = new int[(int)(widthMax & 0xFFFFFFFFL)][(int)(heightMax & 0xFFFFFFFFL)];
        IntraPredModeY = IntraPredMode.createArray2D((int)(widthMax & 0xFFFFFFFFL), (int)(heightMax & 0xFFFFFFFFL));
        IntraPredModeC = IntraPredMode.createArray2D((int)(widthMax & 0xFFFFFFFFL), (int)(heightMax & 0xFFFFFFFFL));


        merge_flag = new boolean[(int)(widthMax & 0xFFFFFFFFL)][(int)(heightMax & 0xFFFFFFFFL)];
        cu_skip_flag = new boolean[(int)(widthMax & 0xFFFFFFFFL)][(int)(heightMax & 0xFFFFFFFFL)];
        palette_mode_flag = new boolean[(int)(widthMax & 0xFFFFFFFFL)][(int)(heightMax & 0xFFFFFFFFL)];
        pcm_flag = new boolean[(int)(widthMax & 0xFFFFFFFFL)][(int)(heightMax & 0xFFFFFFFFL)];
        prev_intra_luma_pred_flag = new boolean[(int)(widthMax & 0xFFFFFFFFL)][(int)(heightMax & 0xFFFFFFFFL)];

        mpm_idx = new int[(int)(widthMax & 0xFFFFFFFFL)][(int)(heightMax & 0xFFFFFFFFL)];
        rem_intra_luma_pred_mode = IntraPredMode.createArray2D((int)(widthMax & 0xFFFFFFFFL), (int)(heightMax & 0xFFFFFFFFL));
        intra_chroma_pred_mode = new byte[(int)(widthMax & 0xFFFFFFFFL)][(int)(heightMax & 0xFFFFFFFFL)];

        tu_residual_act_flag = new boolean[(int)(widthMax & 0xFFFFFFFFL)][(int)(heightMax & 0xFFFFFFFFL)];

        //split_transform_flag = new bool[xMax, yMax][];
        cbf_cb = new boolean[(int)(widthMax & 0xFFFFFFFFL)][(int)(heightMax & 0xFFFFFFFFL)][];
        cbf_cr = new boolean[(int)(widthMax & 0xFFFFFFFFL)][(int)(heightMax & 0xFFFFFFFFL)][];
        cbf_luma = new boolean[(int)(widthMax & 0xFFFFFFFFL)][(int)(heightMax & 0xFFFFFFFFL)][];

        //transform_skip_flag = new bool[xMax, yMax][];
        explicit_rdpcm_flag = new boolean[(int)(widthMax & 0xFFFFFFFFL)][(int)(heightMax & 0xFFFFFFFFL)][];
        explicit_rdpcm_dir_flag = new boolean[(int)(widthMax & 0xFFFFFFFFL)][(int)(heightMax & 0xFFFFFFFFL)][];
    }

    // 6.4.1 Derivation process for z-scan order block availability
    // 
    // The luma location ( xCurr, yCurr ) of the top-left sample of the current block
    // relative to the top-left luma sample of the current picture
    //
    // The luma location ( xNbY, yNbY ) covered by a neighbouring block relative
    // to the top-left luma sample of the current picture.
    final boolean checkZScanAvaliability(int xCurr, int yCurr, int xNbY, int yNbY)
    {
        if (xNbY < 0 || yNbY < 0)
            return false;

        if (xNbY >= (sps.pic_width_in_luma_samples & 0xFFFFFFFFL) || yNbY >= (sps.pic_height_in_luma_samples & 0xFFFFFFFFL))
            return false;

        // The minimum luma block address in z-scan order minBlockAddrCurr of the current block
        /*UInt32*/long minBlockAddrCurr = pps.MinTbAddrZs[xCurr >> (sps.getMinTbLog2SizeY() & 0xFF)][yCurr >> (sps.getMinTbLog2SizeY() & 0xFF)];

        // The minimum luma block address in z-scan order minBlockAddrN of the neighbouring block covering the location (xNbY, yNbY)
        /*UInt32*/long minBlockAddrN = pps.MinTbAddrZs[xNbY >> (sps.getMinTbLog2SizeY() & 0xFF)][yNbY >> (sps.getMinTbLog2SizeY() & 0xFF)];

        if ((minBlockAddrN & 0xFFFFFFFFL) > (minBlockAddrCurr & 0xFFFFFFFFL))
            return false;

        // SliceAddrRs associated with the slice segment containing the current block
        int xCurrCtb = xCurr >> (sps.getCtbLog2SizeY() & 0xFF);
        int yCurrCtb = yCurr >> (sps.getCtbLog2SizeY() & 0xFF);
        int ctbCurrAddrRs = yCurrCtb * (int)sps.getPicWidthInCtbsY() + xCurrCtb;

        // SliceAddrRs associated with the slice segment containing the neighbouring block
        int xNbCtb = xNbY >> (sps.getCtbLog2SizeY() & 0xFF);
        int yNbCtb = yNbY >> (sps.getCtbLog2SizeY() & 0xFF);
        int ctbNbAddrRs = yNbCtb * (int)sps.getPicWidthInCtbsY() + xNbCtb;

        // the current block and the neighbouring block are in the same slice
        if ((SliceAddrRs[xCurrCtb][yCurrCtb] & 0xFFFFFFFFL) != (SliceAddrRs[xNbCtb][yNbCtb] & 0xFFFFFFFFL))
            return false;

        // the neighbouring block is contained in a different tile than the current block
        return (pps.TileIdFromRs[ctbCurrAddrRs] & 0xFFFFFFFFL) == (pps.TileIdFromRs[ctbNbAddrRs] & 0xFFFFFFFFL);
    }

    // 6.4.2 Derivation process for prediction block availability
    //
    // the luma location ( xCb, yCb ) of the top-left sample of the current luma coding block
    // relative to the top-left luma sample of the current picture
    //
    // a variable nCbS specifying the size of the current luma coding block
    //
    // the luma location ( xPb, yPb ) of the top-left sample of the current luma prediction block
    // relative to the top-left luma sample of the current picture
    //
    // two variables nPbW and nPbH specifying the width and the height of the current luma prediction block
    //
    // a variable partIdx specifying the partition index of the current prediction unit within the current coding unit
    //
    // the luma location ( xNbY, yNbY ) covered by a neighbouring prediction block
    // relative to the top-left luma sample of the current picture
    //
    // Output of this process is the availability of the neighbouring prediction block covering the location ( xNbY, yNbY )
    final boolean checkPredictionAvaliability(int xCb, int yCb, int nCbS, int xPb, int yPb, int nPbW, int nPbH, int partIdx, int xNbY, int yNbY)
    {
        boolean sameCb =
            (xCb <= xNbY) && (yCb <= yNbY) &&
            (xCb + nCbS > xNbY) && (yCb + nCbS > yNbY);

        boolean availableN;

        if (!sameCb)
        {
            availableN = checkZScanAvaliability(xPb, yPb, xNbY, yNbY);
        }
        else
        {
            availableN = nPbW << 1 != nCbS ||
                    nPbH << 1 != nCbS ||
                    partIdx != 1 ||
                    yCb + nPbH > yNbY ||
                    xCb + nPbW <= xNbY;
        }

        if (availableN && CuPredMode[xNbY][yNbY] == PredMode.MODE_INTRA)
            availableN = false;

        return availableN;
    }

    final void setCtDepth(int x0, int y0, int log2CbSize, int cqtDepth)
    {
        int nCbS = 1 << log2CbSize;
        for (int i = x0; i < x0 + nCbS; i++)
            for (int j = y0; j < y0 + nCbS; j++)
                CtDepth[i][j] = cqtDepth;
    }
    final void setCuPredMode(int x0, int y0, int nCbS, PredMode mode)
    {
        for (int i = x0; i < x0 + nCbS; i++)
        {
            for (int j = y0; j < y0 + nCbS; j++)
            {
                CuPredMode[i][j] = mode;

                if(mode == PredMode.MODE_SKIP)
                    merge_flag[i][j] = true;
            }
        }
    }
    final void setPcmFlag(int x0, int y0, int nCbS, boolean value)
    {
        for (int i = x0; i < x0 + nCbS; i++)
            for (int j = y0; j < y0 + nCbS; j++)
                pcm_flag[i][j] = value;
    }
    final void setQpY(int x0, int y0, int log2CbSize, int qpY)
    {
        int nCbS = 1 << log2CbSize;
        for (int i = x0; i < x0 + nCbS; i++)
            for (int j = y0; j < y0 + nCbS; j++)
                QpY[i][j] = qpY;
    }
    final void setLog2CbSize(int x0, int y0, int log2CbSize)
    {
        int nCbS = 1 << log2CbSize;
        for (int i = x0; i < x0 + nCbS; i++)
            for (int j = y0; j < y0 + nCbS; j++)
                Log2CbSize[i][j] = log2CbSize;
    }

    // 8.4.2 Derivation process for luma intra prediction mode
    final IntraPredMode[] generateIntraPredModeCandidates(int xPb, int yPb, int pbOffset, boolean availableA, boolean availableB)
    {
        IntraPredMode candIntraPredModeA, candIntraPredModeB;

        if (!availableA)
        {
            candIntraPredModeA = IntraPredMode.INTRA_DC;
        }
        else if (CuPredMode[xPb - 1][yPb] != PredMode.MODE_INTRA || pcm_flag[xPb - 1][yPb])
        {
            candIntraPredModeA = IntraPredMode.INTRA_DC;
        }
        else
        {
            candIntraPredModeA = IntraPredModeY[xPb - 1][yPb];
        }

        if (!availableB)
        {
            candIntraPredModeB = IntraPredMode.INTRA_DC;
        }
        else if (CuPredMode[xPb][yPb - 1] != PredMode.MODE_INTRA || pcm_flag[xPb][yPb - 1])
        {
            candIntraPredModeB = IntraPredMode.INTRA_DC;
        }
        else if (yPb - 1 < ((yPb >> (sps.getCtbLog2SizeY() & 0xFF)) << (sps.getCtbLog2SizeY() & 0xFF)))
        {
            candIntraPredModeB = IntraPredMode.INTRA_DC;
        }
        else
        {
            candIntraPredModeB = IntraPredModeY[xPb][yPb - 1];
        }

        IntraPredMode[] candModeList = IntraPredMode.createArray(3);

        if (candIntraPredModeB == candIntraPredModeA)
        {
            if (candIntraPredModeA.ordinal() < 2)
            {
                candModeList[0] = IntraPredMode.INTRA_PLANAR;
                candModeList[1] = IntraPredMode.INTRA_DC;
                candModeList[2] = IntraPredMode.INTRA_ANGULAR26;
            }
            else
            {
                candModeList[0] = candIntraPredModeA;
                candModeList[1] = IntraPredMode.get((2 + ((candIntraPredModeA.ordinal() + 29) % 32)));
                candModeList[2] = IntraPredMode.get((2 + (((candIntraPredModeA.ordinal() - 2) + 1) % 32)));
            }
        }
        else
        {
            candModeList[0] = candIntraPredModeA;
            candModeList[1] = candIntraPredModeB;

            if (candModeList[0] != IntraPredMode.INTRA_PLANAR && candModeList[1] != IntraPredMode.INTRA_PLANAR)
            {
                candModeList[2] = IntraPredMode.INTRA_PLANAR;
            }
            else if (candModeList[0] != IntraPredMode.INTRA_DC && candModeList[1] != IntraPredMode.INTRA_DC)
            {
                candModeList[2] = IntraPredMode.INTRA_DC;
            }
            else
            {
                candModeList[2] = IntraPredMode.INTRA_ANGULAR26;
            }
        }

        return candModeList;
    }

    final IntraPredMode setIntraPredModeY(int xPb, int yPb, int pbSize, IntraPredMode[] candModeList)
    {
        IntraPredMode mode;

        if (prev_intra_luma_pred_flag[xPb][yPb])
        {
            mode = candModeList[mpm_idx[xPb][yPb]];
        }
        else
        {
            if (candModeList[0].ordinal() > candModeList[1].ordinal())
            {
                final IntraPredMode tmp = candModeList[0];
                candModeList[0] = candModeList[1];
                candModeList[1] = tmp;
            }

            if (candModeList[0].ordinal() > candModeList[2].ordinal())
            {
                final IntraPredMode tmp = candModeList[0];
                candModeList[0] = candModeList[2];
                candModeList[2] = tmp;
            }

            if (candModeList[1].ordinal() > candModeList[2].ordinal())
            {
                final IntraPredMode tmp = candModeList[1];
                candModeList[1] = candModeList[2];
                candModeList[2] = tmp;
            }

            mode = rem_intra_luma_pred_mode[xPb][yPb];

            int intMode = mode.ordinal();
            for (int i = 0; i < 3; i++)
            {
                if (intMode >= candModeList[i].ordinal())
                    intMode++;
            }
            mode = IntraPredMode.get(intMode);
        }

        for (int i = xPb; i < xPb + pbSize; i++)
        {
            for (int j = yPb; j < yPb + pbSize; j++)
            {
                IntraPredModeY[i][j] = mode;
            }
        }

        return mode;
    }
    final void setIntraPredModeC(int xPb, int yPb, int pbSize, IntraPredMode mode)
    {
        for (int i = xPb; i < xPb + pbSize; i++)
            for (int j = yPb; j < yPb + pbSize; j++)
                IntraPredModeC[i][j] = mode;
    }

    final IntraPredMode deriveIntraPredModeIndex(IntraPredMode intraLumaPredMode, byte intraChromaPredMode)
    {
        if ((intraChromaPredMode & 0xFF) == 4)
            return intraLumaPredMode;

        IntraPredMode candidate;

        switch (intraChromaPredMode)
        {
            case 0:
                candidate = IntraPredMode.INTRA_PLANAR;
                break;
            case 1:
                candidate = IntraPredMode.INTRA_ANGULAR26;
                break;
            case 2:
                candidate = IntraPredMode.INTRA_ANGULAR10;
                break;
            case 3:
            default:
                candidate = IntraPredMode.INTRA_DC;
                break;
        }

        if (intraLumaPredMode == candidate)
            return IntraPredMode.INTRA_ANGULAR34;

        return candidate;
    }
}

