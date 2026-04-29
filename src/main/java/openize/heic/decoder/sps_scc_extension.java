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


class sps_scc_extension
{
    final boolean sps_curr_pic_ref_enabled_flag;
    final boolean palette_mode_enabled_flag;
    /*UInt32*/ long palette_max_size;
    /*UInt32*/ long delta_palette_max_predictor_size;
    boolean sps_palette_predictor_initializers_present_flag;
    /*UInt32*/ long sps_num_palette_predictor_initializers_minus1;
    // /*UInt16*/ int[][] sps_palette_predictor_initializer;
    byte motion_vector_resolution_control_idc;
    boolean intra_boundary_filtering_disabled_flag;

    public sps_scc_extension(BitStreamWithNalSupport stream, /*UInt32*/long chroma_format_idc, byte bit_depth_luma_minus8, byte bit_depth_chroma_minus8)
    {
        sps_curr_pic_ref_enabled_flag = stream.readFlag();           // u(1)
        palette_mode_enabled_flag = stream.readFlag();               // u(1)
        if (palette_mode_enabled_flag)
        {
            palette_max_size = stream.readUev();                           // ue(v)
            delta_palette_max_predictor_size = stream.readUev();           // ue(v)
            sps_palette_predictor_initializers_present_flag = stream.readFlag(); // u(1)
            if (sps_palette_predictor_initializers_present_flag)
            {
                sps_num_palette_predictor_initializers_minus1 = stream.readUev(); // ue(v)
                assert ((((sps_num_palette_predictor_initializers_minus1 & 0xFFFFFFFFL) + 1) & 0xFFFFFFFFL) <= (((palette_max_size & 0xFFFFFFFFL) + (delta_palette_max_predictor_size & 0xFFFFFFFFL)) & 0xFFFFFFFFL));

                int numComps = ((chroma_format_idc & 0xFFFFFFFFL) == 0) ? 1 : 3;

                // sps_palette_predictor_initializer = new /*UInt16*/int[numComps][(int) (((sps_num_palette_predictor_initializers_minus1 & 0xFFFFFFFFL) + 1) & 0xFFFFFFFFL)];

                for (int comp = 0; comp < numComps; comp++)
                    for (int i = 0; i <= (sps_num_palette_predictor_initializers_minus1 & 0xFFFFFFFFL); i++)
                    {
                        /*sps_palette_predictor_initializer[comp][i] =*/        // u(v)
                        stream.read(((comp == 0 ? bit_depth_luma_minus8 : bit_depth_chroma_minus8) & 0xFF) + 8); // & 0xFFFF;
                    }
            }
        }
        motion_vector_resolution_control_idc = (byte) stream.read(2);   // u(2)
        intra_boundary_filtering_disabled_flag = stream.readFlag();  // u(1)
    }

    public sps_scc_extension(/*UInt32*/long chroma_format_idc)
    {
        sps_curr_pic_ref_enabled_flag = false;
        palette_mode_enabled_flag = false;
        palette_max_size = 0;
        delta_palette_max_predictor_size = 0;
        sps_palette_predictor_initializers_present_flag = false;
        sps_num_palette_predictor_initializers_minus1 = 0;

        // int numComps = ((chroma_format_idc & 0xFFFFFFFFL) == 0) ? 1 : 3;
        //sps_palette_predictor_initializer = new /*UInt16*/int[numComps][(int) (((sps_num_palette_predictor_initializers_minus1 & 0xFFFFFFFFL) + 1) & 0xFFFFFFFFL)];

        motion_vector_resolution_control_idc = 0;
        intra_boundary_filtering_disabled_flag = false;
    }
}
