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


class st_ref_pic_set
{
    final /*UInt32*/ long stRpsIdx;

    boolean inter_ref_pic_set_prediction_flag;
    /*UInt32*/ long delta_idx_minus1;
    byte delta_rps_sign;
    /*UInt32*/ long abs_delta_rps_minus1;
    //boolean[] used_by_curr_pic_flag;
    //boolean[] use_delta_flag;
    /*UInt32*/ long num_negative_pics;
    /*UInt32*/ long num_positive_pics;
    /*UInt32*/ long[] delta_poc_s0_minus1;
    boolean[] used_by_curr_pic_s0_flag;
    /*UInt32*/ long[] delta_poc_s1_minus1;
    boolean[] used_by_curr_pic_s1_flag;

    st_ref_pic_set(BitStreamWithNalSupport stream, seq_parameter_set_rbsp sps, /*UInt32*/long stRpsIdx)
    {
        this.stRpsIdx = stRpsIdx;

        if ((stRpsIdx & 0xFFFFFFFFL) != 0)
        {
            inter_ref_pic_set_prediction_flag = stream.readFlag();     // u(1)
        }

        if (inter_ref_pic_set_prediction_flag)
        {
            if (stRpsIdx == sps.num_short_term_ref_pic_sets)
            {
                delta_idx_minus1 = stream.readUev();                          // ue(v)
            }

            delta_rps_sign = (byte) stream.read(1);                     // u(1)
            abs_delta_rps_minus1 = stream.readUev();                          // ue(v)
            /*UInt32*/
            long RefRpsIdx = ((stRpsIdx & 0xFFFFFFFFL) - (((delta_idx_minus1 & 0xFFFFFFFFL) + 1) & 0xFFFFFFFFL)) & 0xFFFFFFFFL;

//            used_by_curr_pic_flag = new boolean[(int)(((sps.numDeltaPocs((int)RefRpsIdx) & 0xFFFFFFFFL) + 1) & 0xFFFFFFFFL)];
//            use_delta_flag = new boolean[(int)(((sps.numDeltaPocs((int)RefRpsIdx) & 0xFFFFFFFFL) + 1) & 0xFFFFFFFFL)];

            int count = (int) sps.numDeltaPocs((int) RefRpsIdx);
            for (int j = 0; j <= count; j++)
            {
                /*used_by_curr_pic_flag[j] =*/
                boolean is_flag = stream.readFlag();          // u(1)
                //if (!used_by_curr_pic_flag[j])
                if (!is_flag)
                    /*use_delta_flag[j] =*/
                {
                    stream.readFlag();             // u(1)
                }
            }
        }
        else
        {
            num_negative_pics = stream.readUev();                          // ue(v)
            num_positive_pics = stream.readUev();                          // ue(v)
            delta_poc_s0_minus1 = new /*UInt32*/long[(int) (num_negative_pics & 0xFFFFFFFFL)];
            used_by_curr_pic_s0_flag = new boolean[(int) (num_negative_pics & 0xFFFFFFFFL)];
            delta_poc_s1_minus1 = new /*UInt32*/long[(int) (num_positive_pics & 0xFFFFFFFFL)];
            used_by_curr_pic_s1_flag = new boolean[(int) (num_positive_pics & 0xFFFFFFFFL)];

            for (int i = 0; i < (num_negative_pics & 0xFFFFFFFFL); i++)
            {
                delta_poc_s0_minus1[i] = stream.readUev();                    // ue(v)
                used_by_curr_pic_s0_flag[i] = stream.readFlag();       // u(1)
            }

            for (int i = 0; i < (num_positive_pics & 0xFFFFFFFFL); i++)
            {
                delta_poc_s1_minus1[i] = stream.readUev();                    // ue(v)
                used_by_curr_pic_s1_flag[i] = stream.readFlag();       // u(1)
            }
        }
    }

    public final /*UInt32*/long getNumNegativePics()
    {
        return num_negative_pics;
    }

    public final /*UInt32*/long getNumPositivePics()
    {
        return num_positive_pics;
    }

    public final /*UInt32*/long getNumDeltaPocs()
    {
        return (((getNumNegativePics() & 0xFFFFFFFFL) + (getNumPositivePics() & 0xFFFFFFFFL)) & 0xFFFFFFFFL);
    }

    public final boolean[] getUsedByCurrPicS0()
    {
        return used_by_curr_pic_s0_flag;
    }

    public final boolean[] getUsedByCurrPicS1()
    {
        return used_by_curr_pic_s1_flag;
    }

    public final long deltaPocS0(int i)
    {
        return i == 0 ? -(((delta_poc_s0_minus1[i] & 0xFFFFFFFFL) + 1) & 0xFFFFFFFFL) : deltaPocS0(i - 1) - (((delta_poc_s0_minus1[i] & 0xFFFFFFFFL) + 1) & 0xFFFFFFFFL);
    }

    public final long deltaPocS1(int i)
    {
        return i == 0 ? -(((delta_poc_s1_minus1[i] & 0xFFFFFFFFL) + 1) & 0xFFFFFFFFL) : deltaPocS1(i - 1) - (((delta_poc_s1_minus1[i] & 0xFFFFFFFFL) + 1) & 0xFFFFFFFFL);
    }
}


