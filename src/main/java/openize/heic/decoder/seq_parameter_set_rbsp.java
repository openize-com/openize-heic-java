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

import java.util.HashMap;


public class seq_parameter_set_rbsp extends NalUnit
{
    public final video_parameter_set_rbsp vps;
    final byte sps_video_parameter_set_id;
    final byte sps_max_sub_layers_minus1;
    final boolean sps_temporal_id_nesting_flag;
    final profile_tier_level profile_tier_level;
    final byte sps_seq_parameter_set_id; // max - 16
    final /*UInt32*/ long chroma_format_idc;
    final /*UInt32*/ long pic_width_in_luma_samples;
    final /*UInt32*/ long pic_height_in_luma_samples;
    final boolean conformance_window_flag;
    final byte bit_depth_luma_minus8;
    final byte bit_depth_chroma_minus8;
    final byte log2_max_pic_order_cnt_lsb_minus4;
    final boolean sps_sub_layer_ordering_info_present_flag;
    //final /*UInt32*/ long[] sps_max_dec_pic_buffering_minus1;
//    final /*UInt32*/ long[] sps_max_num_reorder_pics;
//    final /*UInt32*/ long[] sps_max_latency_increase_plus1;
    final byte log2_min_luma_coding_block_size_minus3;
    final byte log2_diff_max_min_luma_coding_block_size;
    final byte log2_min_luma_transform_block_size_minus2;
    final byte log2_diff_max_min_luma_transform_block_size;
    final /*UInt32*/ long max_transform_hierarchy_depth_inter;
    final /*UInt32*/ long max_transform_hierarchy_depth_intra;
    final boolean scaling_list_enabled_flag;
    final boolean amp_enabled_flag;
    final boolean sample_adaptive_offset_enabled_flag;
    final boolean pcm_enabled_flag;
    final /*UInt32*/ long num_short_term_ref_pic_sets;
    final HashMap<Integer, st_ref_pic_set> st_ref_pic_sets;
    final boolean long_term_ref_pics_present_flag;
    /*UInt32*/ long num_long_term_ref_pics_sps;
    int[] lt_ref_pic_poc_lsb_sps;
    boolean[] used_by_curr_pic_lt_sps_flag;
    final boolean sps_temporal_mvp_enabled_flag;
    final boolean strong_intra_smoothing_enabled_flag;
    final boolean vui_parameters_present_flag;
    final boolean sps_extension_present_flag;
    boolean sps_range_extension_flag;
    boolean sps_multilayer_extension_flag;
    boolean sps_3d_extension_flag;
    boolean sps_scc_extension_flag;
    byte sps_extension_4bits;
    final sps_range_extension sps_range_ext;
    sps_multilayer_extension sps_multilayer_ext;
    sps_3d_extension sps_3d_ext;
    final sps_scc_extension sps_scc_ext;
    boolean sps_extension_data_flag;
    final vui_parameters vui_parameters;
    boolean separate_colour_plane_flag;
    /*UInt32*/ long conf_win_left_offset;
    /*UInt32*/ long conf_win_right_offset;
    /*UInt32*/ long conf_win_top_offset;
    /*UInt32*/ long conf_win_bottom_offset;
    boolean sps_scaling_list_data_present_flag;
    scaling_list_data scaling_list_data;
    byte pcm_sample_bit_depth_luma;
    byte pcm_sample_bit_depth_chroma;
    byte log2_min_pcm_luma_coding_block_size_minus3;
    byte log2_diff_max_min_pcm_luma_coding_block_size;
    boolean pcm_loop_filter_disabled_flag;

