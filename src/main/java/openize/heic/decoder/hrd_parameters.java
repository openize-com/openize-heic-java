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


class hrd_parameters
{
    private boolean nal_hrd_parameters_present_flag;
    private boolean vcl_hrd_parameters_present_flag;
    private boolean sub_pic_hrd_params_present_flag;
    //private byte tick_divisor_minus2;
    //private byte du_cpb_removal_delay_increment_length_minus1;
//    private byte sub_pic_cpb_params_in_pic_timing_sei_flag;
//    private byte dpb_output_delay_du_length_minus1;
//    private byte bit_rate_scale;
//    private byte cpb_size_scale;
//    private byte initial_cpb_removal_delay_length_minus1;
//    private byte au_cpb_removal_delay_length_minus1;
//    private byte dpb_output_delay_length_minus1;
//    private byte cpb_size_du_scale;
    //private final boolean[] fixed_pic_rate_general_flag;
    //private final boolean[] fixed_pic_rate_within_cvs_flag;
    //private final /*UInt32*/long[] elemental_duration_in_tc_minus1;
    //private final boolean[] low_delay_hrd_flag;
    //private final /*UInt32*/long[] cpb_cnt_minus1;
    //private final sub_layer_hrd_parameters[] sub_layer_hrd_parameters;

    public hrd_parameters(BitStreamWithNalSupport stream, boolean commonInfPresentFlag, int maxNumSubLayersMinus1)
    {
        if (commonInfPresentFlag)
        {
            nal_hrd_parameters_present_flag = stream.readFlag();           // u(1)
            vcl_hrd_parameters_present_flag = stream.readFlag();           // u(1)
            if (nal_hrd_parameters_present_flag || vcl_hrd_parameters_present_flag)
            {
                sub_pic_hrd_params_present_flag = stream.readFlag();       // u(1)
                if (sub_pic_hrd_params_present_flag)
                {
                    /*tick_divisor_minus2 = (byte)*/
                    stream.read(8);        // u(8)
                    /*du_cpb_removal_delay_increment_length_minus1 = (byte)*/
                    stream.read(5);                          // u(5)
                    /*sub_pic_cpb_params_in_pic_timing_sei_flag = (byte)*/
                    stream.read(8);                          // u(1)
                    /*dpb_output_delay_du_length_minus1 = (byte)*/
                    stream.read(5);                          // u(5)
                }

                /*bit_rate_scale = (byte)*/
                stream.read(4);                 // u(4)
                /*cpb_size_scale = (byte)*/
                stream.read(4);                 // u(4)

                if (sub_pic_hrd_params_present_flag)
                {
                    /*cpb_size_du_scale = (byte)*/
                    stream.read(4);          // u(4)
                }
                //initial_cpb_removal_delay_length_minus1 = (byte)
                stream.read(5);                              // u(5)
                //au_cpb_removal_delay_length_minus1 = (byte)
                stream.read(5);                              // u(5)
                // dpb_output_delay_length_minus1 = (byte)
                stream.read(5);                              // u(5)
            }
        }

        boolean[] fixed_pic_rate_general_flag = new boolean[maxNumSubLayersMinus1 + 1];
        boolean[] fixed_pic_rate_within_cvs_flag = new boolean[maxNumSubLayersMinus1 + 1];
        // elemental_duration_in_tc_minus1 = new /*UInt32*/long[maxNumSubLayersMinus1 + 1];
        boolean[] low_delay_hrd_flag = new boolean[maxNumSubLayersMinus1 + 1];
       // cpb_cnt_minus1 = new /*UInt32*/long[maxNumSubLayersMinus1 + 1];
        //sub_layer_hrd_parameters = new sub_layer_hrd_parameters[maxNumSubLayersMinus1 + 1];

        for (int i = 0; i <= maxNumSubLayersMinus1; i++)
        {
            fixed_pic_rate_general_flag[i] = stream.readFlag();            // u(1)
            if (!fixed_pic_rate_general_flag[i])
            {
                fixed_pic_rate_within_cvs_flag[i] = stream.readFlag();     // u(1)
            }

            if (fixed_pic_rate_within_cvs_flag[i])
                /*elemental_duration_in_tc_minus1[i] =*/
            {
                stream.readUev();     // ue(v)
            }
            else
            {
                low_delay_hrd_flag[i] = stream.readFlag();                 // u(1)
            }

            long cpb_cnt_minus1 = 0;
            if (!low_delay_hrd_flag[i])
                /*cpb_cnt_minus1[i] =*/
            {
                cpb_cnt_minus1 = stream.readUev();                      // ue(v)
            }

            /*UInt32*/
            long CpbCnt = ((cpb_cnt_minus1 & 0xFFFFFFFFL) + 1) & 0xFFFFFFFFL;
            ///*UInt32*/long CpbCnt = ((cpb_cnt_minus1[i] & 0xFFFFFFFFL) + 1) & 0xFFFFFFFFL;

            if (nal_hrd_parameters_present_flag)
                /*sub_layer_hrd_parameters[i] = */
            {
                new sub_layer_hrd_parameters(stream, CpbCnt, sub_pic_hrd_params_present_flag);
            }

            if (vcl_hrd_parameters_present_flag)
                /*sub_layer_hrd_parameters[i] = */
            {
                new sub_layer_hrd_parameters(stream, CpbCnt, sub_pic_hrd_params_present_flag);
            }
        }
    }
}
