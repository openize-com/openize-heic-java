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

import openize.heic.decoder.io.BitStreamWithNalSupport;



class sps_range_extension
{
    final boolean transform_skip_rotation_enabled_flag;
    final boolean transform_skip_context_enabled_flag;
    final boolean implicit_rdpcm_enabled_flag;
    final boolean explicit_rdpcm_enabled_flag;
    final boolean extended_precision_processing_flag;
    final boolean intra_smoothing_disabled_flag;
    final boolean high_precision_offsets_enabled_flag;
    final boolean persistent_rice_adaptation_enabled_flag;
    final boolean cabac_bypass_alignment_enabled_flag;

    public sps_range_extension(BitStreamWithNalSupport stream)
    {
        transform_skip_rotation_enabled_flag = stream.readFlag();    // u(1)
        transform_skip_context_enabled_flag = stream.readFlag();     // u(1)
        implicit_rdpcm_enabled_flag = stream.readFlag();             // u(1)
        explicit_rdpcm_enabled_flag = stream.readFlag();             // u(1)
        extended_precision_processing_flag = stream.readFlag();      // u(1)
        intra_smoothing_disabled_flag = stream.readFlag();           // u(1)
        high_precision_offsets_enabled_flag = stream.readFlag();     // u(1)
        persistent_rice_adaptation_enabled_flag = stream.readFlag(); // u(1)
        cabac_bypass_alignment_enabled_flag = stream.readFlag();     // u(1)
    }
    public sps_range_extension()
    {
        transform_skip_rotation_enabled_flag = false;
        transform_skip_context_enabled_flag = false;
        implicit_rdpcm_enabled_flag = false;
        explicit_rdpcm_enabled_flag = false;
        extended_precision_processing_flag = false;
        intra_smoothing_disabled_flag = false;
        high_precision_offsets_enabled_flag = false;
        persistent_rice_adaptation_enabled_flag = false;
        cabac_bypass_alignment_enabled_flag = false;
    }
}

