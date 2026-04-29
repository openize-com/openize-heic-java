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



class coding_quadtree
{

    public coding_quadtree(BitStreamWithNalSupport stream, slice_segment_header header,
        int x0, int y0, int log2CbSize, int cqtDepth)
    {
        seq_parameter_set_rbsp sps = header.pps.sps;
        HeicPicture picture = header.parentPicture;
        boolean split_cu_flag;

        if (x0 + (1L << log2CbSize) <= (header.pps.sps.pic_width_in_luma_samples & 0xFFFFFFFFL) &&
            y0 + (1L << log2CbSize) <= (header.pps.sps.pic_height_in_luma_samples & 0xFFFFFFFFL) &&
            log2CbSize > (sps.getMinCbLog2SizeY() & 0xFF))
        {
            split_cu_flag = stream.getCabac()
                                  .read_split_cu_flag(x0, y0, picture, cqtDepth);
        }
        else
        {
            split_cu_flag = log2CbSize > (sps.getMinCbLog2SizeY() & 0xFF);
        }

        /*UInt32*/long Log2MinCuQpDeltaSize = ((header.pps.sps.getCtbLog2SizeY() & 0xFF) - (header.pps.diff_cu_qp_delta_depth & 0xFFFFFFFFL)) & 0xFFFFFFFFL;
        /*UInt32*/long Log2MinCuChromaQpOffsetSize = ((header.pps.sps.getCtbLog2SizeY() & 0xFF) - (header.pps.pps_range_ext.diff_cu_chroma_qp_offset_depth & 0xFFFFFFFFL)) & 0xFFFFFFFFL;

        if (header.pps.cu_qp_delta_enabled_flag && log2CbSize >= (Log2MinCuQpDeltaSize & 0xFFFFFFFFL))
        {
            stream.getContext().setCuQpDeltaCoded(false);
            stream.getContext().setCuQpDeltaVal(0);
        }

        if (header.cu_chroma_qp_offset_enabled_flag &&
            log2CbSize >= (Log2MinCuChromaQpOffsetSize & 0xFFFFFFFFL))
        {
            stream.getContext().setCuChromaQpOffsetCoded(false);
        }

        if (split_cu_flag)
        {
            int x1 = x0 + (1 << (log2CbSize - 1));
            int y1 = y0 + (1 << (log2CbSize - 1));
            new coding_quadtree(stream, header, x0, y0, log2CbSize - 1, cqtDepth + 1);

            if (x1 < (header.pps.sps.pic_width_in_luma_samples & 0xFFFFFFFFL))
                new coding_quadtree(stream, header, x1, y0, log2CbSize - 1, cqtDepth + 1);
            if (y1 < (header.pps.sps.pic_height_in_luma_samples & 0xFFFFFFFFL))
                new coding_quadtree(stream, header, x0, y1, log2CbSize - 1, cqtDepth + 1);
            if (x1 < (header.pps.sps.pic_width_in_luma_samples & 0xFFFFFFFFL) && y1 < (header.pps.sps.pic_height_in_luma_samples & 0xFFFFFFFFL))
                new coding_quadtree(stream, header, x1, y1, log2CbSize - 1, cqtDepth + 1);
        }
        else
        {
            picture.setCtDepth(x0, y0, log2CbSize, cqtDepth);
            new coding_unit(stream, header, x0, y0, log2CbSize);
        }
    }
}
