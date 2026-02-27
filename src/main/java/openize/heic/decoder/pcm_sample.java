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


class pcm_sample
{

    public pcm_sample(BitStreamWithNalSupport stream, slice_segment_header header, int log2CbSize)
    {
        HeicPicture picture = header.parentPicture;

        picture.pcm_sample_luma = new int[1 << (log2CbSize << 1)];

        for (int i = 0; i < 1 << (log2CbSize << 1); i++)
            picture.pcm_sample_luma[i] = stream.read(header.pps.sps.pcm_sample_bit_depth_luma & 0xFF);

        picture.pcm_sample_chroma = new int[((2 << (log2CbSize << 1)) / ((header.pps.sps.getSubWidthC() & 0xFF) * (header.pps.sps.getSubHeightC() & 0xFF)))];

        if ((header.pps.sps.getChromaArrayType() & 0xFFFFFFFFL) != 0)
        {
            for (int i = 0; i < ((2 << (log2CbSize << 1)) / ((header.pps.sps.getSubWidthC() & 0xFF) * (header.pps.sps.getSubHeightC() & 0xFF))); i++)
                picture.pcm_sample_chroma[i] = stream.read(header.pps.sps.pcm_sample_bit_depth_chroma & 0xFF);
        }
    }
}
