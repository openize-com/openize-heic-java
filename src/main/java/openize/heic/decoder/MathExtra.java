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



/**
 * <p>
 * Class used to create extended math functions.
 * </p>
 */
final class MathExtra
{
    static int clip3(int x, int y, int z)
    {
        if (z < x)
            return x;
        else if (z > y)
            return y;
        else
            return z;
    }

    static /*UInt32*/long clip3(/*UInt32*/long x, /*UInt32*/long y, /*UInt32*/long z)
    {
        if ((z & 0xFFFFFFFFL) < (x & 0xFFFFFFFFL))
            return x;
        else if ((z & 0xFFFFFFFFL) > (y & 0xFFFFFFFFL))
            return y;
        else
            return z;
    }
    static int clipBitDepth(int x, int bitDepth)
    {
        return clip3(0, (1 << bitDepth) - 1, x);
    }
    static /*UInt32*/long clipBitDepth(/*UInt32*/long x, int bitDepth)
    {
        return clip3(0, ((1L << bitDepth) - 1L) & 0xFFFFFFFFL, x);
    }

    static int ceilDiv(int a, int b)
    {
        int deleted = a / b;
        return deleted +
            (deleted > 0 && a % b != 0 ? 1 : 0);
    }

    static /*UInt32*/long ceilDiv(/*UInt32*/long a, /*UInt32*/long b)
    {
        return (((((((a & 0xFFFFFFFFL) + (b & 0xFFFFFFFFL)) & 0xFFFFFFFFL) - 1) & 0xFFFFFFFFL) / (b & 0xFFFFFFFFL)) & 0xFFFFFFFFL);
    }
}

