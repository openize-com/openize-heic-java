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

import openize.MathUtils;
import openize.heic.decoder.io.BitStreamWithNalSupport;


public class slice_segment_header
{
    public final pic_parameter_set_rbsp pps;
    public int slice_index;
    public HeicPicture parentPicture;
    final boolean first_slice_segment_in_pic_flag;
    boolean no_output_of_prior_pics_flag;
    final byte slice_pic_parameter_set_id;
    boolean dependent_slice_segment_flag;
    /*UInt32*/ int slice_segment_address;
    //final boolean[] slice_reserved_flag;
    SliceType slice_type;
    boolean pic_output_flag;
    byte colour_plane_id;
    int slice_pic_order_cnt_lsb;
    boolean short_term_ref_pic_set_sps_flag;
    int short_term_ref_pic_set_idx;
    /*UInt32*/ int num_long_term_sps;
    /*UInt32*/ int num_long_term_pics;
    boolean[] used_by_curr_pic_lt_flag;
    /*UInt32*/ int[] lt_idx_sps;
    int[] poc_lsb_lt;
    boolean[] delta_poc_msb_present_flag;
    // /*UInt32*/ long[] delta_poc_msb_cycle_lt;
    boolean slice_temporal_mvp_enabled_flag;
    boolean slice_sao_luma_flag;
    boolean slice_sao_chroma_flag;
    boolean num_ref_idx_active_override_flag;
    /*UInt32*/ long num_ref_idx_l0_active_minus1;
    /*UInt32*/ long num_ref_idx_l1_active_minus1;
    boolean collocated_from_l0_flag;
    /*UInt32*/ long collocated_ref_idx;
    boolean cabac_init_flag;
    boolean mvd_l1_zero_flag;
    byte five_minus_max_num_merge_cand;
    boolean use_integer_mv_flag;
    int slice_qp_delta;
    int slice_cb_qp_offset;
    int slice_cr_qp_offset;
    int slice_act_y_qp_offset;
    int slice_act_cb_qp_offset;
    int slice_act_cr_qp_offset;
    boolean cu_chroma_qp_offset_enabled_flag;
    boolean deblocking_filter_override_flag;
    boolean slice_deblocking_filter_disabled_flag;
    int slice_beta_offset_div2;
    int slice_tc_offset_div2;
    boolean slice_loop_filter_across_slices_enabled_flag;
    /*UInt32*/ long num_entry_point_offsets;
    int offset_len_minus1;
    //int[] entry_point_offset_minus1;
    /*UInt32*/ int slice_segment_header_extension_length;
    //byte[] slice_segment_header_extension_data_byte;
    //ref_pic_lists_modification ref_pic_lists_modification;
    //public int CurrRpsIdx;
    //pred_weight_table pred_weight_table;
    private /*UInt32*/ long ctbAddrInRs;
    private /*UInt32*/ long ctbAddrInTs;

