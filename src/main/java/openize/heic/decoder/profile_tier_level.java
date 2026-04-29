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


class profile_tier_level
{
    byte general_profile_idc;
    final int general_level_idc;

    profile_tier_level(BitStreamWithNalSupport stream, boolean profilePresentFlag, int maxNumSubLayersMinus1)
    {
//        byte general_profile_space;
//        boolean general_tier_flag;
        boolean[] general_profile_compatibility_flag;
//        boolean general_progressive_source_flag;
//        boolean general_interlaced_source_flag;
//        boolean general_non_packed_constraint_flag;
//        boolean general_frame_only_constraint_flag;
//        boolean general_max_12bit_constraint_flag;
//        boolean general_max_10bit_constraint_flag;
//        boolean general_max_8bit_constraint_flag;
//        boolean general_max_422chroma_constraint_flag;
//        boolean general_max_420chroma_constraint_flag;
//        boolean general_max_monochrome_constraint_flag;
//        boolean general_intra_constraint_flag;
//        boolean general_one_picture_only_constraint_flag;
//        boolean general_lower_bit_rate_constraint_flag;
//        boolean general_max_14bit_constraint_flag;
//        boolean general_inbld_flag;
        final boolean[] sub_layer_profile_present_flag;
        final boolean[] sub_layer_level_present_flag;
//        final byte[] sub_layer_profile_space;
//        final boolean[] sub_layer_tier_flag;
        final byte[] sub_layer_profile_idc;
        final boolean[][] sub_layer_profile_compatibility_flag;
//        final boolean[] sub_layer_progressive_source_flag;
//        final boolean[] sub_layer_interlaced_source_flag;
//        final boolean[] sub_layer_non_packed_constraint_flag;
//        final boolean[] sub_layer_frame_only_constraint_flag;
//        final boolean[] sub_layer_max_12bit_constraint_flag;
//        final boolean[] sub_layer_max_10bit_constraint_flag;
//        final boolean[] sub_layer_max_8bit_constraint_flag;
//        final boolean[] sub_layer_max_422chroma_constraint_flag;
//        final boolean[] sub_layer_max_420chroma_constraint_flag;
//        final boolean[] sub_layer_max_monochrome_constraint_flag;
//        final boolean[] sub_layer_intra_constraint_flag;
//        final boolean[] sub_layer_one_picture_only_constraint_flag;
//        final boolean[] sub_layer_lower_bit_rate_constraint_flag;
//        final boolean[] sub_layer_max_14bit_constraint_flag;
//        final boolean[] sub_layer_inbld_flag;
//        final byte[] sub_layer_level_idc;

        if (profilePresentFlag)
        {
            /*general_profile_space = (byte)*/
            stream.read(2);              // u(2)
            /*general_tier_flag =*/
            stream.readFlag();                     // u(1)
            general_profile_idc = (byte) stream.read(5);                // u(5)
            general_profile_compatibility_flag = new boolean[32];
            for (int j = 0; j < 32; j++)
                general_profile_compatibility_flag[j] = stream.readFlag();   // u(1)
            /*general_progressive_source_flag =*/
            stream.readFlag();       // u(1)
            /*general_interlaced_source_flag =*/
            stream.readFlag();        // u(1)
            /*general_non_packed_constraint_flag =*/
            stream.readFlag();    // u(1)
            /*general_frame_only_constraint_flag =*/
            stream.readFlag();    // u(1)

            if ((general_profile_idc & 0xFF) == 4 || general_profile_compatibility_flag[4] || (general_profile_idc & 0xFF) == 5 || general_profile_compatibility_flag[5] || (general_profile_idc & 0xFF) == 6 || general_profile_compatibility_flag[6] || (general_profile_idc & 0xFF) == 7 || general_profile_compatibility_flag[7] || (general_profile_idc & 0xFF) == 8 || general_profile_compatibility_flag[8] || (general_profile_idc & 0xFF) == 9 || general_profile_compatibility_flag[9] || (general_profile_idc & 0xFF) == 10 || general_profile_compatibility_flag[10] || (general_profile_idc & 0xFF) == 11 || general_profile_compatibility_flag[11])
            {
                /* The number of bits in this syntax structure is not affected by this condition */

                /*general_max_12bit_constraint_flag =*/
                stream.readFlag();         // u(1)
                /*general_max_10bit_constraint_flag =*/
                stream.readFlag();         // u(1)
                /*general_max_8bit_constraint_flag =*/
                stream.readFlag();          // u(1)
                /*general_max_422chroma_constraint_flag =*/
                stream.readFlag();     // u(1)
                /*general_max_420chroma_constraint_flag =*/
                stream.readFlag();     // u(1)
                /*general_max_monochrome_constraint_flag =*/
                stream.readFlag();    // u(1)
                /*general_intra_constraint_flag =*/
                stream.readFlag();             // u(1)
                /*general_one_picture_only_constraint_flag =*/
                stream.readFlag();  // u(1)
                /*general_lower_bit_rate_constraint_flag =*/
                stream.readFlag();    // u(1)

                if ((general_profile_idc & 0xFF) == 5 || general_profile_compatibility_flag[5] || (general_profile_idc & 0xFF) == 9 || general_profile_compatibility_flag[9] || (general_profile_idc & 0xFF) == 10 || general_profile_compatibility_flag[10] || (general_profile_idc & 0xFF) == 11 || general_profile_compatibility_flag[11])
                {
                    /*general_max_14bit_constraint_flag =*/
                    stream.readFlag();        // u(1)
                    stream.skipBits(33);            /* general_reserved_zero_33bits */ // u(33)
                }
                else
                {
                    stream.skipBits(34);            /* general_reserved_zero_34bits */ // u(34)
                }
            }
            else if ((general_profile_idc & 0xFF) == 2 || general_profile_compatibility_flag[2])
            {
                stream.skipBits(7);                 /* general_reserved_zero_7bits  */ // u(7)
                /*general_one_picture_only_constraint_flag =*/
                stream.readFlag();     // u(1)
                stream.skipBits(35);                /* general_reserved_zero_35bits */ // u(35)
            }
            else
            {
                stream.skipBits(43);                /* general_reserved_zero_43bits */ // u(43)
            }

            if ((general_profile_idc & 0xFF) == 1 || general_profile_compatibility_flag[1] || (general_profile_idc & 0xFF) == 2 || general_profile_compatibility_flag[2] || (general_profile_idc & 0xFF) == 3 || general_profile_compatibility_flag[3] || (general_profile_idc & 0xFF) == 4 || general_profile_compatibility_flag[4] || (general_profile_idc & 0xFF) == 5 || general_profile_compatibility_flag[5] || (general_profile_idc & 0xFF) == 9 || general_profile_compatibility_flag[9] || (general_profile_idc & 0xFF) == 11 || general_profile_compatibility_flag[11])
            {
                /* The number of bits in this syntax structure is not affected by this condition */
                /*general_inbld_flag =*/
                stream.readFlag();                // u(1)
            }
            else
            {
                stream.skipBits(1);         /* general_reserved_zero_bit */ // u(1)
            }
        }

        general_level_idc = stream.read(8);                            // u(8)
        sub_layer_profile_present_flag = new boolean[maxNumSubLayersMinus1];
        sub_layer_level_present_flag = new boolean[maxNumSubLayersMinus1];
        for (int i = 0; i < maxNumSubLayersMinus1; i++)
        {
            sub_layer_profile_present_flag[i] = stream.readFlag();     // u(1)
            sub_layer_level_present_flag[i] = stream.readFlag();       // u(1)
        }
        if (maxNumSubLayersMinus1 > 0)
        {
            for (int i = maxNumSubLayersMinus1; i < 8; i++)
                stream.skipBits(2);               /* reserved_zero_2bits */ // u(2)
        }

//        sub_layer_profile_space = new byte[maxNumSubLayersMinus1];
//        sub_layer_tier_flag = new boolean[maxNumSubLayersMinus1];
        sub_layer_profile_idc = new byte[maxNumSubLayersMinus1];
        sub_layer_profile_compatibility_flag = new boolean[maxNumSubLayersMinus1][32];
//        sub_layer_progressive_source_flag = new boolean[maxNumSubLayersMinus1];
//        sub_layer_interlaced_source_flag = new boolean[maxNumSubLayersMinus1];
//        sub_layer_non_packed_constraint_flag = new boolean[maxNumSubLayersMinus1];
//        sub_layer_frame_only_constraint_flag = new boolean[maxNumSubLayersMinus1];
//        sub_layer_max_12bit_constraint_flag = new boolean[maxNumSubLayersMinus1];
//        sub_layer_max_10bit_constraint_flag = new boolean[maxNumSubLayersMinus1];
//        sub_layer_max_8bit_constraint_flag = new boolean[maxNumSubLayersMinus1];
//        sub_layer_max_422chroma_constraint_flag = new boolean[maxNumSubLayersMinus1];
//        sub_layer_max_420chroma_constraint_flag = new boolean[maxNumSubLayersMinus1];
//        sub_layer_max_monochrome_constraint_flag = new boolean[maxNumSubLayersMinus1];
//        sub_layer_intra_constraint_flag = new boolean[maxNumSubLayersMinus1];
//        sub_layer_one_picture_only_constraint_flag = new boolean[maxNumSubLayersMinus1];
//        sub_layer_lower_bit_rate_constraint_flag = new boolean[maxNumSubLayersMinus1];
//        sub_layer_max_14bit_constraint_flag = new boolean[maxNumSubLayersMinus1];
//        sub_layer_inbld_flag = new boolean[maxNumSubLayersMinus1];
//        sub_layer_level_idc = new byte[maxNumSubLayersMinus1];

        for (int i = 0; i < maxNumSubLayersMinus1; i++)
        {
            if (sub_layer_profile_present_flag[i])
            {
                /*sub_layer_profile_space[i] = (byte)*/
                stream.read(2);     // u(2)
                /*sub_layer_tier_flag[i] =*/
                stream.readFlag();            // u(1)
                sub_layer_profile_idc[i] = (byte) stream.read(5);       // u(5)

                for (int j = 0; j < 32; j++)
                    sub_layer_profile_compatibility_flag[i][j] = stream.readFlag();    // u(1)

                /*sub_layer_progressive_source_flag[i] =*/
                stream.readFlag();              // u(1)
                /*sub_layer_interlaced_source_flag[i] =*/
                stream.readFlag();               // u(1)
                /*sub_layer_non_packed_constraint_flag[i] =*/
                stream.readFlag();           // u(1)
                /*sub_layer_frame_only_constraint_flag[i] =*/
                stream.readFlag();           // u(1)
                if ((sub_layer_profile_idc[i] & 0xFF) == 4 || sub_layer_profile_compatibility_flag[i][4] || (sub_layer_profile_idc[i] & 0xFF) == 5 || sub_layer_profile_compatibility_flag[i][5] || (sub_layer_profile_idc[i] & 0xFF) == 6 || sub_layer_profile_compatibility_flag[i][6] || (sub_layer_profile_idc[i] & 0xFF) == 7 || sub_layer_profile_compatibility_flag[i][7] || (sub_layer_profile_idc[i] & 0xFF) == 8 || sub_layer_profile_compatibility_flag[i][8] || (sub_layer_profile_idc[i] & 0xFF) == 9 || sub_layer_profile_compatibility_flag[i][9] || (sub_layer_profile_idc[i] & 0xFF) == 10 || sub_layer_profile_compatibility_flag[i][10] || (sub_layer_profile_idc[i] & 0xFF) == 11 || sub_layer_profile_compatibility_flag[i][11])
                {
                    /* The number of bits in this syntax structure is not affected by this condition */

                    /*sub_layer_max_12bit_constraint_flag[i] =*/
                    stream.readFlag();            // u(1)
                    /*sub_layer_max_10bit_constraint_flag[i] =*/
                    stream.readFlag();            // u(1)
                    /*sub_layer_max_8bit_constraint_flag[i] =*/
                    stream.readFlag();             // u(1)
                    /*sub_layer_max_422chroma_constraint_flag[i] =*/
                    stream.readFlag();        // u(1)
                    /*sub_layer_max_420chroma_constraint_flag[i] =*/
                    stream.readFlag();        // u(1)
                    /*sub_layer_max_monochrome_constraint_flag[i] =*/
                    stream.readFlag();       // u(1)
                    /*sub_layer_intra_constraint_flag[i] =*/
                    stream.readFlag();                // u(1)
                    /*sub_layer_one_picture_only_constraint_flag[i] =*/
                    stream.readFlag();     // u(1)
                    /*sub_layer_lower_bit_rate_constraint_flag[i] =*/
                    stream.readFlag();       // u(1)
                    if ((sub_layer_profile_idc[i] & 0xFF) == 5 || sub_layer_profile_compatibility_flag[i][5] || (sub_layer_profile_idc[i] & 0xFF) == 9 || sub_layer_profile_compatibility_flag[i][9] || (sub_layer_profile_idc[i] & 0xFF) == 10 || sub_layer_profile_compatibility_flag[i][10] || (sub_layer_profile_idc[i] & 0xFF) == 11 || sub_layer_profile_compatibility_flag[i][11])
                    {
                        /*sub_layer_max_14bit_constraint_flag[i] =*/
                        stream.readFlag();        // u(1)
                        stream.skipBits(33);               /* sub_layer_reserved_zero_33bits */ // u(33)
                    }
                    else
                    {
                        stream.skipBits(34);               /* sub_layer_reserved_zero_34bits */ // u(34)
                    }
                }
                else if ((sub_layer_profile_idc[i] & 0xFF) == 2 || sub_layer_profile_compatibility_flag[i][2])
                {
                    stream.skipBits(7);                    /* sub_layer_reserved_zero_7bits */  // u(7)
                    /*sub_layer_one_picture_only_constraint_flag[i] =*/
                    stream.readFlag();     // u(1)
                    stream.skipBits(35);                   /* sub_layer_reserved_zero_35bits */ // u(35)
                }
                else
                {
                    stream.skipBits(43);                   /* sub_layer_reserved_zero_43bits */ // u(43)
                }

                if ((sub_layer_profile_idc[i] & 0xFF) == 1 || sub_layer_profile_compatibility_flag[i][1] || (sub_layer_profile_idc[i] & 0xFF) == 2 || sub_layer_profile_compatibility_flag[i][2] || (sub_layer_profile_idc[i] & 0xFF) == 3 || sub_layer_profile_compatibility_flag[i][3] || (sub_layer_profile_idc[i] & 0xFF) == 4 || sub_layer_profile_compatibility_flag[i][4] || (sub_layer_profile_idc[i] & 0xFF) == 5 || sub_layer_profile_compatibility_flag[i][5] || (sub_layer_profile_idc[i] & 0xFF) == 9 || sub_layer_profile_compatibility_flag[i][9] || (sub_layer_profile_idc[i] & 0xFF) == 11 || sub_layer_profile_compatibility_flag[i][11])
                {
                    /* The number of bits in this syntax structure is not affected by this condition */
                    /*sub_layer_inbld_flag[i] =*/
                    stream.readFlag();                           // u(1)
                }
                else
                {
                    stream.skipBits(1);                       /* sub_layer_reserved_zero_bit */ // u(1)
                }
            }
            if (sub_layer_level_present_flag[i])
            {
                /*sub_layer_level_idc[i] = (byte)*/
                stream.read(8);                             // u(8)
            }
        }
    }
}
