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


/**
 * <p>
 * Type of auxiliary reference layer.
 * </p>
 */
public enum AuxiliaryReferenceType
{
    /**
     * <p>
     * Transparency layer.
     * Defined as "urn:mpeg:hevc:2015:auxid:1".
     * </p>
     */
    Alpha,

    /**
     * <p>
     * Depth map layer.
     * Defined as "urn:mpeg:hevc:2015:auxid:2".
     * </p>
     */
    DepthMap,

    /**
     * <p>
     * High dynamic range layer.
     * Defined as "urn:com:apple:photo:2020:aux:hdrgainmap".
     * </p>
     */
    HdrGainMap,

    /**
     * <p>
     * Layer that represents the portrait effects matte of the image.
     * Defined as "urn:com:apple:photo:2018:aux:portraiteffectsmatte".
     * </p>
     */
    PortraitEffectsMatte,

    /**
     * <p>
     * Layer that represents the semantic segmentation hair matte of the image.
     * Defined as "urn:com:apple:photo:2019:aux:semantichairmatte".
     * </p>
     */
    SemanticHairMatte,

    /**
     * <p>
     * Layer that represents the semantic segmentation skin matte of the image.
     * Defined as "urn:com:apple:photo:2019:aux:semanticskinmatte".
     * </p>
     */
    SemanticSkinMatte,

    /**
     * <p>
     * Layer that represents the semantic segmentation teeth matte of the image.
     * Defined as "urn:com:apple:photo:2019:aux:semanticteethmatte".
     * </p>
     */
    SemanticTeethMatte,

    /**
     * <p>
     * Layer that represents the semantic segmentation glasses matte of the image.
     * Defined as "urn:com:apple:photo:2020:aux:semanticglassesmatte".
     * </p>
     */
    SemanticGlassesMatte,

    /**
     * <p>
     * Layer that represents the semantic segmentation sky matte of the image.
     * Defined as "urn:com:apple:photo:2020:aux:semanticskymatte".
     * </p>
     */
    SemanticSkyMatte,

    /**
     * <p>
     * Defined as "tag:apple.com,2023:photo:aux:linearthumbnail".
     * </p>
     */
    LinearThumbnail,

    /**
     * <p>
     * Defined as "tag:apple.com,2023:photo:aux:styledeltamap".
     * </p>
     */
    StyleDeltaMap,

    /**
     * <p>
     * Undefined layer.
     * </p>
     */
    Undefined,
}
