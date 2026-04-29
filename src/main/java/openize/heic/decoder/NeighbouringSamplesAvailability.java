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


import java.util.BitSet;

class NeighbouringSamplesAvailability extends NeighbouringSamplesTemplate
{
    private final BitSet data;
    public NeighbouringSamplesAvailability(int nTbS)
    {
        super(nTbS);
        data = new BitSet(4 * nTbS + 1);
    }

    public final boolean get(int x, int y)
    {
        return data.get(getIndex(x, y));
    }

    public final void set(int x, int y, boolean value)
    {
        data.set(getIndex(x, y), value);
    }
}
