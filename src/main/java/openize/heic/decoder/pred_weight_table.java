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


class pred_weight_table
{
    //final /*UInt32*/long luma_log2_weight_denom;
    //int delta_chroma_log2_weight_denom;
    // final boolean[] luma_weight_l0_flag;
    // final boolean[] chroma_weight_l0_flag;
    // final int[] delta_luma_weight_l0;
    // final int[] luma_offset_l0;
//    final int[][] delta_chroma_weight_l0;
//    final int[][] delta_chroma_offset_l0;
//    boolean[] luma_weight_l1_flag;
    //boolean[] chroma_weight_l1_flag;
    //   int[] delta_luma_weight_l1;
    //int[] luma_offset_l1;
//    int[][] delta_chroma_weight_l1;
//    int[][] delta_chroma_offset_l1;

    pred_weight_table(BitStreamWithNalSupport stream, slice_segment_header slice_header)
    {
        pic_parameter_set_rbsp pps = slice_header.pps;
        seq_parameter_set_rbsp sps = pps.sps;

        //slice_header.
        /*long luma_log2_weight_denom =*/
        stream.readUev();                         // ue(v)

        if ((sps.getChromaArrayType() & 0xFFFFFFFFL) != 0)
            /*delta_chroma_log2_weight_denom =*/
        {
            stream.readSev();             // se(v)
        }

        //int sumWeightFlags = 0;

        boolean[] luma_weight_l0_flag = new boolean[(int) (((slice_header.num_ref_idx_l0_active_minus1 & 0xFFFFFFFFL) + 1) & 0xFFFFFFFFL)];
        boolean[] chroma_weight_l0_flag = new boolean[(int) (((slice_header.num_ref_idx_l0_active_minus1 & 0xFFFFFFFFL) + 1) & 0xFFFFFFFFL)];

        for (int i = 0; i <= (slice_header.num_ref_idx_l0_active_minus1 & 0xFFFFFFFFL); i++)
            //if ((pic_layer_id(RefPicList0[i]) != nuh_layer_id) ||
            //    (PicOrderCnt(RefPicList0[i]) != PicOrderCnt(CurrPic)))
            luma_weight_l0_flag[i] = stream.readFlag();         // u(1)

        if ((sps.getChromaArrayType() & 0xFFFFFFFFL) != 0)
        {
            for (int i = 0; i <= (slice_header.num_ref_idx_l0_active_minus1 & 0xFFFFFFFFL); i++)
                //if ((pic_layer_id(RefPicList0[i]) != nuh_layer_id) ||
                //    (PicOrderCnt(RefPicList0[i]) != PicOrderCnt(CurrPic)))
                chroma_weight_l0_flag[i] = stream.readFlag();   // u(1)
        }

        //delta_luma_weight_l0 = new int[(int)(((slice_header.num_ref_idx_l0_active_minus1 & 0xFFFFFFFFL) + 1) & 0xFFFFFFFFL)];
        //luma_offset_l0 = new int[(int)(((slice_header.num_ref_idx_l0_active_minus1 & 0xFFFFFFFFL) + 1) & 0xFFFFFFFFL)];
//        delta_chroma_weight_l0 = new int[(int)(((slice_header.num_ref_idx_l0_active_minus1 & 0xFFFFFFFFL) + 1) & 0xFFFFFFFFL)][2];
//        delta_chroma_offset_l0 = new int[(int)(((slice_header.num_ref_idx_l0_active_minus1 & 0xFFFFFFFFL) + 1) & 0xFFFFFFFFL)][2];

        for (int i = 0; i <= (slice_header.num_ref_idx_l0_active_minus1 & 0xFFFFFFFFL); i++)
        {
            if (luma_weight_l0_flag[i])
            {
                /*delta_luma_weight_l0[i] =*/
                stream.readSev();             // se(v)
                /*luma_offset_l0[i] =*/
                stream.readSev();                   // se(v)
            }

            if (chroma_weight_l0_flag[i])
            {
                for (int j = 0; j < 2; j++)
                {
                    /*delta_chroma_weight_l0[i][j] =*/
                    stream.readSev();   // se(v)
                    /*delta_chroma_offset_l0[i][j] =*/
                    stream.readSev();   // se(v)
                }
            }
        }

        if (slice_header.slice_type == SliceType.B)
        {
            boolean[] luma_weight_l1_flag = new boolean[(int) (((slice_header.num_ref_idx_l1_active_minus1 & 0xFFFFFFFFL) + 1) & 0xFFFFFFFFL)];
            boolean[] chroma_weight_l1_flag = new boolean[(int) (((slice_header.num_ref_idx_l1_active_minus1 & 0xFFFFFFFFL) + 1) & 0xFFFFFFFFL)];

            for (int i = 0; i <= (slice_header.num_ref_idx_l1_active_minus1 & 0xFFFFFFFFL); i++)
                //if ((pic_layer_id(RefPicList0[i]) != nuh_layer_id) ||
                //    (PicOrderCnt(RefPicList1[i]) != PicOrderCnt(CurrPic)))
                luma_weight_l1_flag[i] = stream.readFlag();        // u(1)

            if ((sps.getChromaArrayType() & 0xFFFFFFFFL) != 0)
            {
                for (int i = 0; i <= (slice_header.num_ref_idx_l1_active_minus1 & 0xFFFFFFFFL); i++)
                    //if ((pic_layer_id(RefPicList0[i]) != sps.nuh_layer_id) ||
                    //    (PicOrderCnt(RefPicList1[i]) != PicOrderCnt(CurrPic)))
                    chroma_weight_l1_flag[i] = stream.readFlag();  // u(1)
            }

            //delta_luma_weight_l1 = new int[(int)(((slice_header.num_ref_idx_l1_active_minus1 & 0xFFFFFFFFL) + 1) & 0xFFFFFFFFL)];
            //luma_offset_l1 = new int[(int)(((slice_header.num_ref_idx_l1_active_minus1 & 0xFFFFFFFFL) + 1) & 0xFFFFFFFFL)];
            //delta_chroma_weight_l1 = new int[(int)(((slice_header.num_ref_idx_l1_active_minus1 & 0xFFFFFFFFL) + 1) & 0xFFFFFFFFL)][2];
            //delta_chroma_offset_l1 = new int[(int)(((slice_header.num_ref_idx_l1_active_minus1 & 0xFFFFFFFFL) + 1) & 0xFFFFFFFFL)][2];

            for (int i = 0; i <= (slice_header.num_ref_idx_l1_active_minus1 & 0xFFFFFFFFL); i++)
            {
                if (luma_weight_l1_flag[i])
                {
                    /*delta_luma_weight_l1[i] =*/
                    stream.readSev();            // se(v)
                    /*luma_offset_l1[i] =*/
                    stream.readSev();                  // se(v)
                }
                if (chroma_weight_l1_flag[i])
                {
                    for (int j = 0; j < 2; j++)
                    {
                        /*delta_chroma_weight_l1[i][j] =*/
                        stream.readSev();   // se(v)
                        /*delta_chroma_offset_l1[i][j] =*/
                        stream.readSev();   // se(v)
                    }
                }
            }
        }
    }
}