    public seq_parameter_set_rbsp(BitStreamWithNalSupport stream, /*UInt64*/long startPosition, int size)
    {
        super(stream, startPosition, size);

        sps_video_parameter_set_id = (byte) stream.read(4);                  // u(4)

        vps = stream.getContext().getVPS().get(sps_video_parameter_set_id & 0xFF);

        sps_max_sub_layers_minus1 = (byte) stream.read(3);                   // u(3)
        sps_temporal_id_nesting_flag = stream.readFlag();                   // u(1)

        profile_tier_level = new profile_tier_level(stream, true, sps_max_sub_layers_minus1 & 0xFF);

        sps_seq_parameter_set_id = (byte) stream.readUev();                       // ue(v)
        chroma_format_idc = stream.readUev();                              // ue(v)
        if ((chroma_format_idc & 0xFFFFFFFFL) == 3)
        {
            separate_colour_plane_flag = stream.readFlag();                // u(1)
        }

        pic_width_in_luma_samples = stream.readUev();                      // ue(v)
        pic_height_in_luma_samples = stream.readUev();                     // ue(v)
        conformance_window_flag = stream.readFlag();                       // u(1)
        if (conformance_window_flag)
        {
            conf_win_left_offset = stream.readUev();                       // ue(v)
            conf_win_right_offset = stream.readUev();                      // ue(v)
            conf_win_top_offset = stream.readUev();                        // ue(v)
            conf_win_bottom_offset = stream.readUev();                     // ue(v)
        }

        bit_depth_luma_minus8 = (byte) stream.readUev();                    // ue(v)
        bit_depth_chroma_minus8 = (byte) stream.readUev();                  // ue(v)
        log2_max_pic_order_cnt_lsb_minus4 = (byte) stream.readUev();        // ue(v)
        sps_sub_layer_ordering_info_present_flag = stream.readFlag();      // u(1)

        int layers_count = (sps_sub_layer_ordering_info_present_flag ? 0 : sps_max_sub_layers_minus1) & 0xFF;
        //sps_max_dec_pic_buffering_minus1 = new /*UInt32*/long[(sps_max_sub_layers_minus1 & 0xFF) + 1];
//        sps_max_num_reorder_pics = new /*UInt32*/long[(sps_max_sub_layers_minus1 & 0xFF) + 1];
//        sps_max_latency_increase_plus1 = new /*UInt32*/long[(sps_max_sub_layers_minus1 & 0xFF) + 1];
        for (int i = layers_count; i <= (sps_max_sub_layers_minus1 & 0xFF); i++)
        {
            /*sps_max_dec_pic_buffering_minus1[i] =*/ stream.readUev();         // ue(v)
            /*sps_max_num_reorder_pics[i] =*/ stream.readUev();                // ue(v)
            /*sps_max_latency_increase_plus1[i] =*/ stream.readUev();          // ue(v)
        }

        log2_min_luma_coding_block_size_minus3 = (byte) stream.readUev();          // ue(v)
        log2_diff_max_min_luma_coding_block_size = (byte) stream.readUev();       // ue(v)
        log2_min_luma_transform_block_size_minus2 = (byte) stream.readUev();      // ue(v)
        log2_diff_max_min_luma_transform_block_size = (byte) stream.readUev();// ue(v)
        max_transform_hierarchy_depth_inter = stream.readUev();            // ue(v)
        max_transform_hierarchy_depth_intra = stream.readUev();            // ue(v)

        scaling_list_enabled_flag = stream.readFlag();               // u(1)
        if (scaling_list_enabled_flag)
        {
            sps_scaling_list_data_present_flag = stream.readFlag();  // u(1)
            if (sps_scaling_list_data_present_flag)
            {
                scaling_list_data = new scaling_list_data(stream);
            }
        }

        amp_enabled_flag = stream.readFlag();                        // u(1)
        sample_adaptive_offset_enabled_flag = stream.readFlag();     // u(1)

        pcm_enabled_flag = stream.readFlag();                        // u(1)
        if (pcm_enabled_flag)
        {
            pcm_sample_bit_depth_luma = (byte) (stream.read(4) + 1);    // u(4)
            pcm_sample_bit_depth_chroma = (byte) (stream.read(4) + 1);  // u(4)
            log2_min_pcm_luma_coding_block_size_minus3 = (byte) stream.readUev(); // ue(v)
            log2_diff_max_min_pcm_luma_coding_block_size = (byte) stream.readUev();//ue(v)
            pcm_loop_filter_disabled_flag = stream.readFlag();       // u(1)
        }

        num_short_term_ref_pic_sets = stream.readUev();                    // ue(v)

        st_ref_pic_sets = new HashMap<>();
        for (/*UInt32*/long i = 0; (i & 0xFFFFFFFFL) < (num_short_term_ref_pic_sets & 0xFFFFFFFFL); i++)
            st_ref_pic_sets.put((int) i, new st_ref_pic_set(stream, this, i));

        long_term_ref_pics_present_flag = stream.readFlag();         // u(1)
        if (long_term_ref_pics_present_flag)
        {
            num_long_term_ref_pics_sps = stream.readUev();                 // ue(v)
            lt_ref_pic_poc_lsb_sps = new int[(int) (num_long_term_ref_pics_sps & 0xFFFFFFFFL)];
            used_by_curr_pic_lt_sps_flag = new boolean[(int) (num_long_term_ref_pics_sps & 0xFFFFFFFFL)];
            for (int i = 0; i < (num_long_term_ref_pics_sps & 0xFFFFFFFFL); i++)
            {
                lt_ref_pic_poc_lsb_sps[i] =
                        stream.read((log2_max_pic_order_cnt_lsb_minus4 & 0xFF) + 1); // u(v)
                used_by_curr_pic_lt_sps_flag[i] = stream.readFlag(); // u(1)
            }
        }

        sps_temporal_mvp_enabled_flag = stream.readFlag();           // u(1)
        strong_intra_smoothing_enabled_flag = stream.readFlag();     // u(1)
        vui_parameters_present_flag = stream.readFlag();             // u(1)
        if (vui_parameters_present_flag)
        {
            vui_parameters = new vui_parameters(stream, this);
        }
        else
        {
            vui_parameters = new vui_parameters();
        }

        sps_extension_present_flag = stream.readFlag();              // u(1)
        if (sps_extension_present_flag)
        {
            sps_range_extension_flag = stream.readFlag();            // u(1)
            sps_multilayer_extension_flag = stream.readFlag();       // u(1)
            sps_3d_extension_flag = stream.readFlag();               // u(1)
            sps_scc_extension_flag = stream.readFlag();              // u(1)
            sps_extension_4bits = (byte) stream.read(4);                // u(4)
        }

        sps_range_ext = sps_range_extension_flag ?
                new sps_range_extension(stream) :
                new sps_range_extension();


        if (sps_multilayer_extension_flag)
        {
            sps_multilayer_ext = new sps_multilayer_extension(stream); /* specified in Annex F */
        }

        if (sps_3d_extension_flag)
        {
            sps_3d_ext = new sps_3d_extension(stream); /* specified in Annex I */
        }

        sps_scc_ext = sps_range_extension_flag ?
                new sps_scc_extension(stream, chroma_format_idc, bit_depth_luma_minus8, bit_depth_chroma_minus8) :
                new sps_scc_extension(chroma_format_idc);

        if ((sps_extension_4bits & 0xFF) > 0)
        {
            while (stream.hasMoreRbspData(getEndPosition()))
                sps_extension_data_flag = stream.readFlag();         // u(1)
        }

        new rbsp_trailing_bits(stream);
    }

