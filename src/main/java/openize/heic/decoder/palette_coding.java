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

import openize.MathUtils;
import openize.heic.decoder.io.BitStreamWithNalSupport;


class palette_coding
{
    private final boolean[] palettePredictorEntryReuseFlags;
    int num_signalled_palette_entries;
    int num_palette_indices;
    boolean copy_above_indices_for_final_run_flag;
    boolean palette_transpose_flag;
    boolean palette_escape_val_present_flag;
    int palette_idx_idc;
    boolean copy_above_palette_indices_flag;
    int palette_run_prefix;
    int palette_run_suffix;
    int palette_escape_val;
    //final int[][] new_palette_entries;
    private int numPredictedPaletteEntries;
    private int predictorPaletteSize; // IS NOT DEFINED
    private int[] paletteIndexIdc;
    private int paletteMaxRun;
    private int paletteRun;
    private int runToEnd;
    private int[][] paletteIndexMap;
    private int[][][] paletteEscapeVal;
    private boolean[][] copyAboveIndicesFlag;
    private int currPaletteIndex;

    public palette_coding(BitStreamWithNalSupport stream, slice_segment_header header,
                          int x0, int y0, int nCbS)
    {
        boolean palettePredictionFinished = false;
        numPredictedPaletteEntries = 0;
        palettePredictorEntryReuseFlags = new boolean[getPredictorPaletteSize()];

        for (int predictorEntryIdx = 0; predictorEntryIdx < getPredictorPaletteSize() &&
                !palettePredictionFinished && numPredictedPaletteEntries < (header.pps.sps.sps_scc_ext.palette_max_size & 0xFFFFFFFFL);
             predictorEntryIdx++)
        {
            int palette_predictor_run = stream.readAev(); // ae(v)
            if (palette_predictor_run != 1)
            {
                if (palette_predictor_run > 1)
                {
                    predictorEntryIdx += palette_predictor_run - 1;
                }

                getPalettePredictorEntryReuseFlags()[predictorEntryIdx] = true;
                numPredictedPaletteEntries++;
            }
            else
            {
                palettePredictionFinished = true;
            }
        }

        if (numPredictedPaletteEntries < (header.pps.sps.sps_scc_ext.palette_max_size & 0xFFFFFFFFL))
        {
            num_signalled_palette_entries = stream.readAev(); // ae(v)
        }

        int numComps = ((header.pps.sps.getChromaArrayType() & 0xFFFFFFFFL) == 0) ? 1 : 3;

        //new_palette_entries = new int[numComps][];
        for (int cIdx = 0; cIdx < numComps; cIdx++)
        {
            //new_palette_entries[cIdx] = new int[num_signalled_palette_entries];

            for (int i = 0; i < num_signalled_palette_entries; i++)
            {
                /*new_palette_entries[cIdx][i] =*/ stream.readAev(); // ae(v)
            }
        }

        if (getCurrentPaletteSize() != 0)
        {
            palette_escape_val_present_flag = stream.readAevFlag(); // ae(v)
        }

        if (getMaxPaletteIndex() > 0)
        {
            num_palette_indices = stream.readAev() + 1; // ae(v)
            int adjust = 0;
            setPaletteIndexIdc(new int[num_palette_indices - 1]);
            for (int i = 0; i <= num_palette_indices - 1; i++)
            {
                if (getMaxPaletteIndex() - adjust > 0)
                {
                    palette_idx_idc = stream.readAev(); // ae(v)
                    getPaletteIndexIdc()[i] = palette_idx_idc;
                }
                adjust = 1;
            }
            copy_above_indices_for_final_run_flag = stream.readAevFlag(); // ae(v)
            palette_transpose_flag = stream.readAevFlag(); // ae(v)
        }

     /*   if (palette_escape_val_present_flag)
        {
            //new delta_qp();
            //if (!cu_transquant_bypass_flag)
            //    new chroma_qp_offset();
        }*/

        int remainingNumIndices = num_palette_indices;
        int PaletteScanPos = 0;
        int log2BlockSize = MathUtils.f64_s32(MathUtils.log(nCbS, 2));

        while (PaletteScanPos < nCbS * nCbS)
        {
            int xC = x0 + (Scans.getScanOrder()[log2BlockSize][3][PaletteScanPos][0] & 0xFF);
            int yC = y0 + (Scans.getScanOrder()[log2BlockSize][3][PaletteScanPos][1] & 0xFF);

            int xcPrev = 0;
            int ycPrev = 0;

            if (PaletteScanPos > 0)
            {
                xcPrev = x0 + (Scans.getScanOrder()[log2BlockSize][3][PaletteScanPos - 1][0] & 0xFF);
                ycPrev = y0 + (Scans.getScanOrder()[log2BlockSize][3][PaletteScanPos - 1][1] & 0xFF);
            }

            setPaletteRun(nCbS * nCbS - PaletteScanPos);
            setRunToEnd(1);
            getCopyAboveIndicesFlag()[xC][yC] = false;

            if (getMaxPaletteIndex() > 0)
            {
                if (PaletteScanPos >= nCbS && !getCopyAboveIndicesFlag()[xcPrev][ycPrev])
                {
                    if (remainingNumIndices > 0 && PaletteScanPos < nCbS * nCbS - 1)
                    {
                        copy_above_palette_indices_flag = stream.readAevFlag(); // ae(v)
                        getCopyAboveIndicesFlag()[xC][yC] = copy_above_palette_indices_flag;
                    }
                    else
                    {
                        getCopyAboveIndicesFlag()[xC][yC] = PaletteScanPos != nCbS * nCbS - 1 || remainingNumIndices <= 0;
                    }
                }
            }

            if (!getCopyAboveIndicesFlag()[xC][yC])
            {
                int currNumIndices = num_palette_indices - remainingNumIndices;
                setCurrPaletteIndex(getPaletteIndexIdc()[currNumIndices]);
            }

            if (getMaxPaletteIndex() > 0)
            {
                if (!getCopyAboveIndicesFlag()[xC][yC])
                {
                    remainingNumIndices -= 1;
                }

                if (remainingNumIndices > 0 ||
                        getCopyAboveIndicesFlag()[xC][yC] != copy_above_indices_for_final_run_flag)
                {
                    setPaletteMaxRun(nCbS * nCbS - PaletteScanPos - remainingNumIndices -
                            (copy_above_indices_for_final_run_flag ? 1 : 0));
                    setRunToEnd(0);

                    if (getPaletteMaxRun() - 1 > 0)
                    {
                        palette_run_prefix = stream.readAev(); // ae(v)
                        if ((palette_run_prefix > 1) && (getPaletteMaxRun() - 1 !=
                                (1 << (palette_run_prefix - 1))))
                        {
                            palette_run_suffix = stream.readAev(); // ae(v)
                        }
                    }
                }
            }

            int runPos = 0;
            while (runPos <= getPaletteRun() - 1)
            {
                int xR = x0 + (Scans.getScanOrder()[log2BlockSize][3][PaletteScanPos][0] & 0xFF);
                int yR = y0 + (Scans.getScanOrder()[log2BlockSize][3][PaletteScanPos][1] & 0xFF);

                if (!getCopyAboveIndicesFlag()[xC][yC])
                {
                    getCopyAboveIndicesFlag()[xR][yR] = false;
                    getPaletteIndexMap()[xR][yR] = getCurrPaletteIndex();
                }
                else
                {
                    getCopyAboveIndicesFlag()[xR][yR] = true;
                    getPaletteIndexMap()[xR][yR] = getPaletteIndexMap()[xR][yR - 1];
                }

                runPos++;
                PaletteScanPos++;
            }
        }

        if (palette_escape_val_present_flag)
        {
            for (int cIdx = 0; cIdx < numComps; cIdx++)
            {
                for (int sPos = 0; sPos < nCbS * nCbS; sPos++)
                {
                    int xC = x0 + (Scans.getScanOrder()[log2BlockSize][3][sPos][0] & 0xFF);
                    int yC = y0 + (Scans.getScanOrder()[log2BlockSize][3][sPos][1] & 0xFF);

                    if (getPaletteIndexMap()[xC][yC] == getMaxPaletteIndex())
                    {
                        if (cIdx == 0 ||
                                (xC % 2 == 0 && yC % 2 == 0 && (header.pps.sps.getChromaArrayType() & 0xFFFFFFFFL) == 1) ||
                                (xC % 2 == 0 && !palette_transpose_flag && (header.pps.sps.getChromaArrayType() & 0xFFFFFFFFL) == 2) ||
                                (yC % 2 == 0 && palette_transpose_flag && (header.pps.sps.getChromaArrayType() & 0xFFFFFFFFL) == 2) ||
                                (header.pps.sps.getChromaArrayType() & 0xFFFFFFFFL) == 3)
                        {
                            palette_escape_val = stream.readAev(); // ae(v)
                            getPaletteEscapeVal()[cIdx][xC][yC] = palette_escape_val;
                        }
                    }
                }
            }
        }
    }

