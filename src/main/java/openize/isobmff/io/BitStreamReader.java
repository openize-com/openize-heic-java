/*
 * FileFormat.IsoBmff
 * Copyright (c) 2024-2026 Openize Pty Ltd.
 *
 * This file is part of FileFormat.IsoBmff.
 *
 * FileFormat.IsoBmff is available under MIT license, which is
 * available along with FileFormat.IsoBmff sources.
 */

package openize.isobmff.io;

import openize.io.IOSeekMode;
import openize.io.IOStream;

import java.util.Arrays;
import java.util.Objects;


/**
 * <p>
 * The BitStreamReader class is designed to read bits from a specified stream.
 * It reads a minimal amount of bytes from the stream into an intermediate buffer
 * and then reads the bits from the buffer, returning the read value.
 * If there is still enough data in the buffer, the data is read from it.
 * </p>
 */
public class BitStreamReader
{
    /**
     * <p>
     * File stream.
     * </p>
     */
    protected final IOStream stream;

    /**
     * <p>
     * Bit reader state.
     * </p>
     */
    protected final BitReaderState state = new BitReaderState();

    public BitStreamReader(IOStream stream)
    {
        this(stream, 4);
    }

    /**
     * <p>
     * The constructor takes a Stream object and an optional buffer size as parameters.
     * </p>
     *
     * @param stream     The source stream.
     * @param bufferSize The buffer size.
     */
    public BitStreamReader(IOStream stream, int bufferSize)
    {
        if (stream == null)
        {
            throw new NullPointerException("stream");
        }

        if (bufferSize <= 0)
        {
            throw new IndexOutOfBoundsException("bufferSize");
        }

        this.stream = stream;
        state.buffer = new byte[bufferSize];
        state.bufferActiveLength = bufferSize;
        state.bufferPosition = -1;
    }

    /**
     * <p>
     * Gets the current position within the bitstream.
     * </p>
     * <p>The bitstream position is x8 of stream position, adjusted according to the number of bits read from the latest byte.</p>
     *
     * @return Unsigned long value.
     */
    public final /*UInt64*/long getBitPosition()
    {
        if (stream.getPosition() == 0 && state.getBufferPosition() == -1)
            return 0;
        return ((stream.getPosition() - state.bufferActiveLength + state.bufferPosition) * 8L + state.bitIndex);
    }

    /**
     * <p>
     * Sets the current position within the bitstream.
     * </p>
     *
     * @param bytePosition The new byte position within the bitstream.
     */
    public final void setBytePosition(long bytePosition)
    {
        stream.setPosition(bytePosition);
        state.reset();
    }

    /**
     * <p>
     * Indicates if the current position in the bitstream is on a byte boundary.
     * </p>
     *
     * @return Returns true if the current position in the bitstream is on a byte boundary, false otherwise.
     */
    public final boolean notByteAligned()
    {
        return state.bitIndex != 0 && state.bitIndex != 8;
    }

    /**
     * <p>
     * Indicates if there are more data in the bitstream.
     * </p>
     *
     * @return True if there are more data in the bitstream, false otherwise.
     */
    public final boolean moreData()
    {
        return (state.bufferPosition >= 0 && state.bitIndex < 8) ||
                (stream.getPosition() < stream.getLength());
    }

    /**
     * <p>
     * Fill reader buffer with data from stream.
     * </p>
     *
     * @return The total amount of bytes read into the buffer.
     */
    protected final int fillBufferFromStream()
    {
        final long streamPosition = stream.getPosition();
        final long streamLength = stream.getLength();
        state.bufferActiveLength = Math.min((int) (streamLength - streamPosition), state.buffer.length);
        state.bufferPosition = 0;
        return stream.read(state.buffer, 0, state.bufferActiveLength);
    }

    /**
     * <p>
     * Reads the specified number of bits from the stream.
     * </p>
     *
     * @param bitCount The required number of bits to read.
     * @return The integer value.
     */
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

        if (state.bufferPosition < 0)
        {
            fillBufferFromStream();
        }

        while (remainingBits > 0)
        {
            if (state.bitIndex == 8)
            {
                state.bitIndex = 0;
                state.bufferPosition++;

                if (state.bufferPosition == state.bufferActiveLength)
                {
                    fillBufferFromStream();
                }
            }

            int bitsToRead = Math.min(remainingBits, 8 - state.bitIndex);
            int mask = (1 << bitsToRead) - 1;

            value <<= bitsToRead;
            value |= ((state.buffer[state.bufferPosition] & 0xFF) >> (8 - state.bitIndex - bitsToRead)) & mask;

            state.bitIndex += bitsToRead;
            remainingBits -= bitsToRead;
        }

