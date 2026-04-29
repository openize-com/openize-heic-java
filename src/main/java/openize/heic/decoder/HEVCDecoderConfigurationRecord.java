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
import openize.isobmff.ObservableCollection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class HEVCDecoderConfigurationRecord
{
    public final byte configurationVersion; // 1
    public final byte general_profile_space;
    public final boolean general_tier_flag;
    public final byte general_profile_idc;
    //public final boolean[] general_profile_compatibility_flags;
    //public final boolean[] general_constraint_indicator_flags;
    public final byte general_level_idc;
    public final /*UInt16*/ int min_spatial_segmentation_idc;
    public final byte parallelismType;
    public final byte chromaFormat;
    public final byte bitDepthLumaMinus8;
    public final byte bitDepthChromaMinus8;
    public final int avgFrameRate; // takes the value 0
    public final int constantFrameRate; // takes the value 0
    public final int numTemporalLayers; // takes the value 0
    public final int temporalIdNested; // takes the value 0
    private final boolean[] array_completeness;
    private final List<NalUnit> nalUnits;
    private ObservableCollection<NalUnit> children;
    private seq_parameter_set_rbsp spsUnit;


    public HEVCDecoderConfigurationRecord(BitStreamWithNalSupport stream)
    {
        configurationVersion = (byte) stream.read(8);

        general_profile_space = (byte) stream.read(2);
        general_tier_flag = stream.readFlag();
        general_profile_idc = (byte) stream.read(5);

        //general_profile_compatibility_flags = new boolean[32];
        for (int i = 0; i < 32; i++)
            /*general_profile_compatibility_flags[i] =*/
            stream.readFlag();

        //general_constraint_indicator_flags = new boolean[48];
        for (int i = 0; i < 48; i++)
            /*general_constraint_indicator_flags[i] =*/
            stream.readFlag();

        general_level_idc = (byte) stream.read(8);

        stream.skipBits(4); // 1111
        min_spatial_segmentation_idc = stream.read(12) & 0xFFFF;
        stream.skipBits(6); // 111111
        parallelismType = (byte) stream.read(2);
        stream.skipBits(6); // 111111
        chromaFormat = (byte) stream.read(2);
        stream.skipBits(5); // 11111
        bitDepthLumaMinus8 = (byte) stream.read(3);
        stream.skipBits(5); // 11111
        bitDepthChromaMinus8 = (byte) stream.read(3);

        avgFrameRate = stream.read(16);
        constantFrameRate = stream.read(2);
        numTemporalLayers = stream.read(3);
        temporalIdNested = stream.read(1);

        byte lengthSizeMinusOne = (byte) stream.read(2);
        if ((lengthSizeMinusOne & 0xFF) != 3)
        {
            throw new IllegalStateException();
        }

        byte numOfArrays = (byte) stream.read(8);

        nalUnits = new ArrayList<>(255);
        array_completeness = new boolean[numOfArrays & 0xFF];
        for (int j = 0; j < (numOfArrays & 0xFF); j++)
        {
            array_completeness[j] = stream.readFlag();
            stream.skipBits(1); // = 0
            NalUnitType NAL_unit_type = NalUnitType.codeToType(stream.read(6));
            /*UInt16*/
            int numNalus = stream.read(16) & 0xFFFF;

            for (int i = 0; i < numNalus; i++)
            {
                /*UInt16*/
                int nalUnitLength = stream.read(16) & 0xFFFF;
                NalUnit unit = NalUnit.parseKnownUnit(stream, nalUnitLength, NAL_unit_type);
                stream.getContext()
                      .addNalContext(NAL_unit_type, unit);
                nalUnits.add(unit);
                if (spsUnit == null && unit.NalHeader.type == NalUnitType.SPS_NUT)
                {
                    spsUnit = (seq_parameter_set_rbsp) unit;
                }
            }
        }
        setChildren(new ObservableCollection<>(nalUnits));
    }

    @Override
    public final String toString()
    {
        return "HEVCDecoderConfiguration; completeness: " + Arrays.toString(array_completeness);
    }

    public final ObservableCollection<NalUnit> getChildren()
    {
        return children;
    }

    public final void setChildren(ObservableCollection<NalUnit> value)
    {
        children = value;
    }

    final pic_parameter_set_rbsp getPPS()
    {
        for (NalUnit nalUnit : nalUnits)
        {
            if (nalUnit.NalHeader.type == NalUnitType.PPS_NUT)
            {
                return (pic_parameter_set_rbsp) nalUnit;
            }
        }
        throw new IllegalStateException();
//        return (pic_parameter_set_rbsp) nalUnits.stream().filter(i -> i.NalHeader.type == NalUnitType.PPS_NUT).findFirst()
//                .orElse(null);
    }

    final seq_parameter_set_rbsp getSPS()
    {
        if (spsUnit == null)
        {
            throw new IllegalStateException();
        }
        return spsUnit;
//        for (NalUnit nalUnit : nalUnits)
//        {
//            if (nalUnit.NalHeader.type == NalUnitType.SPS_NUT)
//            {
//                return (seq_parameter_set_rbsp) nalUnit;
//            }
//        }
//        throw new IllegalStateException();
    }

    final video_parameter_set_rbsp getVPS()
    {
        for (NalUnit nalUnit : nalUnits)
        {
            if (nalUnit.NalHeader.type == NalUnitType.VPS_NUT)
            {
                return (video_parameter_set_rbsp) nalUnit;
            }
        }
        throw new IllegalStateException();
//        return (video_parameter_set_rbsp) nalUnits.stream().filter(i -> i.NalHeader.type == NalUnitType.VPS_NUT).findFirst()
//                .orElse(null);
    }
}