    @Override
    public String toString()
    {
        return
                "NAL Unit SPS " + String.format("\nSize: %dx%d ", pic_width_in_luma_samples, pic_height_in_luma_samples) +
                        String.format("\nBit depth: %d %d", (bit_depth_luma_minus8 & 0xFF) + 8, (bit_depth_chroma_minus8 & 0xFF) + 8);
    }

    /**
     * <p>
     * 0 - mono, 1 - 420, 2 - 422, 3 - 444
     * </p>
     */
    public final /*UInt32*/long getChromaArrayType()
    {
        if (!separate_colour_plane_flag)
        {
            return chroma_format_idc;
        }
        return 0;
    }

    public final /*UInt32*/long numNegativePics(int stRpsIdx)
    {
        return st_ref_pic_sets.get(stRpsIdx).num_negative_pics;
    }

    public final /*UInt32*/long numPositivePics(int stRpsIdx)
    {
        return st_ref_pic_sets.get(stRpsIdx).num_positive_pics;
    }

    public final /*UInt32*/long numDeltaPocs(int stRpsIdx)
    {
        return (((numNegativePics(stRpsIdx) & 0xFFFFFFFFL) + (numPositivePics(stRpsIdx) & 0xFFFFFFFFL)) & 0xFFFFFFFFL);
    }

