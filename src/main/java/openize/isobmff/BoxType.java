/*
 * FileFormat.IsoBmff
 * Copyright (c) 2024-2025 Openize Pty Ltd.
 *
 * This file is part of FileFormat.IsoBmff.
 *
 * FileFormat.IsoBmff is available under MIT license, which is
 * available along with FileFormat.IsoBmff sources.
 */

package openize.isobmff;

/**
 * <p>
 * Type of the container box.
 * Used definitions from ISO IEC 14496 Part 12, ISO IEC 14496 Part 15 and ISO IEC 23008 Part 12.
 * </p>
 */
@SuppressWarnings({"SpellCheckingInspection", "unused"})
public enum BoxType
{
    /**
     * <p>
     * Unknown type.
     * </p>
     */
    unknown(0),

    /**
     * <p>
     * File type and compatibility. Located in the root.
     * </p>
     */
    ftyp(0x66747970),

    /**
     * <p>
     * Media data container. Located in the root.
     * </p>
     */
    mdat(0x6d646174),

    /**
     * <p>
     * Free space.
     * </p>
     */
    free(0x66726565),

    /**
     * <p>
     * Free space.
     * </p>
     */
    skip(0x736b69700L),

    /**
     * <p>
     * Container for all the movie metadata. Located in the root.
     * </p>
     */
    moov(0x6d6f6f76L),

    /**
     * <p>
     * Metadata. Located in the root.
     * </p>
     */
    meta(0x6d657461L),

    /**
     * <p>
     * Handler, declares the metadata (handler) type. Located in the 'meta' box.
     * </p>
     */
    hdlr(0x68646c72L),

    /**
     * <p>
     * Data information box, container. Located in the 'meta' box.
     * </p>
     */
    dinf(0x64696e66L),

    /**
     * <p>
     * Data reference box, declares source(s) of items. Located in the 'dinf' box.
     * </p>
     */
    dref(0x64726566L),

    /**
     * <p>
     * Data reference box, declares source(s) of metadata items.
     * </p>
     */
    url(0x75726c20L),

    /**
     * <p>
     * Data reference box, declares source(s) of metadata items.
     * </p>
     */
    urn(0x75726e20L),

    /**
     * <p>
     * Item location. Located in the 'meta' box.
     * </p>
     */
    iloc(0x696c6f63L),

    /**
     * <p>
     * Item data. Located in the 'meta' box.
     * </p>
     */
    idat(0x69646174L),

    /**
     * <p>
     * Item information. Located in the 'meta' box.
     * </p>
     */
    iinf(0x69696e66L),

    /**
     * <p>
     * Item information entry. Located in the 'iinf' box.
     * </p>
     */
    infe(0x696e6665L),

    /**
     * <p>
     * Item reference box. Located in the 'meta' box.
     * </p>
     */
    iref(0x69726566L),

    /**
     * <p>
     * Derived image item. Located in the 'iref' box.
     * </p>
     */
    dimg(0x64696d67L),

    /**
     * <p>
     * Auxiliary media for the indicated track (e.g. depth map or alpha plane for video). Located in the 'iref' box.
     * </p>
     */
    auxl(0x6175786cL),

    /**
     * <p>
     * Thumbnails for the referenced track. Located in the 'iref' box.
     * </p>
     */
    thmb(0x74686d62L),

    /**
     * <p>
     * Primary item reference. Located in the 'meta' box.
     * </p>
     */
    pitm(0x7069746dL),

    /**
     * <p>
     * Item properties. Located in the 'meta' box.
     * </p>
     */
    iprp(0x69707270L),

    /**
     * <p>
     * Item property association. Located in the 'iprp' box.
     * </p>
     */
    ipma(0x69706d61L),

    /**
     * <p>
     * List of item properties. Located in the 'iprp' box.
     * </p>
     */
    ipco(0x6970636fL),

    /**
     * <p>
     * AV1 coded item property. Located in the 'ipco' box.
     * </p>
     */
    av1C(0x61763143L),

    /**
     * <p>
     * HEVC coded item property. Located in the 'ipco' box.
     * </p>
     */
    hvcC(0x68766343L),

