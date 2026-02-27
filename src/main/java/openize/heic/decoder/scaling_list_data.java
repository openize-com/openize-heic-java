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


class scaling_list_data
{
    // boolean[][] scaling_list_pred_mode_flag;
    // /*UInt32*/ int[][] scaling_list_pred_matrix_id_delta;
    int[][] scaling_list_dc_coef_minus8;
    //byte[][][] ScalingList;

    public scaling_list_data(BitStreamWithNalSupport stream)
    {
        // scaling_list_pred_mode_flag = new boolean[4][6];
        //scaling_list_pred_matrix_id_delta = new /*UInt32*/int[4][6];
          scaling_list_dc_coef_minus8 = new int[2][];
        //  ScalingList = new /*Byte*/byte[4][6][];

        for (int sizeId = 0; sizeId < 4; sizeId++)
        {
            if (sizeId > 1)
                scaling_list_dc_coef_minus8[sizeId - 2] = new int[6];

            final int stepValue = (sizeId == 3) ? 3 : 1;
            for (int matrixId = 0; matrixId < 6; matrixId += stepValue)
            {
                /*scaling_list_pred_mode_flag[sizeId][matrixId]*/
                boolean isFlag = stream.readFlag();
                //if (!scaling_list_pred_mode_flag[sizeId][matrixId])
                if (!isFlag)
                {
                    /*scaling_list_pred_matrix_id_delta[sizeId][matrixId] = (int)*/
                    stream.readUev();
                }
                else
                {
                    int nextCoef = 8;
                    int coefNum = 1 << (4 + (sizeId << 1));
                    coefNum = Math.min(coefNum, 64);
                    //ScalingList[sizeId][matrixId] = new /*Byte*/byte[coefNum];

                    if (sizeId > 1)
                    {
                        scaling_list_dc_coef_minus8[sizeId-2][matrixId] = stream.readSev();
                        nextCoef = scaling_list_dc_coef_minus8[sizeId-2][matrixId];
                    }

                    for (int i = 0; i < coefNum; i++)
                    {
                        int scaling_list_delta_coef = stream.readSev();
                        nextCoef = (nextCoef + scaling_list_delta_coef + 256) % 256;
                        //ScalingList[sizeId][matrixId][i] = (/*Byte*/byte)nextCoef;
                    }
                }
            }
        }
    }
}
