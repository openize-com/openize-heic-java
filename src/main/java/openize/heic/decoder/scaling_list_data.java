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

import openize.heic.decoder.io.BitStreamWithNalSupport;

/**
 * Scaling list data (7.3.4) and the scaling factors derived from it (7.4.5).
 *
 * <p>
 * Table 7-4 – Specification of matrixId according to sizeId, prediction mode and colour component:
 * matrixId = cIdx for MODE_INTRA, cIdx + 3 for MODE_INTER; sizeId 0..3 for 4x4..32x32 blocks.
 * </p>
 */
class scaling_list_data
{
    // Table 7-6 – default values of ScalingList[1..3][0..2][i] (intra) and [1..3][3..5][i] (inter)
    private static final int[] DEFAULT_8X8_INTRA = {
        16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 17, 16, 17, 16, 17, 18,
        17, 18, 18, 17, 18, 21, 19, 20, 21, 20, 19, 21, 24, 22, 22, 24,
        24, 22, 22, 24, 25, 25, 27, 30, 27, 25, 25, 29, 31, 35, 35, 31,
        29, 36, 41, 44, 41, 36, 47, 54, 54, 47, 65, 70, 65, 88, 88, 115
    };

    private static final int[] DEFAULT_8X8_INTER = {
        16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 17, 17, 17, 17, 17, 18,
        18, 18, 18, 18, 18, 20, 20, 20, 20, 20, 20, 20, 24, 24, 24, 24,
        24, 24, 24, 24, 25, 25, 25, 25, 25, 25, 25, 28, 28, 28, 28, 28,
        28, 33, 33, 33, 33, 33, 41, 41, 41, 41, 54, 54, 54, 71, 71, 91
    };

    // Used when scaling_list_enabled_flag is set without any scaling_list_data in the SPS or PPS
    static final scaling_list_data DEFAULT = new scaling_list_data();

    // ScalingList[sizeId][matrixId][i], in up-right diagonal scan order
    private final int[][][] scalingList = new int[4][6][];
    // scaling_list_dc_coef_minus8[sizeId - 2][matrixId] + 8
    private final int[][] dcCoef = new int[2][6];

    private int[][][][] scalingFactor;
    private long scalingFactorChromaArrayType = -1;

    private scaling_list_data()
    {
        for (int sizeId = 0; sizeId < 4; sizeId++)
            for (int matrixId = 0; matrixId < 6; matrixId++)
                setDefault(sizeId, matrixId);
    }

    public scaling_list_data(BitStreamWithNalSupport stream)
    {
        for (int sizeId = 0; sizeId < 4; sizeId++)
        {
            int step = sizeId == 3 ? 3 : 1;
            for (int matrixId = 0; matrixId < 6; matrixId += step)
            {
                boolean scaling_list_pred_mode_flag = stream.readFlag();               // u(1)
                if (!scaling_list_pred_mode_flag)
                {
                    int scaling_list_pred_matrix_id_delta = (int) stream.readUev();    // ue(v)
                    if (scaling_list_pred_matrix_id_delta == 0)
                    {
                        setDefault(sizeId, matrixId);
                    }
                    else
                    {
                        int refMatrixId = matrixId - scaling_list_pred_matrix_id_delta * step;
                        scalingList[sizeId][matrixId] = scalingList[sizeId][refMatrixId].clone();
                        if (sizeId > 1)
                            dcCoef[sizeId - 2][matrixId] = dcCoef[sizeId - 2][refMatrixId];
                    }
                }
                else
                {
                    int nextCoef = 8;
                    int coefNum = Math.min(64, 1 << (4 + (sizeId << 1)));
                    if (sizeId > 1)
                    {
                        nextCoef = stream.readSev() + 8;                               // scaling_list_dc_coef_minus8 se(v)
                        dcCoef[sizeId - 2][matrixId] = nextCoef;
                    }

                    int[] list = new int[coefNum];
                    for (int i = 0; i < coefNum; i++)
                    {
                        int scaling_list_delta_coef = stream.readSev();                // se(v)
                        nextCoef = (nextCoef + scaling_list_delta_coef + 256) % 256;
                        list[i] = nextCoef;
                    }
                    scalingList[sizeId][matrixId] = list;
                }
            }
        }
    }

    private void setDefault(int sizeId, int matrixId)
    {
        if (sizeId == 0)
        {
            int[] flat = new int[16];
            java.util.Arrays.fill(flat, 16);
            scalingList[sizeId][matrixId] = flat;
        }
        else
        {
            scalingList[sizeId][matrixId] = (matrixId < 3 ? DEFAULT_8X8_INTRA : DEFAULT_8X8_INTER).clone();
        }

        if (sizeId > 1)
            dcCoef[sizeId - 2][matrixId] = 16;
    }

    /**
     * Returns ScalingFactor[sizeId][matrixId][x][y] (7.4.5).
     */
    synchronized int[][][][] getScalingFactor(long chromaArrayType)
    {
        if (scalingFactor != null && scalingFactorChromaArrayType == chromaArrayType)
            return scalingFactor;

        int[][][][] factor = new int[4][6][][];
        byte[][][][] scanOrder = Scans.getScanOrder();

        for (int matrixId = 0; matrixId < 6; matrixId++)
        {
            factor[0][matrixId] = new int[4][4];
            for (int i = 0; i < 16; i++)
                factor[0][matrixId][scanOrder[2][0][i][0]][scanOrder[2][0][i][1]] = scalingList[0][matrixId][i];

            factor[1][matrixId] = new int[8][8];
            factor[2][matrixId] = new int[16][16];
            for (int i = 0; i < 64; i++)
            {
                int x = scanOrder[3][0][i][0];
                int y = scanOrder[3][0][i][1];
                factor[1][matrixId][x][y] = scalingList[1][matrixId][i];
                for (int j = 0; j < 2; j++)
                    for (int k = 0; k < 2; k++)
                        factor[2][matrixId][x * 2 + k][y * 2 + j] = scalingList[2][matrixId][i];
            }
            factor[2][matrixId][0][0] = dcCoef[0][matrixId];

            // 32x32 chroma matrices are only coded for ChromaArrayType 3, derived from the 16x16 lists
            boolean coded32 = matrixId == 0 || matrixId == 3;
            int[] list32 = coded32 ? scalingList[3][matrixId] : scalingList[2][matrixId];
            factor[3][matrixId] = new int[32][32];
            for (int i = 0; i < 64; i++)
            {
                int x = scanOrder[3][0][i][0];
                int y = scanOrder[3][0][i][1];
                for (int j = 0; j < 4; j++)
                    for (int k = 0; k < 4; k++)
                        factor[3][matrixId][x * 4 + k][y * 4 + j] = list32[i];
            }
            factor[3][matrixId][0][0] = coded32 ? dcCoef[1][matrixId] : dcCoef[0][matrixId];
        }

        scalingFactor = factor;
        scalingFactorChromaArrayType = chromaArrayType;
        return factor;
    }
}
