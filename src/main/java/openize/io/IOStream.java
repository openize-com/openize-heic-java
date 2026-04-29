/*
 * Openize.HEIC
 * Copyright (c) 2024-2026 Openize Pty Ltd.
 *
 * This file is part of Openize.HEIC.
 *
 * Openize.HEIC is available under Openize license, which is
 * available along with Openize.HEIC sources.
 */

package openize.io;

import java.io.Closeable;

/**
 * Interface provides support both reading and writing to a file/memory/etc.
 */
public interface IOStream extends Closeable
{
    /**
     * Reads up to {@code dst}. length bytes of data into an array of bytes. This method blocks until at least one byte of input is available.
     * @param dst the buffer into which the data is read.
     * @return the total number of bytes read into the buffer, or -1 if there is no more data because the end has been reached.
     * Throws:
     * IOException – If the first byte cannot be read for any reason other than end of stream, or if some other I/ O error occurs.
     * NullPointerException – If {@code dst} is null.
     */
    int read(byte[] dst);

    /**
     * Reads up to len bytes of data from this stream into an array of bytes. This method blocks until at least one byte of input is available.
     * @param dst the buffer into which the data is read.
     * @param offset the start offset in array dst at which the data is written.
     * @param count the maximum number of bytes read.
     * @return the total number of bytes read into the buffer, or -1 if there is no more data because the end of the stream has been reached.
     * Throws:
     * IOException – If some other I/ O error occurs.
     * NullPointerException – If dst is null.
     * IndexOutOfBoundsException – If offset is negative, count is negative, or len is greater than {@code dst.length - offset}
     */
    int read(byte[] dst, int offset, int count);

    /**
     * Writes {@code data.length} bytes from the specified byte array to this stream, starting at the current position.
     * @param data the data.
     * Throws:
     * IOException – if an I/ O error occurs.
     */
    void write(byte[] data);

    /**
     * <p>Writes count bytes from the specified byte array starting at offset {@code offset} to this file.</p>
     * Throws:
     * IOException – if an I/ O error occurs.
     * @param data the data.
     * @param offset the start offset in the data.
     * @param count the number of bytes to write.
     */
    void write(byte[] data, int offset, int count);

    /**
     * Sets the current stream position to {@code newPosition}.
     * @param newPosition the new stream position.
     * @return the old stream position.
     */
    long setPosition(long newPosition);

    /**
     * Gets the current stream position.
     * @return the current stream position.
     */
    long getPosition();

    /**
     * Sets the stream offset, measured from the given {@code mode}.
     * @param newPosition the new position started from begin/current/end position according to {@code mode}.
     * @param mode the seeking mode
     * @see IOSeekMode
     */
    void seek(long newPosition, IOSeekMode mode);

    /**
     * Returns the length of this file.
     * @return the length of this file, measured in bytes.
     */
    long getLength();

    /**
     * Sets the length of this stream.
     * @param newLength The desired length of the stream.
     */
    void setLength(long newLength);
}
