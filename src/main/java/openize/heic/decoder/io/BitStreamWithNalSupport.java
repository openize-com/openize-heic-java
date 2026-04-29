/*
 * Openize.HEIC
 * Copyright (c) 2024-2026 Openize Pty Ltd.
 *
 * This file is part of Openize.HEIC.
 *
 * Openize.HEIC is available under Openize license, which is
 * available along with Openize.HEIC sources.
 */

package openize.heic.decoder.io;

import openize.heic.decoder.Cabac;
import openize.heic.decoder.CabacType;
import openize.heic.decoder.DecoderContext;
import openize.heic.decoder.HeicPicture;
import openize.io.IOSeekMode;
import openize.io.IOStream;
import openize.isobmff.io.BitStreamReader;

import java.util.HashMap;
import java.util.Map;


/**
 * <p>
 * The BitStreamWithNalSupport class is designed to read bits from a specified stream.
 * It allows to ignore specified byte sequences while reading.
 * </p>
 */
public class BitStreamWithNalSupport extends BitStreamReader
{
    private final Cabac cabac;
    /**
     * <p>
     * Dictionary of images context information.
     * </p>
     */
    private final HashMap<Long, DecoderContext> contextDictionary;
    private /*UInt32*/ long currentImageId;
    /**
     * <p>
     * Nal Unit reader mode.
     * </p>
     */
    private boolean _nalMode = false;
    /**
     * <p>
     * Previous read byte.
     * </p>
     */
    private  short _prevReadByte = (short) 0xFF;
    /**
     * <p>
     * The byte read before previous.
     * </p>
     */
    private  short _prevPrevReadByte = (short) 0xFF;

    public BitStreamWithNalSupport(IOStream stream)
    {
        this(stream, 4096);
    }

    /**
     * <p>
     * Creates a class object with a stream object and an optional buffer size as parameters.
     * </p>
     *
     * @param stream     The source stream.
     * @param bufferSize The buffer size.
     */
    public BitStreamWithNalSupport(IOStream stream, int bufferSize)
    {
        super(stream, bufferSize);

        contextDictionary = new HashMap<>();
        cabac = new Cabac(this);
    }

    /**
     * <p>
     * Context-adaptive arithmetic entropy-decoder.
     * </p>
     */
    public final Cabac getCabac()
    {
        return cabac;
    }

    /**
     * <p>
     * Returns the current image context.
     * </p>
     */
    public final DecoderContext getContext()
    {
        return contextDictionary.get(getCurrentImageId());
    }

    /**
     * <p>
     * Current image identifier.
     * </p>
     */
    public final /*UInt32*/long getCurrentImageId()
    {
        return currentImageId;
    }

    /**
     * <p>
     * Current image identifier.
     * </p>
     */
    public final void setCurrentImageId(/*UInt32*/long value)
    {
        currentImageId = value;
    }

    /**
     * <p>
     * Creates an image context object.
     * </p>
     *
     * @param imageId Image identifier.
     */
    public final void createNewImageContext(/*UInt32*/long imageId)
    {
        if (!contextDictionary.containsKey(imageId))
        {
            contextDictionary.put(imageId, new DecoderContext());
        }
        setCurrentImageId(imageId);
    }

    /**
     * <p>
     * Deletes the image context object by id.
     * </p>
     *
     * @param imageId Image identifier.
     */
    public final void deleteImageContext(/*UInt32*/long imageId)
    {
        for (Map.Entry<Long, HeicPicture> entry : contextDictionary.get(imageId)
                                                                   .getPictures()
                                                                   .entrySet())
        {
            entry.getValue().cleanMemory();
        }

        contextDictionary.remove(imageId);
    }

    /**
     * <p>
     * Turns on Nal Unit reader mode which ignores specified by standard byte sequences.
     * </p>
     */
    public final void turnOnNalUnitMode()
    {
        _nalMode = true;
        _prevReadByte = (short) 0xFF;
        _prevPrevReadByte = (short) 0xFF;
    }

    /**
     * <p>
     * Turns off Nal Unit reader mode.
     * </p>
     */
    public final void turnOffNulUnitMode()
    {
        _nalMode = false;
    }

    // shall not occur 
    // 0x000000
    // 0x000001
    // 0x000002

    // any four-byte sequence that starts with 0x000003 other than the following sequences shall not occur at any byte-aligned position:
    // 0x00000300
    // 0x00000301
    // 0x00000302
    // 0x00000303

