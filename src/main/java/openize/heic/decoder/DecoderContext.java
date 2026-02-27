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

import java.util.HashMap;


public class DecoderContext
{
    public final HashMap<Integer, video_parameter_set_rbsp> getVPS(){ return vPS; }
    private final HashMap<Integer,video_parameter_set_rbsp> vPS;
    public final HashMap<Integer, seq_parameter_set_rbsp> getSPS(){ return sPS; }
    private final HashMap<Integer,seq_parameter_set_rbsp> sPS;
    public final HashMap<Integer, pic_parameter_set_rbsp> getPPS(){ return pPS; }
    private final HashMap<Integer,pic_parameter_set_rbsp> pPS;
    public final HashMap<Long, HeicPicture> getPictures(){ return pictures; }
    private final HashMap<Long,HeicPicture> pictures;

    //public boolean first_decoded_picture = true;
    public boolean NoRaslOutputFlag = false;
    public boolean HandleCraAsBlaFlag = false;
    public boolean FirstAfterEndOfSequenceNAL = false;

    public int PicOrderCntMsb;
    public int prevPicOrderCntLsb;  // at precTid0Pic
    public int prevPicOrderCntMsb;  // at precTid0Pic

    // coding_unit
    boolean cu_transquant_bypass_flag;
    final boolean[] transform_skip_flag = new boolean[3];

    // CuQp
    public final boolean isCuQpDeltaCoded(){ return isCuQpDeltaCoded; }
    // CuQp
    public final void setCuQpDeltaCoded(boolean value){ isCuQpDeltaCoded = value; }
    private boolean isCuQpDeltaCoded;
    public final int getCuQpDeltaVal(){ return cuQpDeltaVal; }
    public final void setCuQpDeltaVal(int value){ cuQpDeltaVal = value; }
    private int cuQpDeltaVal;
    public final boolean isCuChromaQpOffsetCoded(){ return isCuChromaQpOffsetCoded; }
    public final void setCuChromaQpOffsetCoded(boolean value){ isCuChromaQpOffsetCoded = value; }
    private boolean isCuChromaQpOffsetCoded;
    public final int getCuQpOffsetCb(){ return cuQpOffsetCb; }
    public final void setCuQpOffsetCb(int value){ cuQpOffsetCb = value; }
    private int cuQpOffsetCb;
    public final int getCuQpOffsetCr(){ return cuQpOffsetCr; }
    public final void setCuQpOffsetCr(int value){ cuQpOffsetCr = value; }
    private int cuQpOffsetCr;

    public final int getQpY(){ return qpY; }
    public final void setQpY(int value){ qpY = value; }
    private int qpY;
    public final int getQpCb(){ return qpCb; }
    public final void setQpCb(int value){ qpCb = value; }
    private int qpCb;
    public final int getQpCr(){ return qpCr; }
    public final void setQpCr(int value){ qpCr = value; }
    private int qpCr;
    public final int getLastQpYPrev(){ return lastQpYPrev; }
    public final void setLastQpYPrev(int value){ lastQpYPrev = value; }
    private int lastQpYPrev;
    public final int getCurrentQgX(){ return currentQgX; }
    public final void setCurrentQgX(int value){ currentQgX = value; }
    private int currentQgX;
    public final int getCurrentQgY(){ return currentQgY; }
    public final void setCurrentQgY(int value){ currentQgY = value; }
    private int currentQgY;
    public final int getCurrentQpY(){ return currentQpY; }
    public final void setCurrentQpY(int value){ currentQpY = value; }
    private int currentQpY;
    

    public DecoderContext()
    {
        vPS = new HashMap<>();
        sPS = new HashMap<>();
        pPS = new HashMap<>();
        pictures = new HashMap<>();

        prevPicOrderCntLsb = 0;
        prevPicOrderCntMsb = 0;
    }


    public final void addNalContext(NalUnitType type, NalUnit nalUnit)
    {
        switch (type)
        {
            case VPS_NUT:
                getVPS().put(((video_parameter_set_rbsp)nalUnit).vps_video_parameter_set_id & 0xFF, (video_parameter_set_rbsp)nalUnit);
                break;
            case SPS_NUT:
                getSPS().put(((seq_parameter_set_rbsp)nalUnit).sps_seq_parameter_set_id & 0xFF, (seq_parameter_set_rbsp)nalUnit);
                break;
            case PPS_NUT:
                getPPS().put(((pic_parameter_set_rbsp)nalUnit).pps_pic_parameter_set_id & 0xFF, (pic_parameter_set_rbsp)nalUnit);
                break;
        }
    }

