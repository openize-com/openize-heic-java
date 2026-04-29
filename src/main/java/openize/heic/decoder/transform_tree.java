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


class transform_tree
{

    public transform_tree(BitStreamWithNalSupport stream, slice_segment_header header,
                          boolean IntraSplitFlag, /*UInt32*/long MaxTrafoDepth, PartMode partMode,
                          int x0, int y0, int xBase, int yBase, int log2TrafoSize, int trafoDepth, int blkIdx)
    {
        seq_parameter_set_rbsp sps = header.pps.sps;
        HeicPicture picture = header.parentPicture;

        if (picture.cbf_cb[x0][y0] == null)
        {
            picture.cbf_cb[x0][y0] = new boolean[(int) (((MaxTrafoDepth & 0xFFFFFFFFL) + 1) & 0xFFFFFFFFL)];
        }
        if (picture.cbf_cr[x0][y0] == null)
        {
            picture.cbf_cr[x0][y0] = new boolean[(int) (((MaxTrafoDepth & 0xFFFFFFFFL) + 1) & 0xFFFFFFFFL)];
        }
        if (picture.cbf_luma[x0][y0] == null)
        {
            picture.cbf_luma[x0][y0] = new boolean[(int) (((MaxTrafoDepth & 0xFFFFFFFFL) + 1) & 0xFFFFFFFFL)];
        }

        boolean split_transform_flag;
        if (log2TrafoSize <= (sps.getMaxTbLog2SizeY() & 0xFF) &&
                log2TrafoSize > (sps.getMinTbLog2SizeY() & 0xFF) &&
                trafoDepth < (MaxTrafoDepth & 0xFFFFFFFFL) &&
                !(IntraSplitFlag && (trafoDepth == 0)))
        {
            split_transform_flag = stream.getCabac().read_split_transform_flag(log2TrafoSize);
        }
        else
        {
            boolean interSplitFlag =
                    (sps.max_transform_hierarchy_depth_inter & 0xFFFFFFFFL) == 0 &&
                            picture.CuPredMode[x0][y0] == PredMode.MODE_INTER &&
                            partMode != PartMode.PART_2Nx2N &&
                            trafoDepth == 0;

            split_transform_flag =
                    log2TrafoSize > (sps.getMaxTbLog2SizeY() & 0xFF) ||
                            (IntraSplitFlag && trafoDepth == 0) ||
                            interSplitFlag;
        }

        if ((log2TrafoSize > 2 && (sps.getChromaArrayType() & 0xFFFFFFFFL) != 0) || (sps.getChromaArrayType() & 0xFFFFFFFFL) == 3)
        {
            if (trafoDepth == 0 || picture.cbf_cb[xBase][yBase][trafoDepth - 1])
            {
                picture.cbf_cb[x0][y0][trafoDepth] = stream.getCabac().read_cbf_chroma(trafoDepth);

                if ((sps.getChromaArrayType() & 0xFFFFFFFFL) == 2 && (!split_transform_flag || log2TrafoSize == 3))
                {
                    picture.cbf_cb[x0][y0 + (1 << (log2TrafoSize - 1))][trafoDepth] = stream.getCabac().read_cbf_chroma(trafoDepth);
                }
            }

            if (trafoDepth == 0 || picture.cbf_cr[xBase][yBase][trafoDepth - 1])
            {
                picture.cbf_cr[x0][y0][trafoDepth] = stream.getCabac().read_cbf_chroma(trafoDepth);

                if ((sps.getChromaArrayType() & 0xFFFFFFFFL) == 2 && (!split_transform_flag || log2TrafoSize == 3))
                {
                    picture.cbf_cr[x0][y0 + (1 << (log2TrafoSize - 1))][trafoDepth] = stream.getCabac().read_cbf_chroma(trafoDepth);
                }
            }
        }

        if (split_transform_flag)
        {
            int x1 = x0 + (1 << (log2TrafoSize - 1));
            int y1 = y0 + (1 << (log2TrafoSize - 1));
            new transform_tree(stream, header, IntraSplitFlag, MaxTrafoDepth, partMode, x0, y0, x0, y0, log2TrafoSize - 1, trafoDepth + 1, 0);
            new transform_tree(stream, header, IntraSplitFlag, MaxTrafoDepth, partMode, x1, y0, x0, y0, log2TrafoSize - 1, trafoDepth + 1, 1);
            new transform_tree(stream, header, IntraSplitFlag, MaxTrafoDepth, partMode, x0, y1, x0, y0, log2TrafoSize - 1, trafoDepth + 1, 2);
            new transform_tree(stream, header, IntraSplitFlag, MaxTrafoDepth, partMode, x1, y1, x0, y0, log2TrafoSize - 1, trafoDepth + 1, 3);
        }
        else
        {
            if (picture.CuPredMode[x0][y0] == PredMode.MODE_INTRA ||
                    trafoDepth != 0 ||
                    picture.cbf_cb[x0][y0][trafoDepth] ||
                    picture.cbf_cr[x0][y0][trafoDepth] ||
                    ((header.pps.sps.getChromaArrayType() & 0xFFFFFFFFL) == 2 &&
                            (picture.cbf_cb[x0][y0 + (1 << (log2TrafoSize - 1))][trafoDepth] ||
                                    picture.cbf_cr[x0][y0 + (1 << (log2TrafoSize - 1))][trafoDepth])))
            {
                picture.cbf_luma[x0][y0][trafoDepth] = stream.getCabac().read_cbf_luma(trafoDepth);
            }

            new transform_unit(stream, header, partMode, x0, y0, xBase, yBase, log2TrafoSize, trafoDepth, blkIdx);
        }
    }
}

