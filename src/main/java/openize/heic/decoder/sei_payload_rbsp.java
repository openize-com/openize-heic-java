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


class sei_payload_rbsp extends NalUnit
{
    public sei_payload_rbsp(BitStreamWithNalSupport stream, /*UInt64*/long startPosition, int size)
    {
        super(stream, startPosition, size);

        // int payloadType = 0;
        while (true)
        {
            int read = (/*Byte*/byte) stream.read(8) & 0xFF;
            //payloadType += read;
            if (read != 255)
            {
                break;
            }
        }

        int payloadSize = 0;
        while (true)
        {
            int read = (/*Byte*/byte) stream.read(8) & 0xFF;
            payloadSize += read;
            if (read != 255)
            {
                break;
            }
        }

        stream.skipBits(payloadSize);
    }
}
