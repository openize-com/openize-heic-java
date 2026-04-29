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

import java.util.Arrays;


class NeighbouringSamples extends NeighbouringSamplesTemplate
{
    private final short[] data;

    public NeighbouringSamples(int nTbS)
    {
        super(nTbS);
        data = new short[4 * nTbS + 1];
    }

    public final int get(int x, int y)
    {
        return data[getIndex(x, y)];
    }

    public final void set(int x, int y, int value)
    {
        data[getIndex(x, y)] = (short) value;
    }

    public final void reset(int bitDepth)
    {
        int value = 1 << (bitDepth - 1);
        Arrays.fill(data, (short)(value & 0xFFFF));
    }
}