    public slice_segment_header(
            BitStreamWithNalSupport stream,
            slice_segment_layer_rbsp parent)
    {
        first_slice_segment_in_pic_flag = stream.readFlag();

        if (parent.NalHeader.isRapPicture())
        {
            no_output_of_prior_pics_flag = stream.readFlag();
        }

        slice_pic_parameter_set_id = (byte) stream.readUev();

        pps = stream.getContext().getPPS().get(slice_pic_parameter_set_id & 0xFF);
        seq_parameter_set_rbsp sps = pps.sps;

        if (!first_slice_segment_in_pic_flag)
        {
            if (pps.dependent_slice_segments_enabled_flag)
            {
                dependent_slice_segment_flag = stream.readFlag();
            }

            slice_segment_address = (int) (stream.read(
                                MathUtils.f64_s32(MathUtils.ceiling(MathUtils.log(sps.getPicSizeInCtbsY() & 0xFFFFFFFFL, 2)))) & 0xFFFFFFFFL);

            setCtbAddrInRs(slice_segment_address);
            setCtbAddrInTs(pps.CtbAddrRsToTs[(int) (getCtbAddrInRs())]);
        }

        //int CuQpDeltaVal = 0;
        //slice_reserved_flag = new boolean[pps.num_extra_slice_header_bits];
        if (!dependent_slice_segment_flag)
        {
            for (int i = 0; i < (pps.num_extra_slice_header_bits & 0xFF); i++)
                /*slice_reserved_flag[i] =*/ stream.readFlag();

            slice_type = SliceType.get(stream.readUev());

            if (slice_type != SliceType.I)
            {
                throw new IllegalStateException("ALARM! NOT I FRAME");
            }

            if (pps.output_flag_present_flag)
            {
                pic_output_flag = stream.readFlag();
            }
            //else = true?


            if (sps.separate_colour_plane_flag)
            {
                colour_plane_id = (byte) stream.read(2);
            }

            if (parent.NalHeader.type != NalUnitType.IDR_W_RADL &&
                    parent.NalHeader.type != NalUnitType.IDR_N_LP)
            {

                slice_pic_order_cnt_lsb
                        = stream.read((sps.log2_max_pic_order_cnt_lsb_minus4 & 0xFF) + 4);
                short_term_ref_pic_set_sps_flag = stream.readFlag();

                if (!short_term_ref_pic_set_sps_flag)
                {

                    sps.st_ref_pic_sets.put(sps.st_ref_pic_sets.size(), new st_ref_pic_set(stream, sps, sps.num_short_term_ref_pic_sets));
                }
                else if ((sps.num_short_term_ref_pic_sets & 0xFFFFFFFFL) > 1)
                {
                    short_term_ref_pic_set_idx = stream.read(MathUtils.f64_s32(
                            MathUtils.ceiling(MathUtils.log(sps.num_short_term_ref_pic_sets & 0xFFFFFFFFL, 2))));
                }

                if (sps.long_term_ref_pics_present_flag)
                {
                    if ((sps.num_long_term_ref_pics_sps & 0xFFFFFFFFL) > 0)
                    {
                        num_long_term_sps = (int) stream.readUev();
                    }
                    num_long_term_pics = (int) stream.readUev();

                    /*UInt32*/
                    int num_long_term_sum = num_long_term_sps + num_long_term_pics;
                    used_by_curr_pic_lt_flag = new boolean[(int) (num_long_term_sum & 0xFFFFFFFFL)];
                    lt_idx_sps = new /*UInt32*/int[(int) (num_long_term_sum & 0xFFFFFFFFL)];
                    poc_lsb_lt = new int[(int) (num_long_term_sum & 0xFFFFFFFFL)];
                    delta_poc_msb_present_flag = new boolean[(int) (num_long_term_sum & 0xFFFFFFFFL)];
                    // delta_poc_msb_cycle_lt = new /*UInt32*/long[(int) (num_long_term_sum & 0xFFFFFFFFL)];

                    for (int i = 0; i < ((num_long_term_sps + num_long_term_pics) & 0xFFFFFFFFL); i++)
                    {
                        if (i < (num_long_term_sps & 0xFFFFFFFFL))
                        {
                            if ((sps.num_long_term_ref_pics_sps & 0xFFFFFFFFL) > 1)
                            {
                                lt_idx_sps[i] = (int)stream.readUev();
                            }
                        }
                        else
                        {
                            poc_lsb_lt[i]
                                    = stream.read((sps.log2_max_pic_order_cnt_lsb_minus4 & 0xFF) + 4);
                            used_by_curr_pic_lt_flag[i]
                                    = stream.readFlag();
                        }

                        delta_poc_msb_present_flag[i]
                                = stream.readFlag();

                        if (delta_poc_msb_present_flag[i])
                        {
                            /*delta_poc_msb_cycle_lt[i] =*/ stream.readUev();
                        }
                    }
                }
                if (sps.sps_temporal_mvp_enabled_flag)
                {
                    slice_temporal_mvp_enabled_flag
                            = stream.readFlag();
                }
            }

            if (sps.sample_adaptive_offset_enabled_flag)
            {
                slice_sao_luma_flag = stream.readFlag();
                if ((sps.getChromaArrayType() & 0xFFFFFFFFL) != 0)
                {
                    slice_sao_chroma_flag = stream.readFlag();
                }
            }

            if (slice_type == SliceType.P || slice_type == SliceType.B)
            {
                num_ref_idx_active_override_flag = stream.readFlag();
                if (num_ref_idx_active_override_flag)
                {
                    num_ref_idx_l0_active_minus1 = stream.readUev();
                    if (slice_type == SliceType.B)
                    {
                        num_ref_idx_l1_active_minus1 = stream.readUev();
                    }
                }

                if (pps.lists_modification_present_flag && (getNumPicTotalCurr() & 0xFFFFFFFFL) > 1)
                {
                    /*ref_pic_lists_modification =*/ new ref_pic_lists_modification(
                            stream, slice_type,
                            num_ref_idx_l0_active_minus1,
                            num_ref_idx_l1_active_minus1,
                            pps);
                }

                if (slice_type == SliceType.B)
                {
                    mvd_l1_zero_flag = stream.readFlag();
                }
                if (pps.cabac_init_present_flag)
                {
                    cabac_init_flag = stream.readFlag();
                }

                if (slice_temporal_mvp_enabled_flag)
                {
                    if (slice_type == SliceType.B)
                    {
                        collocated_from_l0_flag = stream.readFlag();
                    }
                    if ((collocated_from_l0_flag && (num_ref_idx_l1_active_minus1 & 0xFFFFFFFFL) > 0) ||
                            (!collocated_from_l0_flag && (num_ref_idx_l1_active_minus1 & 0xFFFFFFFFL) > 0))
                    {
                        collocated_ref_idx = stream.readUev();
                    }
                }

                if ((pps.weighted_pred_flag && slice_type == SliceType.P) ||
                        (pps.weighted_bipred_flag && slice_type == SliceType.B))
                {
                    /*pred_weight_table =*/ new pred_weight_table(
                            stream, this);
                }

                five_minus_max_num_merge_cand = (byte) stream.readUev();
                if ((sps.sps_scc_ext != null ? sps.sps_scc_ext.motion_vector_resolution_control_idc : 0) == 2)
                {
                    use_integer_mv_flag = stream.readFlag();
                }
            }

            slice_qp_delta = stream.readSev();
            if (pps.pps_slice_chroma_qp_offsets_present_flag)
            {
                slice_cb_qp_offset = stream.readSev();
                slice_cr_qp_offset = stream.readSev();
            }
            boolean tmp0 = (pps.pps_scc_ext != null && pps.pps_scc_ext.pps_slice_act_qp_offsets_present_flag);

            if (tmp0)
            {
                slice_act_y_qp_offset = stream.readSev();
                slice_act_cb_qp_offset = stream.readSev();
                slice_act_cr_qp_offset = stream.readSev();
            }
            boolean tmp1 = (pps.pps_range_ext != null && pps.pps_range_ext.chroma_qp_offset_list_enabled_flag);

            if (tmp1)
            {
                cu_chroma_qp_offset_enabled_flag = stream.readFlag();
            }

            if (pps.deblocking_filter_override_enabled_flag)
            {
                deblocking_filter_override_flag = stream.readFlag();
            }

            if (deblocking_filter_override_flag)
            {
                slice_deblocking_filter_disabled_flag = stream.readFlag();
                if (!slice_deblocking_filter_disabled_flag)
                {
                    slice_beta_offset_div2 = stream.readSev();
                    slice_tc_offset_div2 = stream.readSev();
                }
            }

            if (pps.pps_loop_filter_across_slices_enabled_flag &&
                    (slice_sao_luma_flag || slice_sao_chroma_flag ||
                            !slice_deblocking_filter_disabled_flag))
            {
                slice_loop_filter_across_slices_enabled_flag = stream.readFlag();
            }
        }

        if (pps.tiles_enabled_flag || pps.entropy_coding_sync_enabled_flag)
        {
            num_entry_point_offsets = stream.readUev();
            if ((num_entry_point_offsets & 0xFFFFFFFFL) > 0)
            {
                offset_len_minus1 = (byte) stream.readUev() & 0xFF;
                //entry_point_offset_minus1 = new int[(int) (num_entry_point_offsets & 0xFFFFFFFFL)];
                for (int i = 0; i < (num_entry_point_offsets & 0xFFFFFFFFL); i++)
                    /*entry_point_offset_minus1[i] =*/ stream.read(offset_len_minus1 + 1);
                //entry_point_offset_minus1[i+1] += entry_point_offset_minus1[i]
            }
        }
        if (pps.slice_segment_header_extension_present_flag)
        {
            slice_segment_header_extension_length = (int) stream.readUev();
//            slice_segment_header_extension_data_byte =
//                    new byte[slice_segment_header_extension_length];
            for (int i = 0; i < slice_segment_header_extension_length; i++)
                /*slice_segment_header_extension_data_byte[i] = (byte)*/ stream.read(8);
        }

        stream.read(1);             /* equal to 1; alignment_bit_equal_to_one */
        while (stream.notByteAligned())
            stream.skipBits(1);     /* equal to 0; alignment_bit_equal_to_zero */
    }

