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

import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.List;


public class slice_segment_data
{
    //private final List<coding_tree_unit> CTUs = new ArrayList<>(15);

    public slice_segment_data(
            BitStreamWithNalSupport stream,
            slice_segment_header header)
    {
        boolean end_of_slice_segment_flag;
        seq_parameter_set_rbsp sps = header.pps.sps;
        HeicPicture picture = header.parentPicture;

        if (picture.SliceAddrRs == null)
        {
            picture.SliceAddrRs = new /*UInt32*/int[(int) (sps.getPicWidthInCtbsY())][(int) (sps.getPicHeightInCtbsY())];
        }

        if (picture.SliceHeaderIndex == null)
        {
            picture.SliceHeaderIndex = new int[(int) (((sps.getPicWidthInCtbsY() & 0xFFFFFFFFL) << ((sps.getCtbLog2SizeY() & 0xFF) & 0x1F)) & 0xFFFFFFFFL)][(int) (((sps.getPicHeightInCtbsY() & 0xFFFFFFFFL) << ((sps.getCtbLog2SizeY() & 0xFF) & 0x1F)) & 0xFFFFFFFFL)];
        }

        do
        {
            int xCtu = (int) ((header.getCtbAddrInRs() & 0xFFFFFFFFL) % (sps.getPicWidthInCtbsY() & 0xFFFFFFFFL));
            int yCtu = (int) ((header.getCtbAddrInRs() & 0xFFFFFFFFL) / (sps.getPicWidthInCtbsY() & 0xFFFFFFFFL));

            if (header.pps.entropy_coding_sync_enabled_flag &&
                    xCtu == 0 && yCtu > 0)
            {
                stream.getCabac().restoreSyncedTables();
            }

            coding_tree_unit ctu = new coding_tree_unit(stream, header);
            //CTUs.add(ctu);

            if (header.pps.entropy_coding_sync_enabled_flag &&
                    xCtu == 1 &&
                    yCtu < (((sps.getPicHeightInCtbsY() & 0xFFFFFFFFL) - 1) & 0xFFFFFFFFL))
            {
                stream.getCabac().syncTables();
            }

            end_of_slice_segment_flag = stream.getCabac().read_end_of_slice_segment_flag();

            header.setCtbAddrInTs(header.getCtbAddrInTs() + 1)/*Property++*/;
            if ((header.getCtbAddrInTs() & 0xFFFFFFFFL) < (sps.getPicSizeInCtbsY() & 0xFFFFFFFFL))
            {
                header.setCtbAddrInRs(header.pps.CtbAddrTsToRs[(int) (header.getCtbAddrInTs())]);
            }

            if (header.getCtbAddrInTs() == sps.getPicSizeInCtbsY())
            {
                return;
            }

            if (!end_of_slice_segment_flag &&

                    ((header.pps.tiles_enabled_flag &&
                            (header.pps.TileId[(int) (header.getCtbAddrInTs())] & 0xFFFFFFFFL) != (header.pps.TileId[(int) (((header.getCtbAddrInTs() & 0xFFFFFFFFL) - 1) & 0xFFFFFFFFL)] & 0xFFFFFFFFL)) ||

                            (header.pps.entropy_coding_sync_enabled_flag &&
                                    ((((header.getCtbAddrInRs() & 0xFFFFFFFFL) % (sps.getPicWidthInCtbsY() & 0xFFFFFFFFL)) & 0xFFFFFFFFL) == 0 ||
                                            (header.pps.TileId[(int) (header.getCtbAddrInTs())] & 0xFFFFFFFFL) != (header.pps.TileId[(int) (header.pps.CtbAddrRsToTs[(int) (((header.getCtbAddrInRs() & 0xFFFFFFFFL) - 1) & 0xFFFFFFFFL)])] & 0xFFFFFFFFL)))))
            {
                int one = stream.getCabac().read_end_of_subset_one_bit(); /* equal to 1; */

                if (one != 1)
                    throw new IllegalStateException("Unexpected slice-end bit!");

                while (stream.notByteAligned())
                    stream.skipBits(1);

                stream.getCabac().initialization(header);
            }

        } while (!end_of_slice_segment_flag);
    }
}

