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


public class pic_parameter_set_rbsp extends NalUnit
{
    public final seq_parameter_set_rbsp sps;
    final byte pps_pic_parameter_set_id; // max - 64
    final byte pps_seq_parameter_set_id;
    final boolean dependent_slice_segments_enabled_flag;
    final boolean output_flag_present_flag;
    final byte num_extra_slice_header_bits;
    final boolean sign_data_hiding_enabled_flag;
    final boolean cabac_init_present_flag;
    final /*UInt32*/ long num_ref_idx_l0_default_active_minus1;
    final /*UInt32*/ long num_ref_idx_l1_default_active_minus1;
    final int init_qp_minus26;
    final boolean constrained_intra_pred_flag;
    final boolean transform_skip_enabled_flag;
    final boolean cu_qp_delta_enabled_flag;
    final int pps_cb_qp_offset;
    final int pps_cr_qp_offset;
    final boolean pps_slice_chroma_qp_offsets_present_flag;
    final boolean weighted_pred_flag;
    final boolean weighted_bipred_flag;
    final boolean transquant_bypass_enabled_flag;
    final boolean tiles_enabled_flag;
    final boolean entropy_coding_sync_enabled_flag;
    final boolean pps_loop_filter_across_slices_enabled_flag;
    final boolean deblocking_filter_control_present_flag;
    final boolean pps_scaling_list_data_present_flag;
    final boolean lists_modification_present_flag;
    final /*UInt32*/ long log2_parallel_merge_level_minus2;
    final boolean slice_segment_header_extension_present_flag;
    final boolean pps_extension_present_flag;
    final pps_scc_extension pps_scc_ext;
    //pps_3d_extension pps_3d_ext;
    //pps_multilayer_extension pps_multilayer_ext;
    final pps_range_extension pps_range_ext;
    // the width of the i-th tile column in units of CTBs
    public /*UInt32*/ long[] colWidth;
    // the height of the j-th tile row in units of CTBs
    public /*UInt32*/ long[] rowHeight;
    // the location of the i-th tile column boundary in units of CTBs
    public /*UInt32*/ long[] colBd;
    // the location of the j-th tile row boundary in units of CTBs
    public /*UInt32*/ long[] rowBd;
    // the conversion from a CTB address in CTB raster scan of a picture to a CTB address in tile scan
    public /*UInt32*/ long[] CtbAddrRsToTs;
    // the conversion from a CTB address in tile scan to a CTB address in CTB raster scan of a picture
    public /*UInt32*/ long[] CtbAddrTsToRs;
    // the conversion from a CTB address in tile scan to a tile ID
    public /*UInt32*/ long[] TileId;
    // the conversion from a CTB address in CTB raster scan to a tile ID
    public /*UInt32*/ long[] TileIdFromRs;
    // the width of the i-th tile column in units of luma samples
//    public /*UInt32*/ long[] ColumnWidthInLumaSamples;
    // the height of the j-th tile row in units of luma samples
    //  public /*UInt32*/ long[] RowHeightInLumaSamples;
    // the conversion from a location (x, y) in units of minimum transform blocks
    // to a transform block address in z-scan order
    public /*UInt32*/ int[][] MinTbAddrZs;
    /*UInt32*/ long diff_cu_qp_delta_depth;
    /*UInt32*/ long num_tile_columns_minus1;
    /*UInt32*/ long num_tile_rows_minus1;
    boolean uniform_spacing_flag;
    /*UInt32*/ long[] column_width_minus1;
    /*UInt32*/ long[] row_height_minus1;
    boolean loop_filter_across_tiles_enabled_flag;
    boolean deblocking_filter_override_enabled_flag;
    boolean pps_deblocking_filter_disabled_flag;
    int pps_beta_offset_div2;
    int pps_tc_offset_div2;
    boolean pps_range_extension_flag;
    boolean pps_multilayer_extension_flag;
    boolean pps_3d_extension_flag;
    boolean pps_scc_extension_flag;
    byte pps_extension_4bits;
    boolean pps_extension_data_flag;