    // 8.3.1 Decoding process for picture order count
    public final void decodingPictureOrderCount(HeicPicture picture)
    {
        NalHeader header = picture.NalHeader.copy();
        slice_segment_header slice_header = picture.getSliceHeaders().get(0);

        if (header.isIrapPicture() && NoRaslOutputFlag)
        {
            PicOrderCntMsb = 0;
        }
        else
        {
            int MaxPicOrderCntLsb = (int)picture.sps.getMaxPicOrderCntLsb();

            if ((slice_header.slice_pic_order_cnt_lsb < prevPicOrderCntLsb) &&
                ((prevPicOrderCntLsb - slice_header.slice_pic_order_cnt_lsb) >= (MaxPicOrderCntLsb / 2)))
            {
                PicOrderCntMsb = prevPicOrderCntMsb + MaxPicOrderCntLsb;
            }
            else if ((slice_header.slice_pic_order_cnt_lsb > prevPicOrderCntLsb) &&
                    ((slice_header.slice_pic_order_cnt_lsb - prevPicOrderCntLsb) > (MaxPicOrderCntLsb / 2)))
            {
                PicOrderCntMsb = prevPicOrderCntMsb - MaxPicOrderCntLsb;
            }
            else
            {
                PicOrderCntMsb = prevPicOrderCntMsb;
            }
        }


        picture.PicOrderCntVal = PicOrderCntMsb + slice_header.slice_pic_order_cnt_lsb;
        picture.picture_order_cnt_lsb = slice_header.slice_pic_order_cnt_lsb;

        if ((header.nuh_temporal_id_plus1 & 0xFF) == 1 &&
                !header.isSublayerNonReference() &&
                !header.isRaslPicture() &&
                !header.isRadlPicture())
        {
            prevPicOrderCntLsb = slice_header.slice_pic_order_cnt_lsb;
            prevPicOrderCntMsb = PicOrderCntMsb;
        }
    }


