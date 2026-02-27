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


public class video_parameter_set_rbsp extends NalUnit
{
    final byte vps_video_parameter_set_id; // max - 16
    final boolean vps_base_layer_internal_flag;
    final boolean vps_base_layer_available_flag;
    final byte vps_max_layers_minus1;
    final byte vps_max_sub_layers_minus1;
    final boolean vps_temporal_id_nesting_flag;
    final profile_tier_level profile_tier_level;

    final boolean vps_sub_layer_ordering_info_present_flag;

//    final /*UInt32*/ long[] vps_max_dec_pic_buffering_minus1;
//    final /*UInt32*/ long[] vps_max_num_reorder_pics;
//    final /*UInt32*/ long[] vps_max_latency_increase_plus1;

    final byte vps_max_layer_id;
    final /*UInt32*/ long vps_num_layer_sets_minus1;

    //final boolean[][] layer_id_included_flag;
    final boolean vps_timing_info_present_flag;
    /*UInt32*/ long vps_num_units_in_tick;
    /*UInt32*/ long vps_time_scale;
    boolean vps_poc_proportional_to_timing_flag;
    /*UInt32*/ long vps_num_ticks_poc_diff_one;
    /*UInt32*/ long vps_num_hrd_parameters;

    ///*UInt32*/ long[] hrd_layer_set_idx;
    boolean[] cprms_present_flag;
    final boolean vps_extension_flag;
    boolean vps_extension_data_flag;
    hrd_parameters hrd_parameters;

    video_parameter_set_rbsp(BitStreamWithNalSupport stream, /*UInt64*/long startPosition, int size)
    {
        super(stream, startPosition, size);

        vps_video_parameter_set_id = (byte) stream.read(4);             // u(4)
        vps_base_layer_internal_flag = stream.readFlag();            // u(1)
        vps_base_layer_available_flag = stream.readFlag();           // u(1)
        vps_max_layers_minus1 = (byte) stream.read(6);                  // u(6)
        vps_max_sub_layers_minus1 = (byte) stream.read(3);              // u(3)
        vps_temporal_id_nesting_flag = stream.readFlag();            // u(1)
        stream.skipBits(16); // vps_reserved_0xffff_16bits                  // u(16)

        profile_tier_level = new profile_tier_level(stream, true, vps_max_sub_layers_minus1 & 0xFF);

        vps_sub_layer_ordering_info_present_flag = stream.readFlag();// u(1)


        byte layers_count = (byte) (vps_sub_layer_ordering_info_present_flag ? 0 : vps_max_layers_minus1);
//        vps_max_dec_pic_buffering_minus1 = new /*UInt32*/long[(vps_max_layers_minus1 & 0xFF) + 1];
//        vps_max_num_reorder_pics = new /*UInt32*/long[(vps_max_layers_minus1 & 0xFF) + 1];
//        vps_max_latency_increase_plus1 = new /*UInt32*/long[(vps_max_layers_minus1 & 0xFF) + 1];

        for (int i = layers_count & 0xFF; i <= (vps_max_layers_minus1 & 0xFF); i++)
        {
            /*vps_max_dec_pic_buffering_minus1[i] =*/ stream.readUev();        // ue(v)
            /*vps_max_num_reorder_pics[i] =*/ stream.readUev();                // ue(v)
            /*vps_max_latency_increase_plus1[i] =*/ stream.readUev();          // ue(v)
        }

        vps_max_layer_id = (byte) stream.read(6);                       // u(6)
        vps_num_layer_sets_minus1 = stream.readUev();                // ue(v)

        //layer_id_included_flag = new boolean[(int) (((vps_num_layer_sets_minus1 & 0xFFFFFFFFL) + 2) & 0xFFFFFFFFL)][(vps_max_layer_id & 0xFF) + 1];

//        for (int i = 1; i <= (vps_num_layer_sets_minus1 & 0xFFFFFFFFL); i++)
//            for (int j = 0; j <= (vps_max_layer_id & 0xFF); j++)
//                layer_id_included_flag[i][j] = stream.readFlag();    // u(1)

        vps_timing_info_present_flag = stream.readFlag();            // u(1)

        if (vps_timing_info_present_flag)
        {
            vps_num_units_in_tick = stream.read(32) & 0xFFFFFFFFL;             // u(32)
            vps_time_scale = stream.read(32) & 0xFFFFFFFFL;                    // u(32)
            vps_poc_proportional_to_timing_flag = stream.readFlag(); // u(1)
            if (vps_poc_proportional_to_timing_flag)
            {
                vps_num_ticks_poc_diff_one = (((stream.readUev() & 0xFFFFFFFFL) + 1) & 0xFFFFFFFFL);       // ue(v)
            }

            vps_num_hrd_parameters = (stream.readUev());                   // ue(v)
            //hrd_layer_set_idx = new /*UInt32*/long[(int) (vps_num_hrd_parameters & 0xFFFFFFFFL)];
            cprms_present_flag = new boolean[(int) (vps_num_hrd_parameters & 0xFFFFFFFFL)];
            for (int i = 0; i < (vps_num_hrd_parameters & 0xFFFFFFFFL); i++)
            {
                /*hrd_layer_set_idx[i] =*/ stream.readUev();                   // ue(v)
                if (i > 0)
                {
                    cprms_present_flag[i] = stream.readFlag();       // u(1)
                }

                hrd_parameters = new hrd_parameters(stream, cprms_present_flag[i], vps_max_sub_layers_minus1 & 0xFF);
            }
        }

        vps_extension_flag = stream.readFlag();                      // u(1)
        if (vps_extension_flag)
        {
            while (stream.hasMoreRbspData(getEndPosition()))
                vps_extension_data_flag = stream.readFlag();         // u(1)
        }
        new rbsp_trailing_bits(stream);
    }

    @Override
    public final String toString()
    {
        return String.format("NAL Unit VPS \nProfile: %d\nLevel: %d", profile_tier_level.general_profile_idc,
                profile_tier_level.general_level_idc);
    }
}