    public pic_parameter_set_rbsp(BitStreamWithNalSupport stream, /*UInt64*/long startPosition, int size)
    {
        super(stream, startPosition, size);

        pps_pic_parameter_set_id = (byte) stream.readUev();                 // ue(v)
        pps_seq_parameter_set_id = (byte) stream.readUev();                 // ue(v)

        sps = stream.getContext()
                    .getSPS()
                    .get(pps_seq_parameter_set_id & 0xFF);

        dependent_slice_segments_enabled_flag = stream.readFlag();          // u(1)
        output_flag_present_flag = stream.readFlag();                       // u(1)
        num_extra_slice_header_bits = (byte) stream.read(3);                 // u(3)
        sign_data_hiding_enabled_flag = stream.readFlag();                  // u(1)
        cabac_init_present_flag = stream.readFlag();                        // u(1)

        num_ref_idx_l0_default_active_minus1 = stream.readUev();           // ue(v)
        num_ref_idx_l1_default_active_minus1 = stream.readUev();           // ue(v)
        init_qp_minus26 = stream.readSev();                                // se(v)
        constrained_intra_pred_flag = stream.readFlag();                    // u(1)
        transform_skip_enabled_flag = stream.readFlag();                    // u(1)
        cu_qp_delta_enabled_flag = stream.readFlag();                       // u(1)
        if (cu_qp_delta_enabled_flag)
        {
            diff_cu_qp_delta_depth = stream.readUev();                     // ue(v)
        }
        pps_cb_qp_offset = stream.readSev();                               // se(v)
        pps_cr_qp_offset = stream.readSev();                               // se(v)
        pps_slice_chroma_qp_offsets_present_flag = stream.readFlag();       // u(1)
        weighted_pred_flag = stream.readFlag();                             // u(1)
        weighted_bipred_flag = stream.readFlag();                           // u(1)
        transquant_bypass_enabled_flag = stream.readFlag();                 // u(1)
        tiles_enabled_flag = stream.readFlag();                             // u(1)
        entropy_coding_sync_enabled_flag = stream.readFlag();               // u(1)

        if (tiles_enabled_flag)
        {
            num_tile_columns_minus1 = stream.readUev();                    // ue(v)
            num_tile_rows_minus1 = stream.readUev();                       // ue(v)
            uniform_spacing_flag = stream.readFlag();                       // u(1)

            if (!uniform_spacing_flag)
            {
                column_width_minus1 = new /*UInt32*/long[(int) (num_tile_columns_minus1 & 0xFFFFFFFFL)];
                row_height_minus1 = new /*UInt32*/long[(int) (num_tile_rows_minus1 & 0xFFFFFFFFL)];

                for (int i = 0; i < (num_tile_columns_minus1 & 0xFFFFFFFFL); i++)
                    column_width_minus1[i] = stream.readUev();             // ue(v)

                for (int i = 0; i < (num_tile_rows_minus1 & 0xFFFFFFFFL); i++)
                    row_height_minus1[i] = stream.readUev();               // ue(v)
            }

            loop_filter_across_tiles_enabled_flag = stream.readFlag();      // u(1)
        }

        pps_loop_filter_across_slices_enabled_flag = stream.readFlag();     // u(1)
        deblocking_filter_control_present_flag = stream.readFlag();         // u(1)
        if (deblocking_filter_control_present_flag)
        {
            deblocking_filter_override_enabled_flag = stream.readFlag();    // u(1)
            pps_deblocking_filter_disabled_flag = stream.readFlag();        // u(1)
            if (!pps_deblocking_filter_disabled_flag)
            {
                pps_beta_offset_div2 = stream.readSev();                   // se(v)
                pps_tc_offset_div2 = stream.readSev();                     // se(v)
            }
        }

        pps_scaling_list_data_present_flag = stream.readFlag();             // u(1)
        if (pps_scaling_list_data_present_flag)
        {
            new scaling_list_data(stream);
        }

        lists_modification_present_flag = stream.readFlag();                // u(1)
        log2_parallel_merge_level_minus2 = stream.readUev();               // ue(v)
        slice_segment_header_extension_present_flag = stream.readFlag();    // u(1)

        pps_extension_present_flag = stream.readFlag();                     // u(1)
        if (pps_extension_present_flag)
        {
            pps_range_extension_flag = stream.readFlag();                   // u(1)
            pps_multilayer_extension_flag = stream.readFlag();              // u(1)
            pps_3d_extension_flag = stream.readFlag();                      // u(1)
            pps_scc_extension_flag = stream.readFlag();                     // u(1)
            pps_extension_4bits = (byte) stream.read(4);                     // u(4)
        }

        pps_range_ext = pps_range_extension_flag ? new pps_range_extension(stream, transform_skip_enabled_flag) : new pps_range_extension();

        if (pps_multilayer_extension_flag)
        {
            /*pps_multilayer_ext =*/
            new pps_multilayer_extension(stream); /* specified in Annex F */
        }

        if (pps_3d_extension_flag)
        {
            /*pps_3d_ext =*/
            new pps_3d_extension(stream); /* specified in Annex I */
        }

        pps_scc_ext = pps_scc_extension_flag ? new pps_scc_extension(stream) : new pps_scc_extension();

        if ((pps_extension_4bits & 0xFF) > 0)
        {
            while (stream.hasMoreRbspData(getEndPosition()))
                pps_extension_data_flag = stream.readFlag();         // u(1)
        }

        new rbsp_trailing_bits(stream);

        ctb_raster_and_tile_scan();
        zscan_init();
    }