    // 8.6.1 Derivation process for quantization parameters
    final void derivationOfQuantizationParameters(BitStreamWithNalSupport stream, HeicPicture picture, int xCb, int yCb, int xCUBase, int yCUBase)
    {
        // Input to this process is a luma location ( xCb, yCb ) specifying the top-left sample of the current luma
        // coding block relative to the top-left luma sample of the current picture.
        // In this process, the variable QpY, the luma quantization parameter Qp′Y and
        // the chroma quantization parameters Qp′Cb and Qp′Cr are derived.

        slice_segment_header slice_header = picture.getSliceHeaders().get(picture.SliceHeaderIndex[xCb][yCb]);

        int xQg = xCb - (xCb & ((1 << (int)picture.pps.getLog2MinCuQpDeltaSize()) - 1));
        int yQg = yCb - (yCb & ((1 << (int)picture.pps.getLog2MinCuQpDeltaSize()) - 1));

        if (xQg != stream.getContext().getCurrentQgX() || yQg != stream.getContext().getCurrentQgY())
        {
            stream.getContext().setLastQpYPrev(stream.getContext().getCurrentQpY());
            stream.getContext().setCurrentQgX(xQg);
            stream.getContext().setCurrentQgY(yQg);
        }

        int qPY_PREV, qPY_PRED;

        int ctbLSBMask = ((1 << (picture.sps.getCtbLog2SizeY() & 0xFF)) - 1);
        boolean firstInCTBRow = (xQg == 0 && ((yQg & ctbLSBMask) == 0));

        /*UInt32*/long first_ctb_in_slice_RS = slice_header.getSliceAddrRs();

        /*UInt32*/long SliceStartX = ((((first_ctb_in_slice_RS & 0xFFFFFFFFL) % (picture.sps.getPicWidthInCtbsY() & 0xFFFFFFFFL)) & 0xFFFFFFFFL) * (picture.sps.getCtbSizeY() & 0xFFFFFFFFL)) & 0xFFFFFFFFL;
        /*UInt32*/long SliceStartY = ((((first_ctb_in_slice_RS & 0xFFFFFFFFL) / (picture.sps.getPicWidthInCtbsY() & 0xFFFFFFFFL)) & 0xFFFFFFFFL) * (picture.sps.getCtbSizeY() & 0xFFFFFFFFL)) & 0xFFFFFFFFL;

        boolean firstQgInSlice = ((int)SliceStartX == xQg && (int)SliceStartY == yQg);

        boolean firstQgInTile = false;

        if (picture.pps.tiles_enabled_flag)
        {
            //firstQgInTile recalculated
            if ((xQg & ctbLSBMask) == 0 && (yQg & ctbLSBMask) == 0)
            {
                int tileX = xQg >> picture.sps.getCtbLog2SizeY();
                int tileY = yQg >> picture.sps.getCtbLog2SizeY();

                firstQgInTile = picture.pps.check_if_tile_is_first(tileX, tileY);
            }
        }

        if (firstQgInSlice || firstQgInTile || (firstInCTBRow && picture.pps.entropy_coding_sync_enabled_flag))
        {
            qPY_PREV = slice_header.getSliceQPY();
        }
        else
        {
            qPY_PREV = stream.getContext().getLastQpYPrev();
        }

        int qPY_A = getNeighbouringQpY(picture, slice_header, qPY_PREV, xQg, yQg, xQg - 1, yQg);
        int qPY_B = getNeighbouringQpY(picture, slice_header, qPY_PREV, xQg, yQg, xQg, yQg - 1);

        qPY_PRED = (qPY_A + qPY_B + 1) >> 1;

        int QpY = ((qPY_PRED + stream.getContext().getCuQpDeltaVal() + 52 + 2 * (picture.sps.getQpBdOffsetY() & 0xFF)) %
            (52 + (picture.sps.getQpBdOffsetY() & 0xFF))) - (picture.sps.getQpBdOffsetY() & 0xFF);

        stream.getContext().setQpY(QpY + (picture.sps.getQpBdOffsetY() & 0xFF));
        stream.getContext().setCurrentQpY(QpY);

        int qPiCb, qPiCr, qPCb, qPCr;
        if ((picture.sps.getChromaArrayType() & 0xFFFFFFFFL) != 0)
        {
            if (!picture.tu_residual_act_flag[xCb][yCb])
            {
                qPiCb = MathExtra.clip3(-(picture.sps.getQpBdOffsetC() & 0xFF), 57,
                    QpY + picture.pps.pps_cb_qp_offset + slice_header.slice_cb_qp_offset + stream.getContext().getCuQpOffsetCb());
                qPiCr = MathExtra.clip3(-(picture.sps.getQpBdOffsetC() & 0xFF), 57,
                    QpY + picture.pps.pps_cr_qp_offset + slice_header.slice_cr_qp_offset + stream.getContext().getCuQpOffsetCr());
            }
            else
            {
                qPiCb = MathExtra.clip3(-(picture.sps.getQpBdOffsetC() & 0xFF), 57,
                    QpY + picture.pps.getPpsActQpOffsetCb() + slice_header.slice_act_cb_qp_offset + stream.getContext().getCuQpOffsetCb());
                qPiCr = MathExtra.clip3(-(picture.sps.getQpBdOffsetC() & 0xFF), 57,
                    QpY + picture.pps.getPpsActQpOffsetCr() + slice_header.slice_act_cr_qp_offset + stream.getContext().getCuQpOffsetCr());
            }

            if ((picture.sps.getChromaArrayType() & 0xFFFFFFFFL) == 1)
            {
                qPCb = getChromaQpFromQPi(qPiCb);
                qPCr = getChromaQpFromQPi(qPiCr);
            }
            else
            {
                qPCb = Math.min(qPiCb, 51);
                qPCr = Math.min(qPiCr, 51);
            }

            stream.getContext().setQpCb(qPCb + (picture.sps.getQpBdOffsetC() & 0xFF));
            stream.getContext().setQpCr(qPCr + (picture.sps.getQpBdOffsetC() & 0xFF));
        }


        int log2CbSize = picture.Log2CbSize[xCUBase][yCUBase];

        if (log2CbSize < 3)
            log2CbSize = 3;

        picture.setQpY(xCUBase, yCUBase, log2CbSize, stream.getContext().getQpY());
    }

    private int getNeighbouringQpY(HeicPicture picture, slice_segment_header slice_header, int qPY_PREV,
        int xQg, int yQg, int xQgN, int yQgN)
    {
        if (!picture.checkZScanAvailability(xQg, yQg, xQgN, yQgN))
        {
            return qPY_PREV;
        }
        else
        {
            int xTmp = xQgN >> (picture.sps.getMinTbLog2SizeY() & 0xFF);
            int yTmp = yQgN >> (picture.sps.getMinTbLog2SizeY() & 0xFF);
            int minTbAddrA = picture.pps.MinTbAddrZs[xTmp][yTmp];
            int ctbAddrA = minTbAddrA >> (2 * ((picture.sps.getCtbLog2SizeY() & 0xFF) - (picture.sps.getMinTbLog2SizeY() & 0xFF)));

            if (ctbAddrA != (slice_header.getCtbAddrInTs() & 0xFFFFFFFFL))
            {
                return qPY_PREV;
            }
            else
            {
                return picture.QpY[xQgN][yQgN];
            }
        }
    }

    private int getChromaQpFromQPi(int qPi)
    {
        if (qPi < 30)
            return qPi;
        else if (qPi > 43)
            return qPi - 6;
        else
            return qPiToQpCTable[qPi - 30];
    }

    private static final int[] qPiToQpCTable = { 29, 30, 31, 32, 33, 33, 34, 34, 35, 35, 36, 36, 37, 37 };

}

