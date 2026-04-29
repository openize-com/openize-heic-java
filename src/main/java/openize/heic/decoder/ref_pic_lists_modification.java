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


class ref_pic_lists_modification
{
    final boolean ref_pic_list_modification_flag_l0;
    boolean ref_pic_list_modification_flag_l1;
    //final int[] list_entry_l0;
    int[] list_entry_l1;

    ref_pic_lists_modification(
            BitStreamWithNalSupport stream,
            SliceType slice_type,
            /*UInt32*/long num_ref_idx_l0_active_minus1,
            /*UInt32*/long num_ref_idx_l1_active_minus1,
            pic_parameter_set_rbsp pps)
    {
        ref_pic_list_modification_flag_l0 = stream.readFlag();         // u(1)
        // list_entry_l0 = new int[(int) (((num_ref_idx_l0_active_minus1 & 0xFFFFFFFFL) + 1) & 0xFFFFFFFFL)];

        int NumPicTotalCurr = getNumPicTotalCurr(pps);

        if (ref_pic_list_modification_flag_l0)
        {
            for (int i = 0; i <= (num_ref_idx_l1_active_minus1 & 0xFFFFFFFFL); i++)
                /*list_entry_l0[i] =*/ stream.read(MathUtils.f64_s32(                   // u(v)
                        MathUtils.ceiling(MathUtils.log(NumPicTotalCurr, 2))));
        }

        if (slice_type == SliceType.B)
        {
            ref_pic_list_modification_flag_l1 = stream.readFlag();     // u(1)
            list_entry_l1 = new int[(int) (((num_ref_idx_l0_active_minus1 & 0xFFFFFFFFL) + 1) & 0xFFFFFFFFL)];

            if (ref_pic_list_modification_flag_l1)
            {
                for (int i = 0; i <= (num_ref_idx_l1_active_minus1 & 0xFFFFFFFFL); i++)
                    list_entry_l1[i] = stream.read(MathUtils.f64_s32(               // u(v)
                            MathUtils.ceiling(MathUtils.log(NumPicTotalCurr, 2))));
            }
        }
    }

    private int getNumPicTotalCurr(pic_parameter_set_rbsp pps)
    {
        throw new UnsupportedOperationException();
        /*
            int NumPicTotalCurr = 0;
            for (int i = 0; i < NumNegativePics[CurrRpsIdx]; i++)
                if (UsedByCurrPicS0[CurrRpsIdx][i])
                    NumPicTotalCurr++;

            for (int i = 0; i < NumPositivePics[CurrRpsIdx]; i++)
                if (UsedByCurrPicS1[CurrRpsIdx][i])
                    NumPicTotalCurr++;

            for (int i = 0; i < header.num_long_term_sps + header.num_long_term_pics; i++)
                if (UsedByCurrPicLt[i])
                    NumPicTotalCurr++;

            if (pps.pps_scc_ext.pps_curr_pic_ref_enabled_flag)
                NumPicTotalCurr++;
            
            return NumPicTotalCurr;*/
    }
}