    /**
     * <p>
     * Reads the specified number of bits from the stream.
     * </p>
     *
     * @param bitCount The required number of bits to read.
     * @return The integer value.
     */
    @Override
    public int read(int bitCount)
    {
        if (bitCount <= 0 || bitCount > 32)
        {
            if (bitCount == 0)
            {
                return 0;
            }

            throw new IndexOutOfBoundsException("bitCount");
        }

        int value = 0;
        int remainingBits = bitCount;

        if (state.getBufferPosition() < 0)
        {
            fillBufferFromStream();
        }

        while (remainingBits > 0)
        {
            if (state.getBitIndex() == 8)
            {
                if (_nalMode)
                {
                    _prevPrevReadByte = _prevReadByte;
                    _prevReadByte = (short)(state.getBuffer()[state.getBufferPosition()] & 0xFF);
                }

                state.setBitIndex(0);
                state.setBufferPosition(state.getBufferPosition() + 1)/*Property++*/;

                if (state.getBufferPosition() == state.getBufferActiveLength())
                {
                    fillBufferFromStream();
                }
            }

            if (_nalMode)
            {
                if ((_prevPrevReadByte & 0xFF) == 0x00 &&
                        (_prevReadByte & 0xFF) == 0x00 &&
                        (state.getBuffer()[state.getBufferPosition()] & 0xFF) == 0x03)
                {
                    _prevPrevReadByte = _prevReadByte;
                    _prevReadByte = (short)(state.getBuffer()[state.getBufferPosition()] & 0xFF);
                    state.setBufferPosition(state.getBufferPosition() + 1)/*Property++*/;
                    if (state.getBufferPosition() == state.getBufferActiveLength())
                    {
                        fillBufferFromStream();
                    }
                }
            }

            int bitsToRead = Math.min(remainingBits, 8 - state.getBitIndex());
            int mask = (1 << bitsToRead) - 1;

            value <<= bitsToRead;
            value |= ((state.getBuffer()[state.getBufferPosition()] & 0xFF) >> (8 - state.getBitIndex() - bitsToRead)) & mask;

            state.setBitIndex(state.getBitIndex() + bitsToRead);
            remainingBits -= bitsToRead;
        }

        return value;
    }

    /**
     * <p>
     * Reads bytes as ASCII characters until '\0'.
     * </p>
     *
     * @return String value.
     */
    @Override
    public final /*new*/ String readString()
    {
        StringBuilder output = new StringBuilder(50);
        int b;

        do
        {
            b = read(8);
            //if (b < 127 && b > 31)
            output.append((char) b);
        } while (b != 0);

        return output.toString();
    }

    /**
     * <p>
     * Skip the specified number of bits in the stream.
     * </p>
     *
     * @param bitsNumber Number of bits to skip.
     */
    @Override
    public /*new*/ void skipBits(int bitsNumber)
    {
        int remainingBits = bitsNumber;

        int bitsInBuffer = Math.min(remainingBits, (state.getBufferActiveLength() - state.getBufferPosition()) * 8 - state.getBitIndex());

        int bitsToRead = Math.min(remainingBits, bitsInBuffer);

        while (bitsToRead > 0)
        {
            int limitCount = Math.min(bitsToRead, 32);
            read(limitCount);
            bitsToRead -= limitCount;
            remainingBits -= limitCount;
        }

        if (remainingBits == 0)
        {
            return;
        }

        if (_nalMode)
        {
            while (remainingBits >= 32)
            {
                read(32);
                remainingBits -= 32;
            }
        }
        else
        {
            stream.seek(remainingBits / 8, IOSeekMode.CURRENT);
            fillBufferFromStream();
            state.setBitIndex(0);
            remainingBits = remainingBits % 8;
        }

        if (remainingBits != 0)
        {
            read(remainingBits);
        }
    }

    /**
     * <p>
     * Read an unsigned integer 0-th order Exp-Golomb-coded syntax element with the left bit first.
     * </p>
     *
     * @return An unsigned integer.
     */
    public final /*UInt32*/long readUev()
    {
        int leadingZeroBits = 0;

        while (read(1) == 0)
            leadingZeroBits++;

        return ((((long) 1 << leadingZeroBits) - 1 + read(leadingZeroBits)) & 0xFFFFFFFFL);
    }

    /**
     * <p>
     * Read a signed integer 0-th order Exp-Golomb-coded syntax element with the left bit first.
     * </p>
     *
     * @return A signed integer.
     */
    public final int readSev()
    {
        /*UInt32*/
        long codeNum = readUev();

        boolean negative = ((((codeNum & 0xFFFFFFFFL) & 1) & 0xFFFFFFFFL) == 0);
        return (negative ? -1 : 1) * (int) ((((codeNum & 0xFFFFFFFFL) + 1) & 0xFFFFFFFFL) / 2);
    }

    public final int readAev()
    {
        return readAev(CabacType.sao_merge_flag);
    }

    /**
     * <p>
     * Placeholder for not implemented CABAC syntax elements. Always throws an exception.
     * </p>
     */
    public final int readAev(CabacType type)
    {
        throw new UnsupportedOperationException();
    }

    public final boolean readAevFlag()
    {
        return readAevFlag(CabacType.sao_merge_flag);
    }

    /**
     * <p>
     * Placeholder for not implemented CABAC syntax flag elements. Always throws an exception.
     * </p>
     */
    final boolean readAevFlag(CabacType type)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * <p>
     * Checks if there is more data in the RBSP.
     * </p>
     *
     * @param endPosition End position of current RBSP.
     * @return True if there is more data in the RBSP, false otherwise
     */
    public final boolean hasMoreRbspData(/*UInt64*/long endPosition)
    {
        // - If there is no more data in the raw byte sequence payload (RBSP), the return value of more_rbsp_data( ) is equal to FALSE
        // - Otherwise, the RBSP data are searched for the last (least significant, right-most) bit equal to 1 that is present in
        //   the RBSP. Given the position of this bit, which is the first bit(rbsp_stop_one_bit) of the rbsp_trailing_bits()
        //   syntax structure, the following applies:
        //     - If there is more data in an RBSP before the rbsp_trailing_bits() syntax structure, the return value of
        //       more_rbsp_data() is equal to TRUE.
        //     – Otherwise, the return value of more_rbsp_data() is equal to FALSE. 

        if (getBitPosition() >= endPosition)
        {
            throw new UnsupportedOperationException("Parser logic error!");
        }

        if (getBitPosition() == endPosition)
        {
            return false;
        }

        while (notByteAligned())
            skipBits(1);

        return peek(8) != 0x80;
    }
}

