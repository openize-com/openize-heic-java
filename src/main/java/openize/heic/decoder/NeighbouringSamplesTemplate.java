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

class NeighbouringSamplesTemplate
{
    protected final int size;

    public NeighbouringSamplesTemplate(int nTbS)
    {
        size = nTbS;
    }

    protected final int getIndex(int x, int y)
    {
        if (y == -1)
        {
            return x + 1;
        }
        else
        {
            return 2 * size + y + 1;
        }
    }
}