    public final int getMaxNumMergeCand()
    {
        return 5 - (five_minus_max_num_merge_cand & 0xFF);
    }

    public final int getInitType()
    {
        switch (slice_type)
        {
            case P:
                return (cabac_init_flag ? 1 : 0) + 1;
            case B:
                return 2 - (cabac_init_flag ? 1 : 0);
            case I:
            default:
                return 0;
        }
    }

    public final int getSliceQPY()
    {
        return 26 + pps.init_qp_minus26 + slice_qp_delta;
    }

    final /*UInt32*/long getCtbAddrInRs()
    {
        return ctbAddrInRs;
    }

    final void setCtbAddrInRs(/*UInt32*/long value)
    {
        ctbAddrInRs = value;
    }

    final /*UInt32*/long getCtbAddrInTs()
    {
        return ctbAddrInTs;
    }

    final void setCtbAddrInTs(/*UInt32*/long value)
    {
        ctbAddrInTs = value;
    }

    final /*UInt32*/long getSliceAddrRs()
    {
        return !dependent_slice_segment_flag ?
                slice_segment_address : // if not dependent slice
                pps.CtbAddrTsToRs[(int) ((pps.CtbAddrRsToTs[slice_segment_address] - 1) & 0xFFFFFFFFL)];
    }// else take preceding slice segment

