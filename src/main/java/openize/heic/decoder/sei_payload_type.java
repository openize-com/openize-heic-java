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

enum sei_payload_type
{
    buffering_period, /*  = 0 */
    pic_timing, /*  = 1 */
    pan_scan_rect, /*  = 2 */
    filler_payload, /*  = 3 */
    user_data_registered_itu_t_t35, /*  = 4 */
    user_data_unregistered, /*  = 5 */
    recovery_point, /*  = 6 */
    scene_info, /*  = 9 */
    picture_snapshot, /*  = 15 */
    progressive_refinement_segment_start, /*  = 16 */
    progressive_refinement_segment_end, /*  = 17 */
    film_grain_characteristics, /*  = 19 */
    post_filter_hint, /*  = 22 */
    tone_mapping_info, /*  = 23 */
    frame_packing_arrangement, /*  = 45 */
    display_orientation, /*  = 47 */
    green_metadata, /*  = 56 */                            // specified in ISO/IEC 23001-11
    structure_of_pictures_info, /*  = 128 */
    active_parameter_sets, /*  = 129 */
    decoding_unit_info, /*  = 130 */
    temporal_sub_layer_zero_idx, /*  = 131 */
    decoded_picture_hash, /*  = 132 */
    scalable_nesting, /*  = 133 */
    region_refresh_info, /*  = 134 */
    no_display, /*  = 135 */
    time_code, /*  = 136 */
    mastering_display_colour_volume, /*  = 137 */
    segmented_rect_frame_packing_arrangement, /*  = 138 */
    temporal_motion_constrained_tile_sets, /*  = 139 */
    chroma_resampling_filter_hint, /*  = 140 */
    knee_function_info, /*  = 141 */
    colour_remapping_info, /*  = 142 */
    deinterlaced_field_identification, /*  = 143 */
    content_light_level_info, /*  = 144 */
    dependent_rap_indication, /*  = 145 */
    coded_region_completion, /*  = 146 */
    alternative_transfer_characteristics, /*  = 147 */
    ambient_viewing_environment, /*  = 148 */
    content_colour_volume, /*  = 149 */
    equirectangular_projection, /*  = 150 */
    cubemap_projection, /*  = 151 */
    fisheye_video_info, /*  = 152 */
    sphere_rotation, /*  = 154 */
    regionwise_packing, /*  = 155 */
    omni_viewport, /*  = 156 */
    regional_nesting, /*  = 157 */
    mcts_extraction_info_sets, /*  = 158 */
    mcts_extraction_info_nesting, /*  = 159 */
    layers_not_present, /*  = 160 */                       // specified in Annex F
    inter_layer_constrained_tile_sets, /*  = 161 */        // specified in Annex F
    bsp_nesting, /*  = 162 */                              // specified in Annex F
    bsp_initial_arrival_time, /*  = 163 */                 // specified in Annex F
    sub_bitstream_property, /*  = 164 */                   // specified in Annex F
    alpha_channel_info, /*  = 165 */                       // specified in Annex F
    overlay_info, /*  = 166 */                             // specified in Annex F
    temporal_mv_prediction_constraints, /*  = 167 */       // specified in Annex F
    frame_field_info, /*  = 168 */                         // specified in Annex F
    three_dimensional_reference_displays_info, /*  = 176 */// specified in Annex G
    depth_representation_info, /*  = 177 */                // specified in Annex G
    multiview_scene_info, /*  = 178 */                     // specified in Annex G
    multiview_acquisition_info, /*  = 179 */               // specified in Annex G
    multiview_view_position, /*  = 180 */                  // specified in Annex G
    alternative_depth_info, /*  = 181 */                   // specified in Annex I
    sei_manifest, /*  = 200 */
    sei_prefix_indication, /*  = 201 */
    annotated_regions, /*  = 202 */
    shutter_interval_info, /*  = 205 */
}

