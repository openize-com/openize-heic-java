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



enum inter_pred_idc
{
    PRED_L0, /*  = 1 */
    PRED_L1, /*  = 2 */
    PRED_BI, /*  = 3 */ // ( nPbW + nPbH ) != 12
}