    final /*UInt32*/long getCurrRpsIdx()
    {
        return short_term_ref_pic_set_sps_flag ?
                (short_term_ref_pic_set_idx & 0xFFFFFFFFL) :
                pps.sps.num_short_term_ref_pic_sets;
    }

    public final /*UInt32*/long getNumPicTotalCurr()
    {
        /*UInt32*/
        long NumPicTotalCurr = 0;

        for (int i = 0; i < (pps.sps.numNegativePics((int) getCurrRpsIdx()) & 0xFFFFFFFFL); i++)
            if (pps.sps.usedByCurrPicS0((int) getCurrRpsIdx())[i])
            {
                NumPicTotalCurr++;
            }

        for (int i = 0; i < (pps.sps.numPositivePics((int) getCurrRpsIdx()) & 0xFFFFFFFFL); i++)
            if (pps.sps.usedByCurrPicS1((int) getCurrRpsIdx())[i])
            {
                NumPicTotalCurr++;
            }

        for (int i = 0; i < (((num_long_term_sps & 0xFFFFFFFFL) + (num_long_term_pics & 0xFFFFFFFFL)) & 0xFFFFFFFFL); i++)
            if (usedByCurrPicLt(i))
            {
                NumPicTotalCurr++;
            }

        if (pps.pps_scc_ext.pps_curr_pic_ref_enabled_flag)
        {
            NumPicTotalCurr++;
        }

        return NumPicTotalCurr;
    }

    public final int pocLsbLt(int i)
    {
        return (i < (num_long_term_sps & 0xFFFFFFFFL)) ?
                pps.sps.lt_ref_pic_poc_lsb_sps[lt_idx_sps[i]] :
                poc_lsb_lt[i];
    }


    public final boolean usedByCurrPicLt(int i)
    {
        return (i < (num_long_term_sps & 0xFFFFFFFFL)) ?
                pps.sps.used_by_curr_pic_lt_sps_flag[lt_idx_sps[i]] :
                used_by_curr_pic_lt_flag[i];
    }
}
