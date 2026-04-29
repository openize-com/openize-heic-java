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



class sao
{
    boolean sao_merge_left_flag;
    boolean sao_merge_up_flag;

    boolean leftCtbInSliceSeg;
    boolean leftCtbInTile;

    boolean upCtbInSliceSeg;
    boolean upCtbInTile;

    byte sao_type_idx_luma;
    byte sao_type_idx_chroma;
    byte sao_eo_class_luma;
    byte sao_eo_class_chroma;

    public sao(BitStreamWithNalSupport stream, int rx, int ry,
        slice_segment_header header)
    {
        seq_parameter_set_rbsp sps = header.pps.sps;
        HeicPicture picture = header.parentPicture;

        if (rx > 0)
        {
            leftCtbInSliceSeg = (header.getCtbAddrInRs() & 0xFFFFFFFFL) > (header.getSliceAddrRs() & 0xFFFFFFFFL);
            leftCtbInTile = 
                header.pps.TileId[(int)(header.getCtbAddrInTs())] == 
                header.pps.TileId[(int)(header.pps.CtbAddrRsToTs[(int)(((header.getCtbAddrInRs() & 0xFFFFFFFFL) - 1) & 0xFFFFFFFFL)])];

            if (leftCtbInSliceSeg && leftCtbInTile)
                sao_merge_left_flag = stream.getCabac().read_sao_merge_flag();
        }

        if (ry > 0 && !sao_merge_left_flag)
        {
            upCtbInSliceSeg = (((header.getCtbAddrInRs() & 0xFFFFFFFFL) - (sps.getPicWidthInCtbsY() & 0xFFFFFFFFL) ) & 0xFFFFFFFFL) >= (header.getSliceAddrRs() & 0xFFFFFFFFL);
            upCtbInTile = 
                header.pps.TileId[(int)(header.getCtbAddrInTs())] == 
                header.pps.TileId[(int)(header.pps.CtbAddrRsToTs[(int)(((header.getCtbAddrInRs() & 0xFFFFFFFFL) - (sps.getPicWidthInCtbsY() & 0xFFFFFFFFL)) & 0xFFFFFFFFL)])];

            if (upCtbInSliceSeg && upCtbInTile)
                sao_merge_up_flag = stream.getCabac().read_sao_merge_flag();
        }

        int chroma_count = ((header.pps.sps.getChromaArrayType() & 0xFFFFFFFFL) != 0 ? 3 : 1);

        for (int cIdx = 0; cIdx < chroma_count; cIdx++)
        {
            if (picture.sao_offset_abs[cIdx][rx][ry] == null)
                picture.sao_offset_abs[cIdx][rx][ry] = new int[4];

            if (picture.sao_offset_sign[cIdx][rx][ry] == null)
                picture.sao_offset_sign[cIdx][rx][ry] = new int[4];

            if (picture.SaoOffsetVal[cIdx][rx][ry] == null)
                picture.SaoOffsetVal[cIdx][rx][ry] = new int[5];

            if (!sao_merge_up_flag && !sao_merge_left_flag)
            {

                if ((header.slice_sao_luma_flag && cIdx == 0) ||
                    (header.slice_sao_chroma_flag && cIdx > 0))
                {
                    if (cIdx == 0)
                    {
                        sao_type_idx_luma = (byte)stream.getCabac().read_sao_type_idx();
                        picture.SaoTypeIdx[cIdx][rx][ry] = sao_type_idx_luma & 0xFF;
                    }
                    else if (cIdx == 1)
                    {
                        sao_type_idx_chroma = (byte)stream.getCabac().read_sao_type_idx();
                        picture.SaoTypeIdx[cIdx][rx][ry] = sao_type_idx_chroma & 0xFF;
                        picture.SaoTypeIdx[cIdx + 1][rx][ry] = sao_type_idx_chroma & 0xFF;
                    }

                    if (picture.SaoTypeIdx[cIdx][rx][ry] != 0)
                    {
                        for (int i = 0; i < 4; i++)
                        {
                            picture.sao_offset_abs[cIdx][rx][ry][i] =
                                stream.getCabac().read_sao_offset_abs((cIdx == 0 ? sps.getBitDepthY() : sps.getBitDepthC()) & 0xFF); // ae(v)
                        }

                        if (picture.SaoTypeIdx[cIdx][rx][ry] == 1) // Band offset
                        {
                            for (int i = 0; i < 4; i++)
                            {
                                if (picture.sao_offset_abs[cIdx][rx][ry][i] != 0)
                                    picture.sao_offset_sign[cIdx][rx][ry][i] = stream.getCabac().read_sao_offset_sign(); // ae(v)
                                else
                                    picture.sao_offset_sign[cIdx][rx][ry][i] = 0;
                            }

                            picture.sao_band_position[cIdx][rx][ry] = stream.getCabac().read_sao_band_position(); // ae(v)
                        }
                        else // SaoTypeIdx == 2; Edge offset
                        {
                            if (cIdx == 0)
                            {
                                sao_eo_class_luma = stream.getCabac().read_sao_class();      // ae(v)
                                picture.SaoEoClass[cIdx][rx][ry] = sao_eo_class_luma & 0xFF;
                            }
                            else if (cIdx == 1)
                            {
                                sao_eo_class_chroma = stream.getCabac().read_sao_class();    // ae(v)
                                picture.SaoEoClass[cIdx][rx][ry] = sao_eo_class_chroma & 0xFF;
                                picture.SaoEoClass[cIdx + 1][rx][ry] = sao_eo_class_chroma & 0xFF;
                            }

                            for (int i = 0; i < 4; i++)
                                picture.sao_offset_sign[cIdx][rx][ry][i] = i < 2 ? 0 : 1;
                        }
                    }
                }
                else
                {
                    picture.SaoTypeIdx[cIdx][rx][ry] = 0;
                    picture.SaoEoClass[cIdx][rx][ry] = 0;
                    picture.sao_band_position[cIdx][rx][ry] = 0;

                    for (int i = 0; i < 4; i++)
                    {
                        picture.sao_offset_abs[cIdx][rx][ry][i] = 0;
                        picture.sao_offset_sign[cIdx][rx][ry][i] = 0;
                    }
                }
            }
            else if (sao_merge_left_flag)
            {
                picture.SaoTypeIdx[cIdx][rx][ry] = picture.SaoTypeIdx[cIdx][rx - 1][ry];
                picture.SaoEoClass[cIdx][rx][ry] = picture.SaoEoClass[cIdx][rx - 1][ry];
                picture.sao_band_position[cIdx][rx][ry] = picture.sao_band_position[cIdx][rx - 1][ry];

                for (int i = 0; i < 4; i++)
                {
                    picture.sao_offset_abs[cIdx][rx][ry][i] = picture.sao_offset_abs[cIdx][rx - 1][ry][i];
                    picture.sao_offset_sign[cIdx][rx][ry][i] = picture.sao_offset_sign[cIdx][rx - 1][ry][i];
                }
            }
            else if (sao_merge_up_flag)
            {
                picture.SaoTypeIdx[cIdx][rx][ry] = picture.SaoTypeIdx[cIdx][rx][ry - 1];
                picture.SaoEoClass[cIdx][rx][ry] = picture.SaoEoClass[cIdx][rx][ry - 1];
                picture.sao_band_position[cIdx][rx][ry] = picture.sao_band_position[cIdx][rx][ry - 1];

                for (int i = 0; i < 4; i++)
                {
                    picture.sao_offset_abs[cIdx][rx][ry][i] = picture.sao_offset_abs[cIdx][rx][ry - 1][i];
                    picture.sao_offset_sign[cIdx][rx][ry][i] = picture.sao_offset_sign[cIdx][rx][ry - 1][i];
                }
            }

            if (header.pps.pps_range_ext != null)
            {
                int log2OffsetScale = (int)(cIdx == 0 ?
                    header.pps.pps_range_ext.log2_sao_offset_scale_luma :
                    header.pps.pps_range_ext.log2_sao_offset_scale_chroma);

                picture.SaoOffsetVal[cIdx][rx][ry][0] = 0;

                for (int i = 0; i < 4; i++)
                    picture.SaoOffsetVal[cIdx][rx][ry][i + 1] =
                        (1 - 2 * picture.sao_offset_sign[cIdx][rx][ry][i]) *
                        picture.sao_offset_abs[cIdx][rx][ry][i] << log2OffsetScale;

            }
        }
    }
}

