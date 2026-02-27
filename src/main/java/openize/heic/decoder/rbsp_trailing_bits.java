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


class rbsp_trailing_bits
{
    public rbsp_trailing_bits(BitStreamWithNalSupport stream)
    {
        /*int one =*/
        stream.read(1);   /* equal to 1; rbsp_stop_one_bit */

        while (stream.notByteAligned())
        {
            stream.skipBits(1);     /* equal to 0; rbsp_alignment_zero_bit */
        }
    }
}
