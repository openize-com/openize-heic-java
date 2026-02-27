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

import openize.heic.decoder.io.BitStreamWithNalSupport;


public class slice_segment_layer_rbsp extends NalUnit
{
    public final slice_segment_header slice_header;
    public slice_segment_data data;

    public slice_segment_layer_rbsp(BitStreamWithNalSupport stream, /*UInt64*/long startPosition, int size)
    {
        super(stream, startPosition, size);

        slice_header = new slice_segment_header(stream, this);

        stream.getCabac().initialization(slice_header);

        if (slice_header.first_slice_segment_in_pic_flag)
        {
            HeicPicture picture = new HeicPicture(slice_header, NalHeader.copy());
            stream.getContext().getPictures().put(stream.getCurrentImageId(), picture);
            slice_header.parentPicture = picture;

            if (NalHeader.isIrapPicture())
            {
                if (NalHeader.isIdrPicture() ||
                        NalHeader.isBlaPicture() ||
                        //first_decoded_picture ||
                        stream.getContext().FirstAfterEndOfSequenceNAL)
                {
                    stream.getContext().NoRaslOutputFlag = true;
                    stream.getContext().FirstAfterEndOfSequenceNAL = false;
                }
                else
                {
                    stream.getContext().NoRaslOutputFlag = false;
                    stream.getContext().HandleCraAsBlaFlag = false;
                }
            }
        }

        if (stream.getContext().getPictures().isEmpty())
        {
            throw new IndexOutOfBoundsException();
        }

        HeicPicture currentPicture = stream.getContext().getPictures().get(stream.getCurrentImageId());

        if (slice_header.parentPicture == null)
        {
            slice_header.parentPicture = currentPicture;
        }

        slice_header.slice_index = currentPicture.getSliceHeaders().size();
        currentPicture.getSliceHeaders().put(slice_header.slice_index, slice_header);
        Scaling.initiate(currentPicture.sps);

        stream.getContext().decodingPictureOrderCount(currentPicture);

        /*data =*/ new slice_segment_data(stream, slice_header);
    }
}
