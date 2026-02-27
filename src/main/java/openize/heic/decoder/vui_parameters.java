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


class vui_parameters
{
    boolean aspect_ratio_info_present_flag;
    byte aspect_ratio_idc; // 17 to 254 == 0
    /*UInt16*/ int sar_width;
    /*UInt16*/ int sar_height;
    boolean overscan_info_present_flag;
    boolean overscan_appropriate_flag;

    boolean video_signal_type_present_flag = false;
    byte video_format = 5;
    boolean video_full_range_flag = false;

    boolean colour_description_present_flag = false;
    byte colour_primaries = 2;
    byte transfer_characteristics = 2;
    byte matrix_coeffs = 2;

    boolean chroma_loc_info_present_flag;
    /*UInt32*/ long chroma_sample_loc_type_top_field;
    /*UInt32*/ long chroma_sample_loc_type_bottom_field;
    boolean neutral_chroma_indication_flag;
    boolean field_seq_flag;
    boolean frame_field_info_present_flag;
    boolean default_display_window_flag;
    /*UInt32*/ long def_disp_win_left_offset;
    /*UInt32*/ long def_disp_win_right_offset;
    /*UInt32*/ long def_disp_win_top_offset;
    /*UInt32*/ long def_disp_win_bottom_offset;
    boolean vui_timing_info_present_flag;
    /*UInt32*/ long vui_num_units_in_tick;
    /*UInt32*/ long vui_time_scale;
    boolean vui_poc_proportional_to_timing_flag;
    /*UInt32*/ long vui_num_ticks_poc_diff_one_minus1;
    boolean vui_hrd_parameters_present_flag;
    //hrd_parameters hrd_parameters;
    boolean bitstream_restriction_flag;
    boolean tiles_fixed_structure_flag;
    boolean motion_vectors_over_pic_boundaries_flag;
    boolean restricted_ref_pic_lists_flag;
    /*UInt32*/ long min_spatial_segmentation_idc;
    /*UInt32*/ long max_bytes_per_pic_denom;
    /*UInt32*/ long max_bits_per_min_cu_denom;
    /*UInt32*/ long log2_max_mv_length_horizontal;
    /*UInt32*/ long log2_max_mv_length_vertical;

    public vui_parameters(BitStreamWithNalSupport stream, seq_parameter_set_rbsp sps)
    {
        aspect_ratio_info_present_flag = stream.readFlag();                // u(1)
        if (aspect_ratio_info_present_flag)
        {
            aspect_ratio_idc = (byte) stream.read(8);                   // u(8)
            if ((aspect_ratio_idc & 0xFF) == 255)
            {
                sar_width = stream.read(16) & 0xFFFF;                    // u(16)
                sar_height = stream.read(16) & 0xFFFF;                   // u(16)
            }
        }

        overscan_info_present_flag = stream.readFlag();                // u(1)
        if (overscan_info_present_flag)
        {
            overscan_appropriate_flag = stream.readFlag();             // u(1)
        }

        video_signal_type_present_flag = stream.readFlag();            // u(1)
        if (video_signal_type_present_flag)
        {
            video_format = (byte) stream.read(3);                       // u(3)
            video_full_range_flag = stream.readFlag();                 // u(1)
            colour_description_present_flag = stream.readFlag();       // u(1)

            if (colour_description_present_flag)
            {
                colour_primaries = (byte) stream.read(8);               // u(8)
                transfer_characteristics = (byte) stream.read(8);       // u(8)
                matrix_coeffs = (byte) stream.read(8);                  // u(8)
            }
        }

        chroma_loc_info_present_flag = stream.readFlag();                  // u(1)
        if (chroma_loc_info_present_flag)
        {
            chroma_sample_loc_type_top_field = stream.readUev();           // ue(v)
            chroma_sample_loc_type_bottom_field = stream.readUev();        // ue(v)
        }

        neutral_chroma_indication_flag = stream.readFlag();                // u(1)
        field_seq_flag = stream.readFlag();                                // u(1)
        frame_field_info_present_flag = stream.readFlag();                 // u(1)
        default_display_window_flag = stream.readFlag();                   // u(1)
        if (default_display_window_flag)
        {
            def_disp_win_left_offset = stream.readUev();                   // ue(v)
            def_disp_win_right_offset = stream.readUev();                  // ue(v)
            def_disp_win_top_offset = stream.readUev();                    // ue(v)
            def_disp_win_bottom_offset = stream.readUev();                 // ue(v)
        }

        vui_timing_info_present_flag = stream.readFlag();                  // u(1)
        if (vui_timing_info_present_flag)
        {
            vui_num_units_in_tick = stream.read(32) & 0xFFFFFFFFL;             // u(32)
            vui_time_scale = stream.read(32) & 0xFFFFFFFFL;                    // u(32)
            vui_poc_proportional_to_timing_flag = stream.readFlag();       // u(1)
            if (vui_poc_proportional_to_timing_flag)
            {
                vui_num_ticks_poc_diff_one_minus1 = stream.readUev();      // ue(v)
            }

            vui_hrd_parameters_present_flag = stream.readFlag();           // u(1)
            if (vui_hrd_parameters_present_flag)
                /*hrd_parameters =*/
            {
                new hrd_parameters(stream, true, sps.sps_max_sub_layers_minus1 & 0xFF);
            }
        }

        bitstream_restriction_flag = stream.readFlag();                    // u(1)
        if (bitstream_restriction_flag)
        {
            tiles_fixed_structure_flag = stream.readFlag();                // u(1)
            motion_vectors_over_pic_boundaries_flag = stream.readFlag();   // u(1)
            restricted_ref_pic_lists_flag = stream.readFlag();             // u(1)
            min_spatial_segmentation_idc = stream.readUev();               // ue(v)
            max_bytes_per_pic_denom = stream.readUev();                    // ue(v)
            max_bits_per_min_cu_denom = stream.readUev();                  // ue(v)
            log2_max_mv_length_horizontal = stream.readUev();              // ue(v)
            log2_max_mv_length_vertical = stream.readUev();                // ue(v)
        }
    }

    public vui_parameters()
    {
        // Do nothing
    }
}
