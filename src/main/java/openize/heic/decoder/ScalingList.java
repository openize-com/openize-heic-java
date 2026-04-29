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



class Scaling
{
    /**
     * <p>
     *     Returns the scaling table.
     * </p>
     * Table 7-3 – Specification of sizeId<br/>
     * <br/>
     * sizeId   Size of quantization matrix<br/>
     * 0        4x4<br/>
     * 1        8x8<br/>
     * 2        16x16<br/>
     * 3        32x32<br/>
     * <br/>
     * Table 7-4 – Specification of matrixId according to sizeId, prediction mode and colour component <br/>
     * <br/>
     * sizeId       CuPredMode   cIdx   matrixId<br/>
     * 0, 1, 2, 3   MODE_INTRA  0 (Y)   0<br/>
     * 0, 1, 2, 3   MODE_INTRA  1 (Cb)  1<br/>
     * 0, 1, 2, 3   MODE_INTRA  2 (Cr)  2<br/>
     * 0, 1, 2, 3   MODE_INTER  0 (Y)   3<br/>
     * 0, 1, 2, 3   MODE_INTER  1 (Cb)  4<br/>
     * 0, 1, 2, 3   MODE_INTER  2 (Cr)  5<br/>
     */
    static int[][][][] getScalingFactor(){ return scalingFactor; }

    /**
     * <p>
     *     Table 7-3 – Specification of sizeId
     * </p>
     */
    private static int[][][][] scalingFactor;

    /**
     * <p>
     *
     * </p>
     */
    private static final int[] scaling_list_4x4 = {
        16,16,16,16,
        16,16,16,16,
        16,16,16,16,
        16,16,16,16
    };

    private static final int[] scaling_list_8x8_intra = {
        16,16,16,16,16,16,16,16,
        16,16,17,16,17,16,17,18,
        17,18,18,17,18,21,19,20,
        21,20,19,21,24,22,22,24,
        24,22,22,24,25,25,27,30,
        27,25,25,29,31,35,35,31,
        29,36,41,44,41,36,47,54,
        54,47,65,70,65,88,88,115
    };

    private static final int[] scaling_list_8x8_inter = {
        16,16,16,16,16,16,16,16,
        16,16,17,17,17,17,17,18,
        18,18,18,18,18,20,20,20,
        20,20,20,20,24,24,24,24,
        24,24,24,24,25,25,25,25,
        25,25,25,28,28,28,28,28,
        28,33,33,33,33,33,41,41,
        41,41,54,54,54,71,71,91
    };

    /**
     * <p>
     *     Initializes the scaling tables based on {@code sps}
     * </p>
     */
    static synchronized void initiate(seq_parameter_set_rbsp sps)
    {
        if (scalingFactor != null)
        {
            return;
        }

        scalingFactor = new int[4][6][][];

        int sizeId = 0, x, y;

        for (int matrixId = 0; matrixId < 6; matrixId++)
        {
            scalingFactor[0][matrixId] = new int[1 << (2 + sizeId)][1 << (2 + sizeId)];

            for (int i = 0; i < 16; i++)
            {
                x = Scans.getScanOrder()[2][0][i][0] & 0xFF;
                y = Scans.getScanOrder()[2][0][i][1] & 0xFF;
                scalingFactor[0][matrixId][x][y] = scaling_list_4x4[i];
            }
        }

        sizeId = 1;
        for (int matrixId = 0; matrixId < 6; matrixId++)
        {
            scalingFactor[1][matrixId] = new int[1 << (2 + sizeId)][1 << (2 + sizeId)];

            for (int i = 0; i < 64; i++)
            {
                x = Scans.getScanOrder()[3][0][i][0] & 0xFF;
                y = Scans.getScanOrder()[3][0][i][1] & 0xFF;

                if (matrixId < 3)
                    scalingFactor[1][matrixId][x][y] = scaling_list_8x8_intra[i];
                else
                    scalingFactor[1][matrixId][x][y] = scaling_list_8x8_inter[i];
            }
        }

        sizeId = 2;
        for (int matrixId = 0; matrixId < 6; matrixId++)
        {
            scalingFactor[2][matrixId] = new int[1 << (2 + sizeId)][1 << (2 + sizeId)];

            for (int i = 0; i < 64; i++)
            {
                x = Scans.getScanOrder()[3][0][i][0] & 0xFF;
                y = Scans.getScanOrder()[3][0][i][1] & 0xFF;

                for (int j = 0; j < 2; j++)
                {
                    for (int k = 0; k < 2; k++)
                    {
                        if (matrixId < 3)
                            scalingFactor[2][matrixId][x * 2 + k][y * 2 + j] = scaling_list_8x8_intra[i];
                        else
                            scalingFactor[2][matrixId][x * 2 + k][y * 2 + j] = scaling_list_8x8_inter[i];
                    }
                }
            }

            if (sps.sps_scaling_list_data_present_flag)
                scalingFactor[2][matrixId][0][0] = sps.scaling_list_data.scaling_list_dc_coef_minus8[0][matrixId] + 8;
        }

        sizeId = 3;
        for (int matrixId = 0; matrixId < 6; matrixId++)
        {
            scalingFactor[3][matrixId] = new int[1 << (2 + sizeId)][1 << (2 + sizeId)];

            for (int i = 0; i < 64; i++)
            {
                x = Scans.getScanOrder()[3][0][i][0] & 0xFF;
                y = Scans.getScanOrder()[3][0][i][1] & 0xFF;

                for (int j = 0; j < 4; j++)
                {
                    for (int k = 0; k < 4; k++)
                    {
                        if (matrixId < 3)
                            scalingFactor[3][matrixId][x * 4 + k][y * 4 + j] = scaling_list_8x8_intra[i];
                        else
                            scalingFactor[3][matrixId][x * 4 + k][y * 4 + j] = scaling_list_8x8_inter[i];
                    }
                }
            }

            if (sps.sps_scaling_list_data_present_flag)
            {
                if (matrixId == 0 || matrixId == 3)
                    scalingFactor[3][matrixId][0][0] = sps.scaling_list_data.scaling_list_dc_coef_minus8[1][matrixId] + 8;
                else
                    scalingFactor[3][matrixId][0][0] = sps.scaling_list_data.scaling_list_dc_coef_minus8[0][matrixId] + 8;
            }
        }
    }
}