    @Override
    public final String toString()
    {
        return String.format("NAL Unit PPS \nNumber of l0 l1 refs: %d %d ", (num_ref_idx_l0_default_active_minus1 & 0xFFFFFFFFL) + 1, (num_ref_idx_l1_default_active_minus1 & 0xFFFFFFFFL) + 1);
    }

    public final /*UInt32*/long getLog2MinCuQpDeltaSize()
    {
        return (((sps.getCtbLog2SizeY() & 0xFF) - (diff_cu_qp_delta_depth & 0xFFFFFFFFL)) & 0xFFFFFFFFL);
    }

    public final /*UInt32*/long getLog2MinCuChromaQpOffsetSize()
    {
        return (((sps.getCtbLog2SizeY() & 0xFF) - (pps_range_ext.diff_cu_chroma_qp_offset_depth & 0xFFFFFFFFL)) & 0xFFFFFFFFL);
    }

    public final /*UInt32*/long getLog2MaxTransformSkipSize()
    {
        return (((pps_range_ext.log2_max_transform_skip_block_size_minus2 & 0xFFFFFFFFL) + 2) & 0xFFFFFFFFL);
    }

    public final int getPpsActQpOffsetY()
    {
        return pps_scc_ext.pps_act_y_qp_offset_plus5 - 5;
    }

    public final int getPpsActQpOffsetCb()
    {
        return pps_scc_ext.pps_act_cb_qp_offset_plus5 - 5;
    }

    public final int getPpsActQpOffsetCr()
    {
        return pps_scc_ext.pps_act_cr_qp_offset_plus3 - 3;
    }

