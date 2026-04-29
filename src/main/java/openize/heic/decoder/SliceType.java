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



enum SliceType /*: byte*/
{
    B, /*  = 0 */
    P, /*  = 1 */
    I; /*  = 2 */

    public static SliceType get(long code)
    {
        switch ((int)code)
        {
            case 0:
                return B;
            case 1:
                return P;
            case 2:
                return I;
            default:
                throw new IllegalArgumentException(String.valueOf(code));
        }
    }
}
