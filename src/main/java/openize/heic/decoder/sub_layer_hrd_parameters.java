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



class sub_layer_hrd_parameters
{
//    private final /*UInt32*/long[] bit_rate_value_minus1;
//    private final /*UInt32*/long[] cpb_size_value_minus1;
//    private final /*UInt32*/long[] cpb_size_du_value_minus1;
//    private final /*UInt32*/long[] bit_rate_du_value_minus1;
//    private final boolean[] cbr_flag;

    public sub_layer_hrd_parameters(
            BitStreamWithNalSupport stream,
            /*UInt32*/long CpbCnt,
            boolean sub_pic_hrd_params_present_flag)
    {
//        bit_rate_value_minus1 = new /*UInt32*/long[(int)(CpbCnt & 0xFFFFFFFFL)];
//        cpb_size_value_minus1 = new /*UInt32*/long[(int)(CpbCnt & 0xFFFFFFFFL)];
//        cpb_size_du_value_minus1 = new /*UInt32*/long[(int)(CpbCnt & 0xFFFFFFFFL)];
//        bit_rate_du_value_minus1 = new /*UInt32*/long[(int)(CpbCnt & 0xFFFFFFFFL)];
//        cbr_flag = new boolean[(int)(CpbCnt & 0xFFFFFFFFL)];

        for (int i = 0; i < (CpbCnt & 0xFFFFFFFFL); i++)
        {
            /*bit_rate_value_minus1[i] =*/ stream.readUev();                   // ue(v)
            /*cpb_size_value_minus1[i] =*/ stream.readUev();                   // ue(v)

            if (sub_pic_hrd_params_present_flag)
            {
                /*cpb_size_du_value_minus1[i] =*/ stream.readUev();            // ue(v)
                /*bit_rate_du_value_minus1[i] =*/ stream.readUev();            // ue(v)
            }

            /*cbr_flag[i] =*/ stream.readFlag();                               // u(1)
        }
    }
}