    public final boolean[] usedByCurrPicS0(int stRpsIdx)
    {
        return st_ref_pic_sets.get(stRpsIdx).used_by_curr_pic_s0_flag;
    }

    public final boolean[] usedByCurrPicS1(int stRpsIdx)
    {
        return st_ref_pic_sets.get(stRpsIdx).used_by_curr_pic_s1_flag;
    }

    public final long deltaPocS0(int stRpsIdx, int i)
    {
        return i == 0 ?
                -(((st_ref_pic_sets.get(stRpsIdx).delta_poc_s0_minus1[i] & 0xFFFFFFFFL) + 1) & 0xFFFFFFFFL) :
                deltaPocS0(stRpsIdx, i - 1) - (((st_ref_pic_sets.get(stRpsIdx).delta_poc_s0_minus1[i] & 0xFFFFFFFFL) + 1) & 0xFFFFFFFFL);
    }

    public final long deltaPocS1(int stRpsIdx, int i)
    {
        return i == 0 ?
                -(((st_ref_pic_sets.get(stRpsIdx).delta_poc_s1_minus1[i] & 0xFFFFFFFFL) + 1) & 0xFFFFFFFFL) :
                deltaPocS1(stRpsIdx, i - 1) - (((st_ref_pic_sets.get(stRpsIdx).delta_poc_s1_minus1[i] & 0xFFFFFFFFL) + 1) & 0xFFFFFFFFL);
    }

    /**
     * <p>
     * 2 if 420 or 422, otherwise 1
     * </p>
     */
    public final byte getSubWidthC()
    {
        return (byte) (((chroma_format_idc & 0xFFFFFFFFL) == 1 || (chroma_format_idc & 0xFFFFFFFFL) == 2) ? 2 : 1);
    }

    /**
     * <p>
     * 2 if 420, otherwise 1
     * </p>
     */
    public final byte getSubHeightC()
    {
        return (byte) (((chroma_format_idc & 0xFFFFFFFFL) == 1) ? 2 : 1);
    }

    public final byte getMinCbLog2SizeY()
    {
        return (byte) ((log2_min_luma_coding_block_size_minus3 & 0xFF) + 3);
    }

    public final byte getCtbLog2SizeY()
    {
        return (byte) ((getMinCbLog2SizeY() & 0xFF) + (log2_diff_max_min_luma_coding_block_size & 0xFF));
    }

    public final /*UInt32*/long getMinCbSizeY()
    {
        return ((1L << (getMinCbLog2SizeY() & 0xFF)) & 0xFFFFFFFFL);
    }

    public final /*UInt32*/long getCtbSizeY()
    {
        return ((1L << (getCtbLog2SizeY() & 0xFF)) & 0xFFFFFFFFL);
    }

    public final /*UInt32*/long getCtbWidthC()
    {
        return ((chroma_format_idc & 0xFFFFFFFFL) == 0 || separate_colour_plane_flag) ? 0 : (((getCtbSizeY() & 0xFFFFFFFFL) / (getSubWidthC() & 0xFF)) & 0xFFFFFFFFL);
    }

    public final /*UInt32*/long getCtbHeightC()
    {
        return ((chroma_format_idc & 0xFFFFFFFFL) == 0 || separate_colour_plane_flag) ? 0 : (((getCtbSizeY() & 0xFFFFFFFFL) / (getSubHeightC() & 0xFF)) & 0xFFFFFFFFL);
    }

    public final /*UInt32*/long getPicWidthInMinCbsY()
    {
        return (((pic_width_in_luma_samples & 0xFFFFFFFFL) / (getMinCbSizeY() & 0xFFFFFFFFL)) & 0xFFFFFFFFL);
    }

    public final /*UInt32*/long getPicWidthInCtbsY()
    {
        return MathExtra.ceilDiv(pic_width_in_luma_samples, getCtbSizeY());
    }

    public final /*UInt32*/long getPicHeightInMinCbsY()
    {
        return (((pic_height_in_luma_samples & 0xFFFFFFFFL) / (getMinCbSizeY() & 0xFFFFFFFFL)) & 0xFFFFFFFFL);
    }

