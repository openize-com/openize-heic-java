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

public class Rectangle
{
    private int x;
    private int y;
    private int width;
    private int height;

    public Rectangle()
    {
    }

    public Rectangle(int x, int y, int width, int height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getLeft()
    {
        return x;
    }

    public int getTop()
    {
        return y;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public int getRight()
    {
        return x + width;
    }

    public int getBottom()
    {
        return y + height;
    }

    public boolean isEmpty()
    {
        return width <= 0 && height <= 0;
    }
}
