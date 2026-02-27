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


// 7.4.9.11 Residual coding semantics
class residual_coding
{
    final int last_sig_coeff_x_prefix;
    final int last_sig_coeff_y_prefix;
    int last_sig_coeff_x_suffix;
    int last_sig_coeff_y_suffix;

    @SuppressWarnings({"ReassignedVariable", "SuspiciousNameCombination"})
    public residual_coding(BitStreamWithNalSupport stream, slice_segment_header header,
                           int x0, int y0, int log2TrafoSize, int cIdx)
    {
        pic_parameter_set_rbsp pps = header.pps;
        seq_parameter_set_rbsp sps = header.pps.sps;
        HeicPicture picture = header.parentPicture;

        DecoderContext streamContext = stream.getContext();
        streamContext.transform_skip_flag[cIdx] = false;

        boolean[] explicit_rdpcm_flag_x0_y0 = picture.explicit_rdpcm_flag[x0][y0];
        if (explicit_rdpcm_flag_x0_y0 == null)
        {
            explicit_rdpcm_flag_x0_y0 = new boolean[3];
            picture.explicit_rdpcm_flag[x0][y0] = explicit_rdpcm_flag_x0_y0;
        }

        Cabac streamCabac = stream.getCabac();
        if (header.pps.transform_skip_enabled_flag && !streamContext.cu_transquant_bypass_flag &&
                (log2TrafoSize <= (pps.getLog2MaxTransformSkipSize() & 0xFFFFFFFFL)))
        {
            streamContext.transform_skip_flag[cIdx] = streamCabac.read_transform_skip_flag(cIdx);
        }

        if (picture.CuPredMode[x0][y0] == PredMode.MODE_INTER &&
                header.pps.sps.sps_range_ext.explicit_rdpcm_enabled_flag &&
                (streamContext.transform_skip_flag[cIdx] || streamContext.cu_transquant_bypass_flag))
        {
            explicit_rdpcm_flag_x0_y0[cIdx] = streamCabac.read_explicit_rdpcm_flag(cIdx);

            if (explicit_rdpcm_flag_x0_y0[cIdx])
            {
                picture.explicit_rdpcm_dir_flag[x0][y0][cIdx] = streamCabac.read_explicit_rdpcm_dir_flag(cIdx);
            }
        }

        last_sig_coeff_x_prefix = streamCabac.read_last_sig_coeff_prefix(log2TrafoSize, cIdx, CabacType.last_sig_coeff_x_prefix);
        last_sig_coeff_y_prefix = streamCabac.read_last_sig_coeff_prefix(log2TrafoSize, cIdx, CabacType.last_sig_coeff_y_prefix);

        if (last_sig_coeff_x_prefix > 3)
        {
            last_sig_coeff_x_suffix = streamCabac.read_last_sig_coeff_suffix(last_sig_coeff_x_prefix);
        }

        if (last_sig_coeff_y_prefix > 3)
        {
            last_sig_coeff_y_suffix = streamCabac.read_last_sig_coeff_suffix(last_sig_coeff_y_prefix);
        }

        int LastSignificantCoeffX =
                (last_sig_coeff_x_prefix <= 3) ? last_sig_coeff_x_prefix :
                        (1 << ((last_sig_coeff_x_prefix >> 1) - 1)) * (2 + (last_sig_coeff_x_prefix & 1)) + last_sig_coeff_x_suffix;

        int LastSignificantCoeffY =
                (last_sig_coeff_y_prefix <= 3) ? last_sig_coeff_y_prefix :
                        (1 << ((last_sig_coeff_y_prefix >> 1) - 1)) * (2 + (last_sig_coeff_y_prefix & 1)) + last_sig_coeff_y_suffix;

        // define scanIdx
        int scanIdx = 0;
        if (picture.CuPredMode[x0][y0] == PredMode.MODE_INTRA)
        {
            if ((log2TrafoSize == 2) ||
                    (log2TrafoSize == 3 && cIdx == 0) ||
                    (log2TrafoSize == 3 && (header.pps.sps.getChromaArrayType() & 0xFFFFFFFFL) == 3))
            {
                IntraPredMode predModeIntra = cIdx == 0
                        ? picture.IntraPredModeY[x0][y0]
                        : picture.IntraPredModeC[x0][y0];

                if (predModeIntra.ordinal() >= 6 && predModeIntra.ordinal() <= 14)
                {
                    scanIdx = 2;
                }

                if (predModeIntra.ordinal() >= 22 && predModeIntra.ordinal() <= 30)
                {
                    scanIdx = 1;
                }
            }
        }

        if (scanIdx == 2)
        {
            int tmp = LastSignificantCoeffX;
            LastSignificantCoeffX = LastSignificantCoeffY;
            LastSignificantCoeffY = tmp;
        }

        int lastScanPos = 16;
        int lastSubBlock = (1 << (log2TrafoSize - 2)) * (1 << (log2TrafoSize - 2)) - 1;

        /*UInt32*/
        long xS, yS, xC, yC;
        byte[][][][] scanOrder = Scans.getScanOrder();
        byte[][] scanOrderLogTrafoSize_2_ScanIdx = scanOrder[log2TrafoSize - 2][scanIdx];
        do
        {
            if (lastScanPos == 0)
            {
                lastScanPos = 16;
                lastSubBlock--;
            }

            lastScanPos--;

            xS = scanOrderLogTrafoSize_2_ScanIdx[lastSubBlock][0] & 0xFF;
            yS = scanOrderLogTrafoSize_2_ScanIdx[lastSubBlock][1] & 0xFF;

            xC = ((xS << 2) + (scanOrder[2][scanIdx][lastScanPos][0] & 0xFFL));
            yC = ((yS << 2) + (scanOrder[2][scanIdx][lastScanPos][1] & 0xFFL));
        } while (((xC & 0xFFFFFFFFL) != LastSignificantCoeffX) || ((yC & 0xFFFFFFFFL) != LastSignificantCoeffY));

        int arrSize = 1 << log2TrafoSize;
        boolean[][] coded_sub_block_flag = new boolean[arrSize][arrSize];
        boolean[][] sig_coeff_flag = new boolean[arrSize][arrSize];

        int lastSubblock_greater1Ctx = 0;

        int lastInvocation_ctxSet = 0;
        int lastInvocation_greater1Ctx = 0;
        boolean lastInvocation_coeff_abs_level_greater1_flag = false;
        boolean firstSubblock = true;
        boolean firstCoeffInSubblock;

        for (int i = lastSubBlock; i >= 0; i--)
        {
            xS = scanOrderLogTrafoSize_2_ScanIdx[i][0] & 0xFF;
            yS = scanOrderLogTrafoSize_2_ScanIdx[i][1] & 0xFF;

            //int escapeDataPresent = 0;
            boolean inferSbDcSigCoeffFlag = false;

            if ((i < lastSubBlock) && (i > 0))
            {
                coded_sub_block_flag[(int) (xS)][(int) (yS)] = streamCabac.read_coded_sub_block_flag(
                        xS, yS, coded_sub_block_flag, cIdx, log2TrafoSize);
                inferSbDcSigCoeffFlag = true;
            }
            else if (i == 0 || i == lastSubBlock)
            {
                coded_sub_block_flag[(int) (xS)][(int) (yS)] = true;
            }

            if (i == lastSubBlock)
            {
                xC = ((((xS & 0xFFFFFFFFL) << 2) & 0xFFFFFFFFL) + (scanOrder[2][scanIdx][lastScanPos][0] & 0xFF)) & 0xFFFFFFFFL;
                yC = ((((yS & 0xFFFFFFFFL) << 2) & 0xFFFFFFFFL) + (scanOrder[2][scanIdx][lastScanPos][1] & 0xFF)) & 0xFFFFFFFFL;
                sig_coeff_flag[(int) (xC)][(int) (yC)] = true;
            }

            for (int n = (i == lastSubBlock) ? lastScanPos - 1 : 15; n >= 0; n--)
            {
                xC = ((((xS & 0xFFFFFFFFL) << 2) & 0xFFFFFFFFL) + (scanOrder[2][scanIdx][n][0] & 0xFF)) & 0xFFFFFFFFL;
                yC = ((((yS & 0xFFFFFFFFL) << 2) & 0xFFFFFFFFL) + (scanOrder[2][scanIdx][n][1] & 0xFF)) & 0xFFFFFFFFL;

                if (coded_sub_block_flag[(int) (xS)][(int) (yS)])
                {
                    if (n > 0 || !inferSbDcSigCoeffFlag)
                    {
                        sig_coeff_flag[(int) (xC)][(int) (yC)] = streamCabac.read_sig_coeff_flag(picture,
                                streamContext.transform_skip_flag[cIdx], (int) xC, (int) yC, coded_sub_block_flag, cIdx, log2TrafoSize, scanIdx);

                        if (sig_coeff_flag[(int) (xC)][(int) (yC)])
                        {
                            inferSbDcSigCoeffFlag = false;
                        }
                    }
                    else
                    {
                        sig_coeff_flag[(int) (xC)][(int) (yC)] = true;
                    }
                }

            }

            int firstSigScanPos = 16;
            int lastSigScanPos = -1;
            
            byte numGreater1Flag = 0;
            int lastGreater1ScanPos = -1;
            boolean[] coeff_abs_level_greater1_flag = new boolean[16];
            boolean[] coeff_abs_level_greater2_flag = new boolean[16];
            int[] coeff_abs_level_remaining = new int[16];

            firstCoeffInSubblock = true;

            for (int n = 15; n >= 0; n--)
            {
                xC = ((((xS & 0xFFFFFFFFL) << 2) & 0xFFFFFFFFL) + (scanOrder[2][scanIdx][n][0] & 0xFF)) & 0xFFFFFFFFL;
                yC = ((((yS & 0xFFFFFFFFL) << 2) & 0xFFFFFFFFL) + (scanOrder[2][scanIdx][n][1] & 0xFF)) & 0xFFFFFFFFL;

                if (sig_coeff_flag[(int) (xC)][(int) (yC)])
                {
                    if ((numGreater1Flag & 0xFF) < 8)
                    {
                        int[] referenceToLastInvocation_ctxSet = {lastInvocation_ctxSet};
                        int[] referenceToLastInvocation_greater1Ctx = {lastInvocation_greater1Ctx};
                        boolean[] referenceToLastInvocation_coeff_abs_level_greater1_flag = {lastInvocation_coeff_abs_level_greater1_flag};
                        coeff_abs_level_greater1_flag[n] =
                                streamCabac.read_coeff_abs_level_greater1_flag(cIdx, i,
                                        firstCoeffInSubblock, firstSubblock,
                                        lastSubblock_greater1Ctx,
                                        /*ref*/ referenceToLastInvocation_ctxSet,
                                        /*ref*/ referenceToLastInvocation_greater1Ctx,
                                        /*ref*/ referenceToLastInvocation_coeff_abs_level_greater1_flag);
                        lastInvocation_ctxSet = referenceToLastInvocation_ctxSet[0];
                        lastInvocation_greater1Ctx = referenceToLastInvocation_greater1Ctx[0];
                        lastInvocation_coeff_abs_level_greater1_flag = referenceToLastInvocation_coeff_abs_level_greater1_flag[0];

                        numGreater1Flag++;

                        if (coeff_abs_level_greater1_flag[n] && lastGreater1ScanPos == -1)
                        {
                            lastGreater1ScanPos = n;
                        }
//                        else if (coeff_abs_level_greater1_flag[n])
//                        {
//                            escapeDataPresent = 1;
//                        }
                    }
//                    else
//                    {
//                        escapeDataPresent = 1;
//                    }

                    if (lastSigScanPos == -1)
                    {
                        lastSigScanPos = n;
                    }

                    firstSigScanPos = n;
                    firstCoeffInSubblock = false;
                }
            }
            firstSubblock = false;
            lastSubblock_greater1Ctx = lastInvocation_greater1Ctx;


            boolean signHidden;
            IntraPredMode predModeIntra = cIdx == 0 ? picture.IntraPredModeY[x0][y0] : picture.IntraPredModeC[x0][y0];

            if (streamContext.cu_transquant_bypass_flag ||
                    (picture.CuPredMode[x0][y0] == PredMode.MODE_INTRA &&
                            sps.sps_range_ext.implicit_rdpcm_enabled_flag &&
                            streamContext.transform_skip_flag[cIdx] &&
                            (predModeIntra.ordinal() == 10 || predModeIntra.ordinal() == 26)) ||
                    explicit_rdpcm_flag_x0_y0[cIdx])
            {
                signHidden = false;
            }
            else
            {
                signHidden = (lastSigScanPos - firstSigScanPos) > 3;
            }

            if (lastGreater1ScanPos != -1)
            {
                coeff_abs_level_greater2_flag[lastGreater1ScanPos] = streamCabac.read_coeff_abs_level_greater2_flag(cIdx, lastInvocation_ctxSet);
//                if (coeff_abs_level_greater2_flag[lastGreater1ScanPos])
//                {
//                    escapeDataPresent = 1;
//                }
            }

            boolean[] coeff_sign_flag = new boolean[16];
            byte[][] scanOrder_2_scanIdx = scanOrder[2][scanIdx];
            for (int n = 15; n >= 0; n--)
            {
                xC = ((xS << 2) + (scanOrder_2_scanIdx[n][0] & 0xFFL));
                yC = ((yS << 2) + (scanOrder_2_scanIdx[n][1] & 0xFFL));

                if (sig_coeff_flag[(int) (xC)][(int) (yC)] &&
                        (!header.pps.sign_data_hiding_enabled_flag || !signHidden || (n != firstSigScanPos)))
                {
                    coeff_sign_flag[n] = streamCabac.read_coeff_sign_flag();
                }
            }

            int numSigCoeff = 0;
            int sumAbsLevel = 0;
            int TransCoeffLevel;

            int sbType = (cIdx == 0) ? 2 : 0;
            if (streamContext.transform_skip_flag[cIdx] || streamContext.cu_transquant_bypass_flag)
            {
                sbType++;
            }

            int initRiceValue = (streamCabac.StatCoeff[sbType] & 0xFF) / 4;
            if (!sps.sps_range_ext.persistent_rice_adaptation_enabled_flag)
            {
                initRiceValue = 0;
            }

            int cLastAbsLevel;
            int cLastRiceParam = initRiceValue;
            boolean firstInvoke = true;

            scanOrder_2_scanIdx = scanOrder[2][scanIdx];
            for (int n = 15; n >= 0; n--)
            {
                xC = ((xS << 2) + (scanOrder_2_scanIdx[n][0] & 0xFFL));
                yC = ((yS << 2) + (scanOrder_2_scanIdx[n][1] & 0xFFL));

                if (sig_coeff_flag[(int) (xC)][(int) (yC)])
                {
                    int baseLevel = 1 +
                            (coeff_abs_level_greater1_flag[n] ? 1 : 0) +
                            (coeff_abs_level_greater2_flag[n] ? 1 : 0);

                    if (baseLevel == ((numSigCoeff < 8) ?
                            ((n == lastGreater1ScanPos) ? 3 : 2) : 1))
                    {
                        coeff_abs_level_remaining[n] = streamCabac.read_coeff_abs_level_remaining(cLastRiceParam);

                        // 9.3.3.11
                        cLastAbsLevel = baseLevel + coeff_abs_level_remaining[n];
                        cLastRiceParam += (cLastAbsLevel > (3 * (1 << cLastRiceParam)) ? 1 : 0);

                        if (!sps.sps_range_ext.persistent_rice_adaptation_enabled_flag)
                        {
                            cLastRiceParam = Math.min(cLastRiceParam, 4);
                        }

                        if (sps.sps_range_ext.persistent_rice_adaptation_enabled_flag && firstInvoke)
                        {
                            if (coeff_abs_level_remaining[n] >= (3 << ((streamCabac.StatCoeff[sbType] & 0xFF) / 4)))
                            {
                                streamCabac.StatCoeff[sbType]++;
                            }
                            else if (2 * coeff_abs_level_remaining[n] < (1 << ((streamCabac.StatCoeff[sbType] & 0xFF) / 4)) &&
                                    (streamCabac.StatCoeff[sbType] & 0xFF) > 0)
                            {
                                streamCabac.StatCoeff[sbType]--;
                            }

                            firstInvoke = false;
                        }
                    }

                    TransCoeffLevel = (coeff_abs_level_remaining[n] + baseLevel) * (coeff_sign_flag[n] ? -1 : 1);

                    if (header.pps.sign_data_hiding_enabled_flag && signHidden)
                    {
                        sumAbsLevel += (coeff_abs_level_remaining[n] + baseLevel);
                        if ((n == firstSigScanPos) && ((sumAbsLevel % 2) == 1))
                        {
                            TransCoeffLevel = -1 * TransCoeffLevel;
                        }
                    }

                    if (cIdx == 0)
                    {
                        picture.TransCoeffLevel[cIdx][(int) ((x0 + (xC & 0xFFFFFFFFL)) & 0xFFFFFFFFL)][(int) ((y0 + (yC & 0xFFFFFFFFL)) & 0xFFFFFFFFL)] = TransCoeffLevel;
                    }
                    else
                    {
                        picture.TransCoeffLevel[cIdx][(int) ((x0 / (sps.getSubWidthC() & 0xFF) + (xC & 0xFFFFFFFFL)) & 0xFFFFFFFFL)][(int) ((y0 / (sps.getSubHeightC() & 0xFF) + (yC & 0xFFFFFFFFL)) & 0xFFFFFFFFL)] = TransCoeffLevel;
                    }

                    numSigCoeff++;
                }
            }
        }
    }
}
