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

import java.util.Arrays;

public enum IntraPredMode
{
    INTRA_PLANAR, /*  = 0 */
    INTRA_DC, /*  = 1 */
    INTRA_ANGULAR2, /*  = 2 */
    INTRA_ANGULAR3, /*  = 3 */
    INTRA_ANGULAR4, /*  = 4 */
    INTRA_ANGULAR5, /*  = 5 */
    INTRA_ANGULAR6, /*  = 6 */
    INTRA_ANGULAR7, /*  = 7 */
    INTRA_ANGULAR8, /*  = 8 */
    INTRA_ANGULAR9, /*  = 9 */
    INTRA_ANGULAR10, /*  = 10 */
    INTRA_ANGULAR11, /*  = 11 */
    INTRA_ANGULAR12, /*  = 12 */
    INTRA_ANGULAR13, /*  = 13 */
    INTRA_ANGULAR14, /*  = 14 */
    INTRA_ANGULAR15, /*  = 15 */
    INTRA_ANGULAR16, /*  = 16 */
    INTRA_ANGULAR17, /*  = 17 */
    INTRA_ANGULAR18, /*  = 18 */
    INTRA_ANGULAR19, /*  = 19 */
    INTRA_ANGULAR20, /*  = 20 */
    INTRA_ANGULAR21, /*  = 21 */
    INTRA_ANGULAR22, /*  = 22 */
    INTRA_ANGULAR23, /*  = 23 */
    INTRA_ANGULAR24, /*  = 24 */
    INTRA_ANGULAR25, /*  = 25 */
    INTRA_ANGULAR26, /*  = 26 */
    INTRA_ANGULAR27, /*  = 27 */
    INTRA_ANGULAR28, /*  = 28 */
    INTRA_ANGULAR29, /*  = 29 */
    INTRA_ANGULAR30, /*  = 30 */
    INTRA_ANGULAR31, /*  = 31 */
    INTRA_ANGULAR32, /*  = 32 */
    INTRA_ANGULAR33, /*  = 33 */
    INTRA_ANGULAR34, /*  = 34 */
    INTRA_WEDGE, /*  = 35 */
    INTRA_CONTOUR, /*  = 36 */
    INTRA_SINGLE; /*  = 37 */

    static IntraPredMode[] createArray(int count)
    {
        IntraPredMode[] array = new IntraPredMode[count];
        Arrays.fill(array, IntraPredMode.INTRA_PLANAR);
        return array;
    }


    static IntraPredMode[][] createArray2D(int count1, int count2)
    {
        IntraPredMode[][] array = new IntraPredMode[count1][count2];
        for (IntraPredMode[] modes : array)
        {
            Arrays.fill(modes, INTRA_PLANAR);
        }
        return array;
    }

    public static IntraPredMode get(int id)
    {
        return _values[id];
    }

    private static final IntraPredMode[] _values = IntraPredMode.values();
}