    public final /*UInt32*/long getPicHeightInCtbsY()
    {
        return MathExtra.ceilDiv(pic_height_in_luma_samples, getCtbSizeY());
    }

    public final /*UInt32*/long getPicSizeInMinCbsY()
    {
        return (((getPicWidthInMinCbsY() & 0xFFFFFFFFL) * (getPicHeightInMinCbsY() & 0xFFFFFFFFL)) & 0xFFFFFFFFL);
    }

    public final /*UInt32*/long getPicSizeInCtbsY()
    {
        return (((getPicWidthInCtbsY() & 0xFFFFFFFFL) * (getPicHeightInCtbsY() & 0xFFFFFFFFL)) & 0xFFFFFFFFL);
    }

    public final /*UInt32*/long getPicSizeInSamplesY()
    {
        return (((pic_width_in_luma_samples & 0xFFFFFFFFL) * (pic_height_in_luma_samples & 0xFFFFFFFFL)) & 0xFFFFFFFFL);
    }

    public final /*UInt32*/long getPicWidthInSamplesC()
    {
        return (((pic_width_in_luma_samples & 0xFFFFFFFFL) / (getSubWidthC() & 0xFF)) & 0xFFFFFFFFL);
    }

    public final /*UInt32*/long getPicHeightInSamplesC()
    {
        return (((pic_height_in_luma_samples & 0xFFFFFFFFL) / (getSubHeightC() & 0xFF)) & 0xFFFFFFFFL);
    }

    public final byte getBitDepthY()
    {
        return (byte) (8 + (bit_depth_luma_minus8 & 0xFF));
    }

    public final byte getQpBdOffsetY()
    {
        return (byte) (6 * (bit_depth_luma_minus8 & 0xFF));
    }

    public final byte getBitDepthC()
    {
        return (byte) (8 + (bit_depth_chroma_minus8 & 0xFF));
    }

    public final byte getQpBdOffsetC()
    {
        return (byte) (6 * (bit_depth_chroma_minus8 & 0xFF));
    }

    public final byte getMinTbLog2SizeY()
    {
        return (byte) ((log2_min_luma_transform_block_size_minus2 & 0xFF) + 2);
    }

    public final byte getMaxTbLog2SizeY()
    {
        return (byte) ((getMinTbLog2SizeY() & 0xFF) + (log2_diff_max_min_luma_transform_block_size & 0xFF));
    }

    public final /*UInt32*/long getMinTbSizeY()
    {
        return ((1L << (getMinTbLog2SizeY() & 0xFF)) & 0xFFFFFFFFL);
    }

    public final /*UInt32*/long getMaxPicOrderCntLsb()
    {
        return ((1L << ((log2_max_pic_order_cnt_lsb_minus4 & 0xFF) + 4)) & 0xFFFFFFFFL);
    }

    public final int getLog2MinIpcmCbSizeY()
    {
        return (log2_min_pcm_luma_coding_block_size_minus3 & 0xFF) + 3;
    }

    public final int getLog2MaxIpcmCbSizeY()
    {
        return (log2_diff_max_min_pcm_luma_coding_block_size & 0xFF) + getLog2MinIpcmCbSizeY();
    }

    public final int getCoeffMinY()
    {
        return -(1 << (sps_range_ext.extended_precision_processing_flag ? Math.max(15, (getBitDepthY() & 0xFF) + 6) : 15));
    }

    public final int getCoeffMinC()
    {
        return -(1 << (sps_range_ext.extended_precision_processing_flag ? Math.max(15, (getBitDepthC() & 0xFF) + 6) : 15));
    }

    public final int getCoeffMaxY()
    {
        return (1 << (sps_range_ext.extended_precision_processing_flag ? Math.max(15, (getBitDepthY() & 0xFF) + 6) : 15)) - 1;
    }

    public final int getCoeffMaxC()
    {
        return (1 << (sps_range_ext.extended_precision_processing_flag ? Math.max(15, (getBitDepthC() & 0xFF) + 6) : 15)) - 1;
    }

}

