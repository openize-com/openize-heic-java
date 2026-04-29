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


class coding_unit
{
    public static final  byte[] IntraPredModeChroma422Map = {
            0, 1, 2, 2, 2, 2, 3, 5, 7, 8, 10, 12, 13, 15, 17, 18, 19, 20,
            21, 22, 23, 23, 24, 24, 25, 25, 26, 27, 27, 28, 28, 29, 29, 30, 31
    };
    PartMode _PartMode;
    boolean IntraSplitFlag;
    boolean rqt_root_cbf;
    /*UInt32*/ long MaxTrafoDepth;


    public coding_unit(BitStreamWithNalSupport stream, slice_segment_header header,
                       int x0, int y0, int log2CbSize)
    {
        seq_parameter_set_rbsp sps = header.pps.sps;
        HeicPicture picture = header.parentPicture;
        picture.setLog2CbSize(x0, y0, log2CbSize);

        _PartMode = PartMode.PART_2Nx2N;
        IntraSplitFlag = false;

        stream.getContext().derivationOfQuantizationParameters(stream, picture, x0, y0, x0, y0);

        if (header.pps.transquant_bypass_enabled_flag)
        {
            stream.getContext().cu_transquant_bypass_flag = stream.getCabac().read_cu_transquant_bypass_flag();
        }

        if (header.slice_type != SliceType.I)
        {
            picture.cu_skip_flag[x0][y0] =
                    stream.getCabac().read_cu_skip_flag(x0, y0, picture, picture.CtDepth[x0][y0]);
        }

        int nCbS = (1 << log2CbSize);

        if (picture.cu_skip_flag[x0][y0])
        {
            picture.setCuPredMode(x0, y0, nCbS, PredMode.MODE_SKIP);
            throw new UnsupportedOperationException();
            // new prediction_unit(stream, header, x0, y0, nCbS, nCbS);
            // need this for deblocking filter
            // picture.SetPartMode(x0, y0, PART_2Nx2N);
        }
        else
        {
            if (header.slice_type != SliceType.I)
            {
                picture.setCuPredMode(x0, y0, nCbS,
                        stream.getCabac().read_pred_mode_flag() ? PredMode.MODE_INTRA : PredMode.MODE_INTER);
            }
            else
            {
                picture.setCuPredMode(x0, y0, nCbS, PredMode.MODE_INTRA);
            }

            if (header.pps.sps.sps_scc_ext.palette_mode_enabled_flag &&
                    picture.CuPredMode[x0][y0] == PredMode.MODE_INTRA &&
                    log2CbSize <= (sps.getMaxTbLog2SizeY() & 0xFF))
            {
                picture.palette_mode_flag[x0][y0] = stream.getCabac().read_palette_mode_flag();
            }

            if (picture.palette_mode_flag[x0][y0])
            {
                new palette_coding(stream, header, x0, y0, nCbS);
            }
            else
            {
                if (picture.CuPredMode[x0][y0] != PredMode.MODE_INTRA ||
                        log2CbSize == (sps.getMinCbLog2SizeY() & 0xFF))
                {
                    _PartMode = stream.getCabac().read_part_mode(picture, picture.CuPredMode[x0][y0], log2CbSize);
                    IntraSplitFlag =
                            picture.CuPredMode[x0][y0] == PredMode.MODE_INTRA &&
                                    _PartMode == PartMode.PART_NxN;
                }

                if (picture.CuPredMode[x0][y0] == PredMode.MODE_INTRA)
                {
                    if (_PartMode == PartMode.PART_2Nx2N &&
                            header.pps.sps.pcm_enabled_flag &&
                            log2CbSize >= sps.getLog2MinIpcmCbSizeY() &&
                            log2CbSize <= sps.getLog2MaxIpcmCbSizeY())
                    {
                        picture.setPcmFlag(x0, y0, nCbS, stream.getCabac().read_pcm_flag());
                    }

                    if (picture.pcm_flag[x0][y0])
                    {
                        while (stream.notByteAligned())
                            stream.skipBits(1);  /* pcm_alignment_zero_bit */

                        new pcm_sample(stream, header, log2CbSize);
                    }
                    else
                    {
                        int pbOffset = (_PartMode == PartMode.PART_NxN) ? (nCbS / 2) : nCbS;


                        for (int j = 0; j < nCbS; j += pbOffset)
                            for (int i = 0; i < nCbS; i += pbOffset)
                                picture.prev_intra_luma_pred_flag[x0 + i][y0 + j] = stream.getCabac().read_prev_intra_luma_pred_flag();

                        boolean availableL0 = picture.checkZScanAvailability(x0, y0, x0 - 1, y0);
                        boolean availableA0 = picture.checkZScanAvailability(x0, y0, x0, y0 - 1);

                        int log2blkSize = (_PartMode == PartMode.PART_NxN) ? (log2CbSize - 1) : log2CbSize;
                        //int pbSize = 1 << (log2blkSize - (sps.MinCbLog2SizeY - 1));
                        int pbSize = 1 << log2blkSize;


                        for (int j = 0; j < nCbS; j += pbOffset)
                        {
                            for (int i = 0; i < nCbS; i += pbOffset)
                            {
                                if (picture.prev_intra_luma_pred_flag[x0 + i][y0 + j])
                                {
                                    picture.mpm_idx[x0 + i][y0 + j] = stream.getCabac().read_mpm_idx();
                                }
                                else
                                {
                                    picture.rem_intra_luma_pred_mode[x0 + i][y0 + j] = stream.getCabac().read_rem_intra_luma_pred_mode();
                                }


                                IntraPredMode[] intraPredModeCandidated = picture.generateIntraPredModeCandidates(
                                        x0 + i, y0 + j, (1 << ((sps.getMinCbLog2SizeY() & 0xFF) - 1)), availableL0 || i > 0, availableA0 || j > 0);

                                /*IntraPredMode luma_mode =*/
                                picture.setIntraPredModeY(x0 + i, y0 + j, pbSize, intraPredModeCandidated);
                            }
                        }

                        if ((sps.getChromaArrayType() & 0xFFFFFFFFL) == 3)
                        {
                            // chroma 4:4:4

                            for (int j = 0; j < nCbS; j += pbOffset)
                            {
                                for (int i = 0; i < nCbS; i += pbOffset)
                                {
                                    picture.intra_chroma_pred_mode[x0 + i][y0 + j] = stream.getCabac().read_intra_chroma_pred_mode();

                                    IntraPredMode IntraPredModeC = picture.deriveIntraPredModeIndex(
                                            picture.IntraPredModeY[x0 + i][y0 + j],
                                            picture.intra_chroma_pred_mode[x0 + i][y0 + j]);

                                    picture.setIntraPredModeC(x0 + i, y0 + j, pbSize, IntraPredModeC);
                                }
                            }

                        }
                        else if ((sps.getChromaArrayType() & 0xFFFFFFFFL) != 0)
                        {
                            // chroma 4:2:0 and 4:2:2

                            picture.intra_chroma_pred_mode[x0][y0] = stream.getCabac().read_intra_chroma_pred_mode();

                            IntraPredMode IntraPredModeC = picture.deriveIntraPredModeIndex(
                                    picture.IntraPredModeY[x0][y0],
                                    picture.intra_chroma_pred_mode[x0][y0]);

                            if ((sps.getChromaArrayType() & 0xFFFFFFFFL) == 2)
                            {
                                IntraPredModeC = IntraPredMode.get((IntraPredModeChroma422Map[IntraPredModeC.ordinal()] & 0xFF));
                            }

                            picture.setIntraPredModeC(x0, y0, 1 << log2CbSize, IntraPredModeC);
                            // not sure if that's correct
                        }
                    }
                }
                else
                {
                    switch (_PartMode)
                    {
                        case PART_2Nx2N:
                            new prediction_unit(stream, header, x0, y0, nCbS, nCbS);
                            break;
                        case PART_2NxN:
                            new prediction_unit(stream, header, x0, y0, nCbS, nCbS / 2);
                            new prediction_unit(stream, header, x0, y0 + (nCbS / 2), nCbS, nCbS / 2);
                            break;
                        case PART_Nx2N:
                            new prediction_unit(stream, header, x0, y0, nCbS / 2, nCbS);
                            new prediction_unit(stream, header, x0 + (nCbS / 2), y0, nCbS / 2, nCbS);
                            break;
                        case PART_2NxnU:
                            new prediction_unit(stream, header, x0, y0, nCbS, nCbS / 4);
                            new prediction_unit(stream, header, x0, y0 + (nCbS / 4), nCbS, nCbS * 3 / 4);
                            break;
                        case PART_2NxnD:
                            new prediction_unit(stream, header, x0, y0, nCbS, nCbS * 3 / 4);
                            new prediction_unit(stream, header, x0, y0 + (nCbS * 3 / 4), nCbS, nCbS / 4);
                            break;
                        case PART_nLx2N:
                            new prediction_unit(stream, header, x0, y0, nCbS / 4, nCbS);
                            new prediction_unit(stream, header, x0 + (nCbS / 4), y0, nCbS * 3 / 4, nCbS);
                            break;
                        case PART_nRx2N:
                            new prediction_unit(stream, header, x0, y0, nCbS * 3 / 4, nCbS);
                            new prediction_unit(stream, header, x0 + (nCbS * 3 / 4), y0, nCbS / 4, nCbS);
                            break;
                        case PART_NxN:
                        default:
                            new prediction_unit(stream, header, x0, y0, nCbS / 2, nCbS / 2);
                            new prediction_unit(stream, header, x0 + (nCbS / 2), y0, nCbS / 2, nCbS / 2);
                            new prediction_unit(stream, header, x0, y0 + (nCbS / 2), nCbS / 2, nCbS / 2);
                            new prediction_unit(stream, header, x0 + (nCbS / 2), y0 + (nCbS / 2), nCbS / 2, nCbS / 2);
                            break;
                    }
                }

                if (!picture.pcm_flag[x0][y0])
                {
                    rqt_root_cbf = true;

                    boolean merge_flag = picture.merge_flag[x0][y0];

                    if (picture.CuPredMode[x0][y0] != PredMode.MODE_INTRA &&
                            !(_PartMode == PartMode.PART_2Nx2N && merge_flag))
                    {
                        rqt_root_cbf = stream.getCabac().read_rqt_root_cbf();
                    }

                    if (rqt_root_cbf)
                    {
                        MaxTrafoDepth =
                                (picture.CuPredMode[x0][y0] == PredMode.MODE_INTRA ?
                                        (((header.pps.sps.max_transform_hierarchy_depth_intra & 0xFFFFFFFFL) + (IntraSplitFlag ? 1 : 0)) & 0xFFFFFFFFL) :
                                        (header.pps.sps.max_transform_hierarchy_depth_inter & 0xFFFFFFFFL)) & 0xFFFFFFFFL;

                        new transform_tree(stream, header, IntraSplitFlag, MaxTrafoDepth, _PartMode,
                                x0, y0, x0, y0, log2CbSize, 0, 0);
                    }
                }
            }
        }
    }
}

