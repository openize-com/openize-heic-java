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


class pps_scc_extension
{
    final boolean pps_curr_pic_ref_enabled_flag;
    final boolean residual_adaptive_colour_transform_enabled_flag;
    final boolean pps_palette_predictor_initializers_present_flag;
    boolean pps_slice_act_qp_offsets_present_flag;
    int pps_act_y_qp_offset_plus5;
    int pps_act_cb_qp_offset_plus5;
    int pps_act_cr_qp_offset_plus3;
    /*UInt32*/ long pps_num_palette_predictor_initializers;
    boolean monochrome_palette_flag;
    byte luma_bit_depth_entry_minus8;
    byte chroma_bit_depth_entry_minus8;

    /// *UInt16*/int[][] pps_palette_predictor_initializer;

    public pps_scc_extension(BitStreamWithNalSupport stream)
    {
        pps_curr_pic_ref_enabled_flag = stream.readFlag();           // u(1)
        residual_adaptive_colour_transform_enabled_flag = stream.readFlag();// u(1)
        if (residual_adaptive_colour_transform_enabled_flag)
        {
            pps_slice_act_qp_offsets_present_flag = stream.readFlag();                                   // u(1)
            pps_act_y_qp_offset_plus5 = stream.readSev();                    // se(v)
            pps_act_cb_qp_offset_plus5 = stream.readSev();                   // se(v)
            pps_act_cr_qp_offset_plus3 = stream.readSev();                   // se(v)
        }
        pps_palette_predictor_initializers_present_flag = stream.readFlag();// u(1)
        if (pps_palette_predictor_initializers_present_flag)
        {
            pps_num_palette_predictor_initializers = stream.readUev();     // ue(v)
            if ((pps_num_palette_predictor_initializers & 0xFFFFFFFFL) > 0)
            {
                monochrome_palette_flag = stream.readFlag();         // u(1)
                luma_bit_depth_entry_minus8 = (byte) stream.readUev();       // ue(v)
                if (!monochrome_palette_flag)
                {
                    chroma_bit_depth_entry_minus8 = (byte) stream.readUev();
                }

                int numComps = monochrome_palette_flag ? 1 : 3;
                //pps_palette_predictor_initializer = new /*UInt16*/int[numComps][(int)(pps_num_palette_predictor_initializers & 0xFFFFFFFFL)];

                for (int comp = 0; comp < numComps; comp++)
                    for (int i = 0; i < (pps_num_palette_predictor_initializers & 0xFFFFFFFFL); i++)
                    {
                        // pps_palette_predictor_initializer[comp][i] =
                        stream.read(8 + ((comp == 0 ?        // u(v)
                                luma_bit_depth_entry_minus8 : chroma_bit_depth_entry_minus8) & 0xFF)); // & 0xFFFF;
                    }
            }
        }
    }

    public pps_scc_extension()
    {
        pps_curr_pic_ref_enabled_flag = false;
        residual_adaptive_colour_transform_enabled_flag = false;
        pps_slice_act_qp_offsets_present_flag = false;
        pps_act_y_qp_offset_plus5 = 0;
        pps_act_cb_qp_offset_plus5 = 0;
        pps_act_cr_qp_offset_plus3 = 0;
        pps_palette_predictor_initializers_present_flag = false;
        pps_num_palette_predictor_initializers = 0;
        monochrome_palette_flag = false;
        luma_bit_depth_entry_minus8 = 0;
        chroma_bit_depth_entry_minus8 = 0;
        //pps_palette_predictor_initializer = new /*UInt16*/int[3][0];
    }
}