    /**
     * <p>
     * Width and height item property. Located in the 'ipco' box.
     * </p>
     */
    ispe(0x69737065L),

    /**
     * <p>
     * Pixel aspect ratio item property. Located in the 'ipco' box.
     * </p>
     */
    pasp(0x70617370L),

    /**
     * <p>
     * Colour information item property. Located in the 'ipco' box.
     * </p>
     */
    colr(0x636f6c72L),

    /**
     * <p>
     * Content light level info. Located in the 'clli' box.
     * </p>
     */
    clli(0x636c6c69),

    /**
     * <p>
     * Bit depth item property. Located in the 'ipco' box.
     * </p>
     */
    pixi(0x70697869L),

    /**
     * <p>
     * Position item property. Located in the 'ipco' box.
     * </p>
     */
    rloc(0x726c6f63L),

    /**
     * <p>
     * Auxiliary images association item property. Located in the 'ipco' box.
     * </p>
     */
    auxC(0x61757843L),

    /**
     * <p>
     * Transformation item property. Located in the 'ipco' box.
     * </p>
     */
    clap(0x636c6170L),

    /**
     * <p>
     * Rotation item property. Located in the 'ipco' box.
     * </p>
     */
    irot(0x69726f74L),

    /**
     * <p>
     * Multi-layer item property. Located in the 'ipco' box.
     * </p>
     */
    lsel(0x6c73656cL),

    /**
     * <p>
     * Mirroring item property. Located in the 'ipco' box.
     * </p>
     */
    imir(0x696d6972L),

    /**
     * <p>
     * Operation points information item property. Located in the 'ipco' box.
     * </p>
     */
    oinf(0x6f696e66L),

    /**
     * <p>
     * User description item property. Located in the 'ipco' box.
     * </p>
     */
    udes(0x75646573L),

    /**
     * <p>
     * Content describtion item property. Located in the 'ipco' box.
     * </p>
     */
    cdsc(0x63647363L),

    /**
     * <p>
     * Grouping property box. Located in the 'meta' box.
     * </p>
     */
    grpl(0x6772706cL),

    /**
     * <p>
     * Alternatives entity group. Located in the 'grpl' box.
     * </p>
     */
    altr(0x616c7472L),

    /**
     * <p>
     * Stereo pair entity group. Located in the 'grpl' box.
     * </p>
     */
    ster(0x73746572L),

    /**
     * <p>
     * A time-synchronized capture entity group. Located in the 'grpl' box.
     * </p>
     */
    tsyn(0x7473796eL),

    /**
     * <p>
     * Item protection. Located in the root.
     * </p>
     */
    ipro(0x6970726fL),

    /**
     * <p>
     * Protection scheme information box. Located in the 'ipro' box.
     * </p>
     */
    sinf(0x73696e66L),

    /**
     * <p>
     * Original format box. Located in the 'sinf' box.
     * </p>
     */
    frma(0x66726d61L),

    /**
     * <p>
     * Scheme type box. Located in the 'sinf' box.
     * </p>
     */
    schm(0x7363686dL),

    /**
     * <p>
     * Scheme information box. Located in the 'sinf' box.
     * </p>
     */
    schi(0x73636869L),

    /**
     * <p>
     * Extended type.
     * </p>
     */
    uuid(0x75756964);

    /**
     * <p>Cached values</p>
     */
    private static final BoxType[] _values = BoxType.values();

    /**
     * <p>
     * The code of the box type.
     * </p>
     */
    private final long code;

    /**
     * <p>
     * The box type.
     * </p>
     *
     * @param code The code of the box type.
     */
    BoxType(long code)
    {
        this.code = code;
    }

    /**
     * <p>
     *     Returns the BoxType with the given {@code code} or {@link BoxType#unknown}.
     * </p>
     * @param code The code of the box type.
     * @return The BoxType value.
     */
    public static BoxType codeToType(long code)
    {
        code &= 0xFFFFFFFFL;
        for (BoxType type : _values)
        {
            if (type.getCode() == code)
            {
                return type;
            }
        }

        return unknown;
    }

    /**
     * <p>
     * Returns the code of type.
     * </p>
     *
     * @return The code of type.
     */
    public long getCode()
    {
        return code;
    }
}