    // 6.5.1 CTB raster and tile scanning conversion process
    final void ctb_raster_and_tile_scan()
    {
        colWidth = new /*UInt32*/long[(int) (((num_tile_columns_minus1 & 0xFFFFFFFFL) + 1) & 0xFFFFFFFFL)];
        rowHeight = new /*UInt32*/long[(int) (((num_tile_rows_minus1 & 0xFFFFFFFFL) + 1) & 0xFFFFFFFFL)];

        if (uniform_spacing_flag)
        {
            for (int i = 0; i <= (num_tile_columns_minus1 & 0xFFFFFFFFL); i++)
            {
                colWidth[i] = ((((i + 1) * (sps.getPicWidthInCtbsY() & 0xFFFFFFFFL)) & 0xFFFFFFFFL) / (((num_tile_columns_minus1 & 0xFFFFFFFFL) + 1) & 0xFFFFFFFFL) - ((i * (sps.getPicWidthInCtbsY() & 0xFFFFFFFFL)) & 0xFFFFFFFFL) / (((num_tile_columns_minus1 & 0xFFFFFFFFL) + 1) & 0xFFFFFFFFL)) & 0xFFFFFFFFL;
            }

            for (int j = 0; j <= (num_tile_rows_minus1 & 0xFFFFFFFFL); j++)
            {
                rowHeight[j] = ((((j + 1) * (sps.getPicHeightInCtbsY() & 0xFFFFFFFFL)) & 0xFFFFFFFFL) / (((num_tile_rows_minus1 & 0xFFFFFFFFL) + 1) & 0xFFFFFFFFL) - ((j * (sps.getPicHeightInCtbsY() & 0xFFFFFFFFL)) & 0xFFFFFFFFL) / (((num_tile_rows_minus1 & 0xFFFFFFFFL) + 1) & 0xFFFFFFFFL)) & 0xFFFFFFFFL;
            }
        }
        else
        {
            colWidth[(int) (num_tile_columns_minus1)] = sps.getPicWidthInCtbsY();
            rowHeight[(int) (num_tile_rows_minus1)] = sps.getPicHeightInCtbsY();

            for (int i = 0; i < (num_tile_columns_minus1 & 0xFFFFFFFFL); i++)
            {
                colWidth[i] = ((column_width_minus1[i] & 0xFFFFFFFFL) + 1) & 0xFFFFFFFFL;
                colWidth[(int) (num_tile_columns_minus1)] = ((colWidth[(int) (num_tile_columns_minus1)] & 0xFFFFFFFFL) - (colWidth[i] & 0xFFFFFFFFL)) & 0xFFFFFFFFL;
            }

            for (int j = 0; j < (num_tile_rows_minus1 & 0xFFFFFFFFL); j++)
            {
                rowHeight[j] = ((row_height_minus1[j] & 0xFFFFFFFFL) + 1) & 0xFFFFFFFFL;
                rowHeight[(int) (num_tile_rows_minus1)] = ((rowHeight[(int) (num_tile_rows_minus1)] & 0xFFFFFFFFL) - (rowHeight[j] & 0xFFFFFFFFL)) & 0xFFFFFFFFL;
            }
        }


        colBd = new /*UInt32*/long[(int) (((num_tile_columns_minus1 & 0xFFFFFFFFL) + 2) & 0xFFFFFFFFL)];
        rowBd = new /*UInt32*/long[(int) (((num_tile_rows_minus1 & 0xFFFFFFFFL) + 2) & 0xFFFFFFFFL)];

        colBd[0] = 0;
        rowBd[0] = 0;

        for (int i = 0; i <= (num_tile_columns_minus1 & 0xFFFFFFFFL); i++)
            colBd[i + 1] = ((colBd[i] & 0xFFFFFFFFL) + (colWidth[i] & 0xFFFFFFFFL)) & 0xFFFFFFFFL;

        for (int j = 0; j <= (num_tile_rows_minus1 & 0xFFFFFFFFL); j++)
            rowBd[j + 1] = ((rowBd[j] & 0xFFFFFFFFL) + (rowHeight[j] & 0xFFFFFFFFL)) & 0xFFFFFFFFL;


        CtbAddrRsToTs = new /*UInt32*/long[(int) (sps.getPicSizeInCtbsY())];
        CtbAddrTsToRs = new /*UInt32*/long[(int) (sps.getPicSizeInCtbsY())];
        TileId = new /*UInt32*/long[(int) (sps.getPicSizeInCtbsY())];
        TileIdFromRs = new /*UInt32*/long[(int) (sps.getPicSizeInCtbsY())];

        /*UInt32*/
        long tbX, tbY;
        /*UInt32*/
        long tileX = 0;
        /*UInt32*/
        long tileY = 0;

        for (/*UInt32*/long ctbAddrRs = 0; (ctbAddrRs & 0xFFFFFFFFL) < (sps.getPicSizeInCtbsY() & 0xFFFFFFFFL); ctbAddrRs++)
        {
            tbX = ((ctbAddrRs & 0xFFFFFFFFL) % (sps.getPicWidthInCtbsY() & 0xFFFFFFFFL)) & 0xFFFFFFFFL;
            tbY = ((ctbAddrRs & 0xFFFFFFFFL) / (sps.getPicWidthInCtbsY() & 0xFFFFFFFFL)) & 0xFFFFFFFFL;

            for (/*UInt32*/long i = 0; (i & 0xFFFFFFFFL) <= (num_tile_columns_minus1 & 0xFFFFFFFFL); i++)
                if ((tbX & 0xFFFFFFFFL) >= (colBd[(int) (i)] & 0xFFFFFFFFL))
                {
                    tileX = i;
                }

            for (/*UInt32*/long j = 0; (j & 0xFFFFFFFFL) <= (num_tile_rows_minus1 & 0xFFFFFFFFL); j++)
                if ((tbY & 0xFFFFFFFFL) >= (rowBd[(int) (j)] & 0xFFFFFFFFL))
                {
                    tileY = j;
                }

            CtbAddrRsToTs[(int) (ctbAddrRs)] = 0;

            for (int i = 0; i < (tileX & 0xFFFFFFFFL); i++)
                CtbAddrRsToTs[(int) (ctbAddrRs)] = ((CtbAddrRsToTs[(int) (ctbAddrRs)] & 0xFFFFFFFFL) + (((rowHeight[(int) (tileY)] & 0xFFFFFFFFL) * (colWidth[i] & 0xFFFFFFFFL)) & 0xFFFFFFFFL)) & 0xFFFFFFFFL;

            for (int j = 0; j < (tileY & 0xFFFFFFFFL); j++)
                CtbAddrRsToTs[(int) (ctbAddrRs)] = ((CtbAddrRsToTs[(int) (ctbAddrRs)] & 0xFFFFFFFFL) + (((sps.getPicWidthInCtbsY() & 0xFFFFFFFFL) * (rowHeight[j] & 0xFFFFFFFFL)) & 0xFFFFFFFFL)) & 0xFFFFFFFFL;

            CtbAddrRsToTs[(int) (ctbAddrRs)] = ((CtbAddrRsToTs[(int) (ctbAddrRs)] & 0xFFFFFFFFL) + (((((((((tbY & 0xFFFFFFFFL) - (rowBd[(int) (tileY)] & 0xFFFFFFFFL)) & 0xFFFFFFFFL) * (colWidth[(int) (tileX)] & 0xFFFFFFFFL)) & 0xFFFFFFFFL) + (tbX & 0xFFFFFFFFL)) & 0xFFFFFFFFL) - (colBd[(int) (tileX)] & 0xFFFFFFFFL)) & 0xFFFFFFFFL)) & 0xFFFFFFFFL;

            CtbAddrTsToRs[(int) (CtbAddrRsToTs[(int) (ctbAddrRs)])] = ctbAddrRs;
        }

        for (/*UInt32*/long j = 0, tileIdx = 0; (j & 0xFFFFFFFFL) <= (num_tile_rows_minus1 & 0xFFFFFFFFL); j++)
        {
            for (/*UInt32*/long i = 0; (i & 0xFFFFFFFFL) <= (num_tile_columns_minus1 & 0xFFFFFFFFL); i++, tileIdx++)
            {
                for (/*UInt32*/long y = rowBd[(int) (j)]; (y & 0xFFFFFFFFL) < (rowBd[(int) (((j & 0xFFFFFFFFL) + 1) & 0xFFFFFFFFL)] & 0xFFFFFFFFL); y++)
                {
                    for (/*UInt32*/long x = colBd[(int) (i)]; (x & 0xFFFFFFFFL) < (colBd[(int) (((i & 0xFFFFFFFFL) + 1) & 0xFFFFFFFFL)] & 0xFFFFFFFFL); x++)
                    {
                        TileId[(int) (CtbAddrRsToTs[(int) (((((y & 0xFFFFFFFFL) * (sps.getPicWidthInCtbsY() & 0xFFFFFFFFL)) & 0xFFFFFFFFL) + (x & 0xFFFFFFFFL)) & 0xFFFFFFFFL)])] = tileIdx;
                        TileIdFromRs[(int) (((((y & 0xFFFFFFFFL) * (sps.getPicWidthInCtbsY() & 0xFFFFFFFFL)) & 0xFFFFFFFFL) + (x & 0xFFFFFFFFL)) & 0xFFFFFFFFL)] = tileIdx;
                    }
                }
            }
        }


//        ColumnWidthInLumaSamples = new /*UInt32*/long[(int) (((num_tile_columns_minus1 & 0xFFFFFFFFL) + 1) & 0xFFFFFFFFL)];
//        RowHeightInLumaSamples = new /*UInt32*/long[(int) (((num_tile_rows_minus1 & 0xFFFFFFFFL) + 1) & 0xFFFFFFFFL)];
//
//        for (int i = 0; i <= (num_tile_columns_minus1 & 0xFFFFFFFFL); i++)
//            ColumnWidthInLumaSamples[i] = ((colWidth[i] & 0xFFFFFFFFL) << ((sps.getCtbLog2SizeY() & 0xFF) & 0x1F)) & 0xFFFFFFFFL;
//
//        for (int i = 0; i <= (num_tile_rows_minus1 & 0xFFFFFFFFL); i++)
//            RowHeightInLumaSamples[i] = ((rowHeight[i] & 0xFFFFFFFFL) << ((sps.getCtbLog2SizeY() & 0xFF) & 0x1F)) & 0xFFFFFFFFL;
    }

