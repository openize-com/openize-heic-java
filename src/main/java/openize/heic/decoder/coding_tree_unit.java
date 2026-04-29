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



class coding_tree_unit
{
    public coding_tree_unit(BitStreamWithNalSupport stream, 
        slice_segment_header header)
    {
        seq_parameter_set_rbsp sps = header.pps.sps;
        HeicPicture picture = header.parentPicture;

        int xCtb = (int)((header.getCtbAddrInRs() & 0xFFFFFFFFL) % (sps.getPicWidthInCtbsY() & 0xFFFFFFFFL)) << (sps.getCtbLog2SizeY() & 0xFF);
        int yCtb = (int)((header.getCtbAddrInRs() & 0xFFFFFFFFL) / (sps.getPicWidthInCtbsY() & 0xFFFFFFFFL)) << (sps.getCtbLog2SizeY() & 0xFF);

        picture.SliceAddrRs[xCtb >> (sps.getCtbLog2SizeY() & 0xFF)][yCtb >> (sps.getCtbLog2SizeY() & 0xFF)] = (int)header.getSliceAddrRs();
        picture.SliceHeaderIndex[xCtb][yCtb] = header.slice_index;

        if (header.slice_sao_luma_flag || header.slice_sao_chroma_flag)
            new sao(stream, xCtb >> (sps.getCtbLog2SizeY() & 0xFF), yCtb >> (sps.getCtbLog2SizeY() & 0xFF), header);

        new coding_quadtree(stream, header, xCtb, yCtb, sps.getCtbLog2SizeY() & 0xFF, 0);
    }
}

