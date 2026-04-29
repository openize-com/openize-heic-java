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


enum IntraChromaPredMode
{
    INTRA_CHROMA_PLANAR_OR_34, /*  = 0 */
    INTRA_CHROMA_ANGULAR26_OR_34, /*  = 1 */
    INTRA_CHROMA_ANGULAR10_OR_34, /*  = 2 */
    INTRA_CHROMA_DC_OR_34, /*  = 3 */
    INTRA_CHROMA_LIKE_LUMA, /*  = 4 */
}

