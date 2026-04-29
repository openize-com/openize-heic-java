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

public enum PredMode
{
    MODE_INTRA, /*  = 0 */
    MODE_INTER, /*  = 1 */
    MODE_SKIP; /*  = 2 */

    static PredMode[][] createArray2D(int count1, int count2)
    {
        PredMode[][] array = new PredMode[count1][count2];
        for (PredMode[] modes : array)
        {
            Arrays.fill(modes, MODE_INTRA);
        }
        return array;
    }
}
