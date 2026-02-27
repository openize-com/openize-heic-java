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


public class NalUnit
{
    public final NalHeader NalHeader;
    public final /*UInt64*/ long StartPosition;
    public final int Size;

    public NalUnit(BitStreamWithNalSupport stream, /*UInt64*/long startPosition, int size)
    {
        NalHeader = new NalHeader(stream);

        StartPosition = startPosition;
        Size = size;
    }

    public static NalUnit parseKnownUnit(BitStreamWithNalSupport stream, int size, NalUnitType type)
    {
        NalUnit nalUnit;
        /*UInt64*/
        long startPosition = stream.getBitPosition();
        stream.turnOnNalUnitMode();

        // Discard all NAL units with nuh_layer_id > 0
        // These will have to be handled by an SHVC decoder.

        // throw away NALs from higher TIDs than currently selected

        if (type.getUnitCode() < 32)
        {
            // VCL
            nalUnit = new slice_segment_layer_rbsp(stream, startPosition, size);
        }
        else
        {
            // non-VCL
            switch (type)
            {
                case VPS_NUT:     // 32
                    nalUnit = new video_parameter_set_rbsp(stream, startPosition, size);
                    break;
                case SPS_NUT:     // 33
                    nalUnit = new seq_parameter_set_rbsp(stream, startPosition, size);
                    break;
                case PPS_NUT:     // 34
                    nalUnit = new pic_parameter_set_rbsp(stream, startPosition, size);
                    break;
                case PREFIX_SEI_NUT: // 39
                case SUFFIX_SEI_NUT: // 40
                    nalUnit = new sei_payload_rbsp(stream, startPosition, size);
                    break;
                default:
                    nalUnit = new NalUnit(stream, startPosition, size);
                    break;
            }
        }

        stream.turnOffNulUnitMode();
        /*UInt64*/
        long currentPosition = stream.getBitPosition();

        if (currentPosition - startPosition > (size & 0xFFFFFFFFL) * 8L)
        {
            throw new IndexOutOfBoundsException();
        }

        if (currentPosition - startPosition < (size & 0xFFFFFFFFL) * 8L)
        {
            stream.skipBits(size * 8 - (int) (currentPosition - startPosition));
        }

        return nalUnit;
    }

    public static NalUnit parseUnit(BitStreamWithNalSupport stream)
    {
        int size = stream.read(32);
        int peek = stream.peek(8);
        NalUnitType type = NalUnitType.codeToType((peek >> 1) & 0x3F);
        return parseKnownUnit(stream, size, type);
    }

    public final /*UInt64*/long getEndPosition()
    {
        return StartPosition + (Size & 0xFFFFFFFFL) * 8L;
    }

    @Override
    public String toString()
    {
        return "NAL Unit " + NalHeader.type;
    }
}