    // 6.5.2 Z-scan order array initialization process
    final void zscan_init()
    {
        /*UInt32*/
        long tbX, tbY, ctbAddrRs, p, m;
        /*UInt32*/
        long xMax = ((sps.getPicWidthInCtbsY() & 0xFFFFFFFFL) << (((sps.getCtbLog2SizeY() & 0xFF) - (sps.getMinTbLog2SizeY() & 0xFF)) & 0x1F)) & 0xFFFFFFFFL;
        /*UInt32*/
        long yMax = ((sps.getPicHeightInCtbsY() & 0xFFFFFFFFL) << (((sps.getCtbLog2SizeY() & 0xFF) - (sps.getMinTbLog2SizeY() & 0xFF)) & 0x1F)) & 0xFFFFFFFFL;
        MinTbAddrZs = new /*UInt32*/int[(int) (xMax & 0xFFFFFFFFL)][(int) (yMax & 0xFFFFFFFFL)];

        for (/*UInt32*/long y = 0; (y & 0xFFFFFFFFL) < (yMax & 0xFFFFFFFFL); y++)
        {
            for (/*UInt32*/long x = 0; (x & 0xFFFFFFFFL) < (xMax & 0xFFFFFFFFL); x++)
            {
                tbX = ((((x & 0xFFFFFFFFL) << ((sps.getMinTbLog2SizeY() & 0xFF) & 0x1F)) & 0xFFFFFFFFL) >> (sps.getCtbLog2SizeY() & 0xFF)) & 0xFFFFFFFFL;
                tbY = ((((y & 0xFFFFFFFFL) << ((sps.getMinTbLog2SizeY() & 0xFF) & 0x1F)) & 0xFFFFFFFFL) >> (sps.getCtbLog2SizeY() & 0xFF)) & 0xFFFFFFFFL;
                ctbAddrRs = ((((sps.getPicWidthInCtbsY() & 0xFFFFFFFFL) * (tbY & 0xFFFFFFFFL)) & 0xFFFFFFFFL) + (tbX & 0xFFFFFFFFL)) & 0xFFFFFFFFL;
                MinTbAddrZs[(int) (x)][(int) (y)] = (int) ((CtbAddrRsToTs[(int) (ctbAddrRs)] & 0xFFFFFFFFL) << ((((sps.getCtbLog2SizeY() & 0xFF) - (sps.getMinTbLog2SizeY() & 0xFF)) * 2) & 0x1F));

                p = 0;
                for (byte i = 0; (i & 0xFF) < ((sps.getCtbLog2SizeY() & 0xFF) - (sps.getMinTbLog2SizeY() & 0xFF)); i++)
                {
                    m = (1L << (i & 0xFF)) & 0xFFFFFFFFL;
                    p = ((p & 0xFFFFFFFFL) + ((((m & x) & 0xFFFFFFFFL) != 0 ? (((m & 0xFFFFFFFFL) * (m & 0xFFFFFFFFL)) & 0xFFFFFFFFL) : 0) & 0xFFFFFFFFL)) & 0xFFFFFFFFL;
                    p = ((p & 0xFFFFFFFFL) + ((((m & y) & 0xFFFFFFFFL) != 0 ? ((((2 * (m & 0xFFFFFFFFL)) & 0xFFFFFFFFL) * (m & 0xFFFFFFFFL)) & 0xFFFFFFFFL) : 0) & 0xFFFFFFFFL)) & 0xFFFFFFFFL;
                }
                MinTbAddrZs[(int) (x)][(int) (y)] = (int) ((MinTbAddrZs[(int) (x)][(int) (y)] & 0xFFFFFFFFL) + (p & 0xFFFFFFFFL));
            }
        }
    }

    /**
     * <p>
     *     Calculation if tile is first (required in 8.6.1)
     * </p>
     */
    public boolean check_if_tile_is_first(int tileX, int tileY)
    {
        for (int i = 0; i <= num_tile_columns_minus1; i++)
        {
            if (colBd[i] == tileX)
            {
                for (int k = 0; k <= num_tile_rows_minus1; k++)
                {
                    if (rowBd[k] == tileY)
                    {
                        return true;
                    }
                }

                return false;
            }
        }

        return false;
    }
}
