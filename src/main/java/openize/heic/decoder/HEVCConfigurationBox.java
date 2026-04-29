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
import openize.isobmff.Box;
import openize.isobmff.BoxType;
import openize.isobmff.ObservableCollection;


class HEVCConfigurationBox extends Box
{
    public HEVCDecoderConfigurationRecord record;
    public final /*UInt64*/ long offset;
    private ObservableCollection<HEVCDecoderConfigurationRecord> children;

    public HEVCConfigurationBox(BitStreamWithNalSupport stream, /*UInt64*/long size)
    {
        super(BoxType.hvcC, size);

        offset = stream.getBitPosition() / 8;
        setChildren(new ObservableCollection<>());
    }

    @Override
    public final String toString()
    {
        return type.name();
    }

    public final ObservableCollection<HEVCDecoderConfigurationRecord> getChildren()
    {
        return children;
    }

    public final void setChildren(ObservableCollection<HEVCDecoderConfigurationRecord> value)
    {
        children = value;
    }
}

