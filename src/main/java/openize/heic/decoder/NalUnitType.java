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


public enum NalUnitType
{
    // VCL

    // Coded slice segment of a non-TSA, non-STSA trailing picture
    // slice_segment_layer_rbsp( )
    TRAIL_N(0),
    TRAIL_R(1),

    //Coded slice segment of a TSA picture
    //slice_segment_layer_rbsp( )
    TSA_N(2),
    TSA_R(3),

    //Coded slice segment of an STSA picture
    //slice_segment_layer_rbsp( )
    STSA_N(4),
    STSA_R(5),

    //Coded slice segment of a RADL picture
    //slice_segment_layer_rbsp( )
    RADL_N(6),
    RADL_R(7),

    //Coded slice segment of a RASL picture
    //slice_segment_layer_rbsp( )
    RASL_N(8),
    RASL_R(9),

    //Reserved non-IRAP SLNR VCL NAL unit types VCL
    RSV_VCL_N10(10),
    RSV_VCL_N12(12),
    RSV_VCL_N14(14),

    //Reserved non-IRAP sub-layer reference VCL NAL unit types
    RSV_VCL_R11(11),
    RSV_VCL_R13(13),
    RSV_VCL_R15(15),

    //IRAP
    //Coded slice segment of a BLA picture
    //slice_segment_layer_rbsp( )
    BLA_W_LP(16),
    BLA_W_RADL(17),
    BLA_N_LP(18),

    //IRAP
    //Coded slice segment of an IDR picture
    //slice_segment_layer_rbsp( )
    IDR_W_RADL(19),
    IDR_N_LP(20),

    //IRAP
    //Coded slice segment of a CRA picture
    //slice_segment_layer_rbsp( )
    CRA_NUT(21),

    //IRAP
    //Reserved IRAP VCL NAL unit types VCL
    RSV_IRAP_VCL22(22),
    RSV_IRAP_VCL23(23),

    //Reserved non-IRAP VCL NAL unit types
    RSV_VCL24(24),
    RSV_VCL25(25),
    RSV_VCL26(26),
    RSV_VCL27(27),
    RSV_VCL28(38),
    RSV_VCL29(29),
    RSV_VCL30(30),
    RSV_VCL31(31),

    //non-VCL

    //Video parameter set
    //video_parameter_set_rbsp( )
    VPS_NUT(32),

    //Sequence parameter set
    //seq_parameter_set_rbsp( )
    SPS_NUT(33),

    //Picture parameter set
    //pic_parameter_set_rbsp( )
    PPS_NUT(34),

    //Access unit delimiter
    //access_unit_delimiter_rbsp( )
    AUD_NUT(35),

    //End of sequence
    //end_of_seq_rbsp( )
    EOS_NUT(36),

    //End of bitstream
    //end_of_bitstream_rbsp( )
    EOB_NUT(37),

    //Filler data
    //filler_data_rbsp( )
    FD_NUT(38),

    //Supplemental enhancement information
    //sei_rbsp( )
    PREFIX_SEI_NUT(39),
    SUFFIX_SEI_NUT(40),

    //Reserved non-VCL
    RSV_NVCL41(41),
    RSV_NVCL42(42),
    RSV_NVCL43(43),
    RSV_NVCL44(44),
    RSV_NVCL45(45),
    RSV_NVCL46(46),
    RSV_NVCL47(47),

    //Unspecified non-VCL
    UNSPEC48(48),
    UNSPEC49(49),
    UNSPEC50(50),
    UNSPEC51(51),
    UNSPEC52(52),
    UNSPEC53(53),
    UNSPEC54(54),
    UNSPEC55(55),
    UNSPEC56(56),
    UNSPEC57(57),
    UNSPEC58(58),
    UNSPEC59(59),
    UNSPEC60(60),
    UNSPEC61(61),
    UNSPEC62(62),
    UNSPEC63(63),

    UNSPECIFIED(255);

    private final int unitCode;

    NalUnitType(int unitCode)
    {
        this.unitCode = unitCode;
    }

    public static NalUnitType codeToType(long unitCode)
    {
        for (NalUnitType type : _values)
        {
            if (type.unitCode == unitCode)
            {
                return type;
            }
        }

        throw new IllegalArgumentException("UnitCode is incorrect " + unitCode);
    }

    public int getUnitCode()
    {
        return unitCode;
    }


    @Override
    public String toString()
    {
        return name() + "(" + unitCode + ")";
    }

    private static final NalUnitType[] _values = NalUnitType.values();
}
