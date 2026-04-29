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

import java.io.*;
import java.nio.file.Path;

public class IOFileStream implements IOStream
{
    private final RandomAccessFile file;

    /**
     * <p>
     *     Open/Create a file with an {@code mode} access.
     * </p>
     * @param path The file path to open/create.
     * @param mode The file access mode.
     * @throws IOException File does not exist.
     */
    public IOFileStream(String path, IOMode mode)
    {
        try
        {
            this.file = new RandomAccessFile(path, mode.getMode());
        }
        catch (FileNotFoundException e)
        {
            throw new IOException(e.getMessage(), e);
        }
    }

    /**
     * <p>
     *     Open/Create a file with an {@code mode} access.
     * </p>
     * @param file The file path to open/create.
     * @param mode The file access mode.
     * @throws IOException File does not exist.
     */
    public IOFileStream(File file, IOMode mode)
    {
        try
        {
            this.file = new RandomAccessFile(file, mode.getMode());
        }
        catch (FileNotFoundException e)
        {
            throw new IOException(e.getMessage(), e);
        }
    }

    /**
     * <p>
     *     Open/Create a file with an {@code mode} access.
     * </p>
     * @param path The file path to open/create.
     * @param mode The file access mode.
     * @throws IOException File does not exist.
     */
    public IOFileStream(Path path, IOMode mode)
    {
        try
        {
            this.file = new RandomAccessFile(path.toFile(), mode.getMode());
        }
        catch (FileNotFoundException e)
        {
            throw new IOException(e.getMessage(), e);
        }
    }

    /**
     * <p>
     *     Create an instance of IOStream over RandomAccessFile.
     * </p>
     * @param file The file.
     */
    public IOFileStream(RandomAccessFile file)
    {
        this.file = file;
    }

    @Override
    public int read(byte[] dst)
    {
        if (dst.length == 0)
        {
            return 0;
        }

        try
        {
            return this.file.read(dst);
        }
        catch (java.io.IOException e)
        {
            throw new IOException(e.getMessage(), e);
        }
    }

    @Override
    public int read(byte[] dst, int offset, int count)
    {
        if (count == 0)
        {
            return 0;
        }
        try
        {
            return this.file.read(dst, offset, count);
        }
        catch (java.io.IOException e)
        {
            throw new IOException(e.getMessage(), e);
        }
    }

    @Override
    public void write(byte[] data)
    {
        if (data.length == 0)
        {
            return;
        }
        try
        {
            this.file.write(data);
        }
        catch (java.io.IOException e)
        {
            throw new IOException(e.getMessage(), e);
        }
    }

    @Override
    public void write(byte[] data, int offset, int count)
    {
        try
        {
            this.file.write(data, offset, count);
        }
        catch (java.io.IOException e)
        {
            throw new IOException(e.getMessage(), e);
        }
    }

    @Override
    public long setPosition(long newPosition)
    {
        try
        {
            final long oldPosition = file.getFilePointer();
            if (oldPosition != newPosition)
            {
                file.seek(newPosition);
            }
            return oldPosition;
        }
        catch (java.io.IOException e)
        {
            throw new IOException(e.getMessage(), e);
        }
    }

    @Override
    public long getPosition()
    {
        try
        {
            return file.getFilePointer();
        }
        catch (java.io.IOException e)
        {
            throw new IOException(e.getMessage(), e);
        }
    }

    @Override
    public long getLength()
    {
        try
        {
            return file.length();
        }
        catch (java.io.IOException e)
        {
            throw new IOException(e.getMessage(), e);
        }
    }

    @Override
    public void setLength(long newLength)
    {
        try
        {
            if (file.length() != newLength)
            {
                file.setLength(newLength);
            }
        }
        catch (java.io.IOException e)
        {
            throw new IOException(e.getMessage(), e);
        }
    }

    @Override
    public void close() throws IOException
    {
        try
        {
            this.file.close();
        }
        catch (java.io.IOException e)
        {
            throw new IOException(e.getMessage(), e);
        }
    }

    @Override
    public void seek(long newPosition, IOSeekMode mode)
    {
        try
        {
            switch (mode)
            {
                case BEGIN:
                    if (file.getFilePointer() != newPosition)
                    {
                        file.seek(newPosition);
                    }
                    break;
                case CURRENT:
                    if (newPosition == 0)
                    {
                        return;
                    }
                    file.seek(file.getFilePointer() + newPosition);
                    break;
                case END:
                    final long pos = file.length() + newPosition;
                    if (pos != file.getFilePointer())
                    {
                        file.seek(pos);
                    }
                    break;
            }
        }
        catch (java.io.IOException e)
        {
            throw new IOException(e.getMessage(), e);
        }
    }
}
