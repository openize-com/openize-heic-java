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

import openize.isobmff.io.BitStreamReader;


/**
 * <p>
 * If the decoding of a multi-layer image item results into more than one reconstructed image, the 'lsel'
 * item property shall be associated with the image item.Otherwise, the 'lsel' item property shall not
 * be associated with an image item.
 * </p>
 * <p>This property is used to select which of the reconstructed images is described by subsequent
 * descriptive item properties in the item property association order; and manipulated by transformative
 * item properties, if any, to generate an output image of the image item.
 * </p>
 */
public class LayerSelectorProperty extends Box
{
    /**
     * <p>
     * Specifies the layer identifier of the image among the reconstructed images that is described
     * by subsequent descriptive item properties in the item property association order and manipulated by
     * transformative item properties, if any, to generate an output image of the image item. The semantics of
     * layer_id are specific to the coding format and are therefore defined for each coding format for which
     * the decoding of a multi-layer image item can result into more than one reconstructed images.
     * </p>
     */
    public final /*UInt16*/ int layer_id;

    /**
     * <p>
     * Create the box object from the bitstream and box size.
     * </p>
     *
     * @param stream File stream.
     * @param size   Box size in bytes.
     */
    public LayerSelectorProperty(BitStreamReader stream, /*UInt64*/long size)
    {
        super(BoxType.lsel, size);

        layer_id = stream.read(16) & 0xFFFF;
    }

    /**
     * <p>
     * Text summary of the box.
     * </p>
     */
    @Override
    public String toString()
    {
        return String.format("%s layer: %d", this.type, layer_id);
    }
}
