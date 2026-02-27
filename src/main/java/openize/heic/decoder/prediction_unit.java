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


class prediction_unit
{

    public prediction_unit(BitStreamWithNalSupport stream,
                           slice_segment_header header,
                           int x0, int y0, int nPbW, int nPbH)
    {
        throw new UnsupportedOperationException("Prediction units are not met in static image cases.");
        /* (unreachable):

        seq_parameter_set_rbsp sps = header.pps.sps;
        HeicPicture picture = header.parentPicture;*/
        /*
            if (picture.cu_skip_flag[x0, y0])
            {
                if (header.MaxNumMergeCand > 1)
                    picture.merge_idx[x0, y0] = stream.read_aev();
}
            else
            {
                // MODE_INTER 
                picture.merge_flag[x0, y0] = stream.read_aev() == 1;
                if (picture.merge_flag[x0, y0])
                {
                    if (header.MaxNumMergeCand > 1)
                        picture.merge_idx[x0, y0] = stream.read_aev();
                }
                else
                {
                    if (header.slice_type == SliceType.B)
                        picture.inter_pred_idc[x0, y0] = (inter_pred_idc)stream.read_aev();

                    if (picture.inter_pred_idc[x0, y0] != inter_pred_idc.PRED_L1)
                    {
                        if (header.num_ref_idx_l0_active_minus1 > 0)
                            picture.ref_idx_l0[x0, y0] = stream.read_aev();

                        new mvd_coding(stream, header, x0, y0, false);

                        picture.mvp_l0_flag[x0, y0] = stream.read_aev() == 1;
                    }

                    if (picture.inter_pred_idc[x0, y0] != inter_pred_idc.PRED_L0)
                    {
                        if (header.num_ref_idx_l1_active_minus1 > 0)
                            picture.ref_idx_l1[x0, y0] = stream.read_aev();

                        if (picture.mvd_l1_zero_flag && picture.inter_pred_idc[x0, y0] == inter_pred_idc.PRED_BI)
                        {
                            picture.MvdL1[x0, y0][0] = 0;
                            picture.MvdL1[x0, y0][1] = 0;
                        }
                        else
                        {
                            new mvd_coding(stream, header, x0, y0, true);
                        }

                        picture.mvp_l1_flag[x0, y0] = stream.read_aev_flag();
                    }
                }
            }
            */
    }

    private static class mvd_coding
    {
        final boolean[] abs_mvd_greater0_flag;
        final boolean[] abs_mvd_greater1_flag;
        final int[] abs_mvd; // = 1
        final boolean[] mvd_sign_flag;

        public mvd_coding(BitStreamWithNalSupport stream, slice_segment_header header,
                          int x0, int y0, boolean refList)
        {
            HeicPicture picture = header.parentPicture;

            abs_mvd_greater0_flag = new boolean[2];
            abs_mvd_greater1_flag = new boolean[2];
            abs_mvd = new int[2];
            mvd_sign_flag = new boolean[2];

            abs_mvd_greater0_flag[0] = stream.readAevFlag();                       // ae(v)
            abs_mvd_greater0_flag[1] = stream.readAevFlag();                       // ae(v)

            if (abs_mvd_greater0_flag[0])
            {
                abs_mvd_greater1_flag[0] = stream.readAevFlag();                   // ae(v)
            }

            if (abs_mvd_greater0_flag[1])
            {
                abs_mvd_greater1_flag[1] = stream.readAevFlag();                   // ae(v)
            }

            if (abs_mvd_greater0_flag[0])
            {
                if (abs_mvd_greater1_flag[0])
                {
                    abs_mvd[0] = stream.readAev() + 2;                         // ae(v)
                }

                mvd_sign_flag[0] = stream.readAevFlag();                           // ae(v)
            }

            if (abs_mvd_greater0_flag[1])
            {
                if (abs_mvd_greater1_flag[1])
                {
                    abs_mvd[1] = stream.readAev() + 2;                         // ae(v)
                }

                mvd_sign_flag[1] = stream.readAevFlag();                           // ae(v)
            }

            int[] lMvd = new int[2];
            lMvd[0] = (abs_mvd_greater0_flag[0] ? 1 : 0) *
                    (abs_mvd[0]) * (1 - 2 * (mvd_sign_flag[0] ? 1 : 0));
            lMvd[1] = (abs_mvd_greater0_flag[1] ? 1 : 0) *
                    (abs_mvd[1]) * (1 - 2 * (mvd_sign_flag[1] ? 1 : 0));

            /*if (refList)
            {
                //picture.MvdL1[x0, y0][0] = lMvd[0];
                //picture.MvdL1[x0, y0][1] = lMvd[1];
            }
            else
            {
                //picture.MvdL0[x0, y0][0] = lMvd[0];
                //picture.MvdL0[x0, y0][1] = lMvd[1];
            }*/
        }
    }
}