        return value;
    }

    /**
     * <p>
     * Reads bytes as ASCII characters until '\0'.
     * </p>
     *
     * @return The string value.
     */
    public String readString()
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
     * Reads one bit and returns true if it is 1, otherwise false.
     * </p>
     *
     * @return The boolean value.
     */
    public final boolean readFlag()
    {
        return read(1) == 1;
    }

    /**
     * <p>
     * Peeks the specified number of bits from the stream.
     * This method does not change the position of the underlying stream, state of the reader remains unchanged.
     * </p>
     *
     * @param bitCount The required number of bits to read.
     * @return The integer value.
     */
    public final int peek(int bitCount)
    {
        BitReaderState previousState = state.copy();
        previousState.lastStreamPosition = this.stream.getPosition();

        int value = read(bitCount);

        this.stream.setPosition(previousState.lastStreamPosition);
        previousState.cloneTo(state);
        return value;
    }

    /**
     * <p>
     * Skip the specified number of bits in the stream.
     * </p>
     *
     * @param bitsNumber Number of bits to skip.
     */
    public void skipBits(int bitsNumber)
    {
        int remainingBits = bitsNumber;

        int bitsInBuffer = Math.min(remainingBits, (state.bufferActiveLength - state.bufferPosition) * 8 - state.bitIndex);

        int bitsToRead = Math.min(remainingBits, bitsInBuffer);
        read(bitsToRead);
        remainingBits -= bitsToRead;

        if (remainingBits == 0)
        {
            return;
        }

        stream.seek(remainingBits / 8, IOSeekMode.CURRENT);
        remainingBits = remainingBits % 8;

        state.bufferPosition = 0;
        state.bitIndex = 0;

        if (stream.getPosition() < stream.getLength())
        {
            final byte[] buffer = state.buffer;
            stream.read(buffer, 0, state.bufferActiveLength);
        }
        else
        {
            state.bufferPosition = -1;
        }

        if (remainingBits == 0)
        {
            return;
        }

        read(remainingBits);
    }

    /**
     * <p>
     * Reads a bit at the specified position.
     * This method does not change the position of the underlying stream, state of the reader remains unchanged.
     * This method is approximately 50% slower than the Read method.
     * </p>
     * @param position The started position for reading.
     * @return A read bit.
     */
    public final int getBit(long position)
    {
        BitReaderState previousState = state.copy();
        previousState.lastStreamPosition = this.stream.getPosition();
        this.state.reset();

        long bytePosition = position / 8;
        long bitPosition = position % 8;
        this.stream.setPosition(bytePosition);
        int value = 0;
        for (int i = 0; i <= bitPosition; i++)
        {
            value = this.read(1);
        }

        this.stream.setPosition(previousState.lastStreamPosition);
        previousState.cloneTo(state);
        return value;
    }

    /**
     * <p>
     * Supporting reader structure that contains buffer with read data, position of cursor and other supporting data.
     * </p>
     */
    protected static class BitReaderState
    {
        private  byte[] buffer;
        private int bufferActiveLength;
        private int bufferPosition;
        private int bitIndex;
        private long lastStreamPosition;

        /**
         * <p>
         * Buffer data read from stream.
         * </p>
         * @return The buffer data read from stream.
         */
        public final byte[] getBuffer()
        {
            return buffer;
        }

        /**
         * <p>
         * Tracks the current byte position in the buffer.
         * </p>
         * @return the current byte position in the buffer.
         */
        public final int getBufferPosition()
        {
            return bufferPosition;
        }

        /**
         * <p>
         * Tracks the current byte position in the buffer.
         * </p>
         * @param value a new byte position in the buffer
         */
        public final void setBufferPosition(int value)
        {
            bufferPosition = value;
        }

        /**
         * <p>
         * Tracks the current bit position in the buffer.
         * </p>
         * @return the current bit position in the buffer.
         */
        public final int getBitIndex()
        {
            return bitIndex;
        }

        /**
         * <p>
         * Tracks the current bit position in the buffer.
         * </p>
         * @param value a new bit position in the buffer.
         */
        public final void setBitIndex(int value)
        {
            bitIndex = value;
        }

        public int getBufferActiveLength()
        {
            return bufferActiveLength;
        }

        /**
         * <p>
         * Resets buffer to empty state.
         * </p>
         */
        public final void reset()
        {
            bitIndex = 0;
            bufferPosition = -1;
            Arrays.fill(buffer, (byte) 0);
        }

        /**
         * <p>
         * Returns buffer content in hex notation.
         * </p>
         */
        @Override
        public final /*new*/ String toString()
        {
            StringBuilder sb = new StringBuilder(bufferActiveLength*2+1);
            int count = bufferActiveLength;
            if (count > 0)
            {
                for (byte b : buffer)
                {
                    sb.append(String.format("%02x", b))
                      .append(' ');
                    if (--count <= 0) break;
                }
                sb.setLength(sb.length()-1);
            }
            return sb.toString();
        }

        public void cloneTo(BitReaderState that)
        {
            that.buffer = buffer;
            that.bufferActiveLength = bufferActiveLength;
            that.bufferPosition = bufferPosition;
            that.bitIndex = bitIndex;
            that.lastStreamPosition = lastStreamPosition;
        }

        public BitReaderState copy()
        {
            BitReaderState struct = new BitReaderState();
            cloneTo(struct);
            return struct;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o)
            {
                return true;
            }
            if (!(o instanceof BitReaderState))
            {
                return false;
            }
            BitReaderState that = (BitReaderState) o;
            return bufferActiveLength == that.bufferActiveLength && bufferPosition == that.bufferPosition
                    && bitIndex == that.bitIndex && lastStreamPosition == that.lastStreamPosition
                    && buffer == that.buffer;
        }

        @SuppressWarnings("ArrayHashCode")
        @Override
        public int hashCode()
        {
            final int code = buffer.hashCode();
            return Objects.hash(code, bufferActiveLength, bufferPosition, bitIndex, lastStreamPosition);
        }
    }
}
