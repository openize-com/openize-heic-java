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



class pps_range_extension
{
    /*UInt32*/long log2_max_transform_skip_block_size_minus2;
    final boolean cross_component_prediction_enabled_flag;
    final boolean chroma_qp_offset_list_enabled_flag;
    /*UInt32*/long diff_cu_chroma_qp_offset_depth;
    int chroma_qp_offset_list_len_minus1;
    int[] cb_qp_offset_list;
    int[] cr_qp_offset_list;
    final /*UInt32*/long log2_sao_offset_scale_luma;
    final /*UInt32*/long log2_sao_offset_scale_chroma;

    public pps_range_extension(BitStreamWithNalSupport stream, boolean transform_skip_enabled_flag)
    {
        if (transform_skip_enabled_flag)
            log2_max_transform_skip_block_size_minus2 = stream.readUev();     // ue(v)
        cross_component_prediction_enabled_flag = stream.readFlag(); // u(1)
        chroma_qp_offset_list_enabled_flag = stream.readFlag();      // u(1)
        if (chroma_qp_offset_list_enabled_flag)
        {
            diff_cu_chroma_qp_offset_depth = stream.readUev();             // ue(v)
            chroma_qp_offset_list_len_minus1 = (int)stream.readUev();              // ue(v)
            cb_qp_offset_list = new int[chroma_qp_offset_list_len_minus1 + 1];
            cr_qp_offset_list = new int[chroma_qp_offset_list_len_minus1 + 1];
            for (int i = 0; i <= chroma_qp_offset_list_len_minus1; i++)
            {
                cb_qp_offset_list[i] = stream.readSev();                   // se(v)
                cr_qp_offset_list[i] = stream.readSev();                   // se(v)
            }
        }
        log2_sao_offset_scale_luma = stream.readUev();                     // ue(v)
        log2_sao_offset_scale_chroma = stream.readUev();                   // ue(v)
    }
    public pps_range_extension()
    {
        log2_max_transform_skip_block_size_minus2 = 0;
        cross_component_prediction_enabled_flag = false;
        chroma_qp_offset_list_enabled_flag = false;
        diff_cu_chroma_qp_offset_depth = 0;
        chroma_qp_offset_list_len_minus1 = -1;
        log2_sao_offset_scale_luma = 0;
        log2_sao_offset_scale_chroma = 0;
    }
}