    public final int getNumPredictedPaletteEntries()
    {
        return numPredictedPaletteEntries;
    }

    public final int getPredictorPaletteSize()
    {
        return predictorPaletteSize;
    }

    public final boolean[] getPalettePredictorEntryReuseFlags()
    {
        return palettePredictorEntryReuseFlags;
    }

    public final int[] getPaletteIndexIdc()
    {
        return paletteIndexIdc;
    }

    final void setPaletteIndexIdc(int[] value)
    {
        paletteIndexIdc = value;
    }

    public final int getPaletteMaxRun()
    {
        return paletteMaxRun;
    }

    final void setPaletteMaxRun(int value)
    {
        paletteMaxRun = value;
    }

    public final int getPaletteRun()
    {
        return paletteRun;
    }

    final void setPaletteRun(int value)
    {
        paletteRun = value;
    }

    public final int getRunToEnd()
    {
        return runToEnd;
    }

    final void setRunToEnd(int value)
    {
        runToEnd = value;
    }

    public final int[][] getPaletteIndexMap()
    {
        return paletteIndexMap;
    }

    final void setPaletteIndexMap(int[][] value)
    {
        paletteIndexMap = value;
    }

    public final int[][][] getPaletteEscapeVal()
    {
        return paletteEscapeVal;
    }

    final void setPaletteEscapeVal(int[][][] value)
    {
        paletteEscapeVal = value;
    }

    public final boolean[][] getCopyAboveIndicesFlag()
    {
        return copyAboveIndicesFlag;
    }

    public final int getCurrPaletteIndex()
    {
        return currPaletteIndex;
    }

    final void setCurrPaletteIndex(int value)
    {
        currPaletteIndex = value;
    }

    final int getCurrentPaletteSize()
    {
        return numPredictedPaletteEntries + num_signalled_palette_entries;
    }

    final int getMaxPaletteIndex()
    {
        return getCurrentPaletteSize() - 1 + (palette_escape_val_present_flag ? 1 : 0);
    }
}
