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



/**
 * <p>
 * Type of an exif directory.
 * </p>
 */
public enum ExifDirectoryType
{
    /**
     * <p>
     * IFD0 directory.
     * </p>
     */
    ExifIfd0Directory,

    /**
     * <p>
     * One of several Exif directories.
     * Holds information about image IFD's in a chain after the first.
     * </p>
     */
    ExifImageDirectory,

    /**
     * <p>
     * Exif interoperability tags directory.
     * </p>
     */
    ExifInteropDirectory,

    /**
     * <p>
     * SubIFD directory.
     * </p>
     */
    ExifSubIfdDirectory,

    /**
     * <p>
     * IFD1 directory. Holds information about an embedded thumbnail image.
     * </p>
     */
    ExifThumbnailDirectory,

    /**
     * <p>
     * GPS Exif tags directory.
     * </p>
     */
    GpsDirectory,

    /**
     * <p>
     * Directory for image encoding information for DCT filters, as stored by Adobe.
     * </p>
     */
    AdobeJpegDirectory,

    /**
     * <p>
     * Directory for tags specific to Apple cameras.
     * </p>
     */
    AppleMakernoteDirectory,

    /**
     * <p>
     * Directory for basic metadata for Avi files.
     * </p>
     */
    AviDirectory,

    /**
     * <p>
     * Directory for basic metadata for Bmp files.
     * </p>
     */
    BmpHeaderDirectory,

    /**
     * <p>
     * Directory for tags specific to Canon cameras.
     * </p>
     */
    CanonMakernoteDirectory,

    /**
     * <p>
     * Directory for tags specific to Casio (type 1) cameras.
     * </p>
     */
    CasioType1MakernoteDirectory,

    /**
     * <p>
     * Directory for tags specific to Casio (type 2) cameras.
     * </p>
     */
    CasioType2MakernoteDirectory,

    /**
     * <p>
     * Directory for tags specific to DJI aircraft cameras.
     * </p>
     */
    DjiMakernoteDirectory,

    /**
     * <p>
     * Directory for tags from Photoshop "ducky" segments.
     * </p>
     */
    DuckyDirectory,

    /**
     * <p>
     * Directory for basic metadata for Eps files.
     * </p>
     */
    EpsDirectory,

    /**
     * <p>
     * Directory for the reporting of errors.
     * </p>
     */
    ErrorDirectory,

    /**
     * <p>
     * Directory for OS tags.
     * </p>
     */
    FileMetadataDirectory,

    /**
     * <p>
     * Directory for tags derived from filename.
     * </p>
     */
    FileTypeDirectory,

    /**
     * <p>
     * Directory for camera info tags specific to FLIR cameras.
     * </p>
     */
    FlirCameraInfoDirectory,

    /**
     * <p>
     * Directory for header tags specific to FLIR cameras.
     * </p>
     */
    FlirHeaderDirectory,

    /**
     * <p>
     * Directory for tags specific to FLIR cameras.
     * </p>
     */
    FlirMakernoteDirectory,

    /**
     * <p>
     * Directory for raw tags specific to FLIR cameras.
     * </p>
     */
    FlirRawDataDirectory,

    /**
     * <p>
     * Directory for tags specific to Fujifilm cameras.
     * </p>
     */
    FujifilmMakernoteDirectory,

    /**
     * <p>
     * Directory for tiff geo tags.
     * </p>
     */
    GeoTiffDirectory,

    /**
     * <p>
     * Directory for animation metadata for Gif files.
     * </p>
     */
    GifAnimationDirectory,

    /**
     * <p>
     * Directory for comment metadata for Gif files.
     * </p>
     */
    GifCommentDirectory,

    /**
     * <p>
     * Directory for control metadata for Gif files.
     * </p>
     */
    GifControlDirectory,

    /**
     * <p>
     * Directory for header metadata for Gif files.
     * </p>
     */
    GifHeaderDirectory,

    /**
     * <p>
     * Directory for basic metadata for Gif files.
     * </p>
     */
    GifImageDirectory,

    /**
     * <p>
     * Directory for basic metadata for Heic files.
     * </p>
     */
    HeicImagePropertiesDirectory,

    /**
     * <p>
     * Directory for basic metadata for Heic thumbnails.
     * </p>
     */
    HeicThumbnailDirectory,

    /**
     * <p>
     * Directory of tables for the DHT (Define Huffman Table(s)) segment.
     * </p>
     */
    HuffmanTablesDirectory,

    /**
     * <p>
     * Directory for basic metadata for Icc files.
     * </p>
     */
    IccDirectory,

    /**
     * <p>
     * Directory for basic metadata for Ico files.
     * </p>
     */
    IcoDirectory,

    /**
     * <p>
     * Directory for tags used by the International Press Telecommunications Council (IPTC) metadata format.
     * </p>
     */
    IptcDirectory,

    /**
     * <p>
     * Directory for basic metadata for Jfif files.
     * </p>
     */
    JfifDirectory,

    /**
     * <p>
     * Directory for basic metadata for Jfxx files.
     * </p>
     */
    JfxxDirectory,

    /**
     * <p>
     * Directory for comment metadata for Jpeg files.
     * </p>
     */
    JpegCommentDirectory,

    /**
     * <p>
     * Directory for basic metadata for Jpeg files.
     * </p>
     */
    JpegDirectory,

    /**
     * <p>
     * Directory for DNL metadata for Jpeg files.
     * </p>
     */
    JpegDnlDirectory,

    /**
     * <p>
     * Directory for tags specific to Kodak cameras.
     * </p>
     */
    KodakMakernoteDirectory,

    /**
     * <p>
     * Directory for tags specific to Kyocera cameras.
     * </p>
     */
    KyoceraMakernoteDirectory,

    /**
     * <p>
     * Directory for tags specific to Leica cameras.
     * </p>
     */
    LeicaMakernoteDirectory,

    /**
     * <p>
     * Directory for tags specific to Leica (type 5) cameras.
     * </p>
     */
    LeicaType5MakernoteDirectory,

    /**
     * <p>
     * Directory for basic metadata for Mp3 files.
     * </p>
     */
    Mp3Directory,

    /**
     * <p>
     * Directory for header metadata for Netpbm files.
     * </p>
     */
    NetpbmHeaderDirectory,

    /**
     * <p>
     * Directory for picture control tags specific to Nikon (type 1) cameras.
     * </p>
     */
    NikonPictureControl1Directory,

    /**
     * <p>
     * Directory for picture control tags specific to Nikon (type 1) cameras.
     * </p>
     */
    NikonPictureControl2Directory,

    /**
     * <p>
     * Directory for tags specific to Nikon (type 1) cameras.
     * </p>
     */
    NikonType1MakernoteDirectory,

    /**
     * <p>
     * Directory for tags specific to Nikon (type 1) cameras.
     * </p>
     */
    NikonType2MakernoteDirectory,

    /**
     * <p>
     * Directory for camera settings tags specific to Olympus cameras (Epson, Konica, Minolta and Agfa...).
     * </p>
     */
    OlympusCameraSettingsMakernoteDirectory,

    /**
     * <p>
     * Directory for equipment tags specific to Olympus cameras (Epson, Konica, Minolta and Agfa...).
     * </p>
     */
    OlympusEquipmentMakernoteDirectory,

    /**
     * <p>
     * Directory for focus info tags specific to Olympus cameras (Epson, Konica, Minolta and Agfa...).
     * </p>
     */
    OlympusFocusInfoMakernoteDirectory,

    /**
     * <p>
     * Directory for image processing tags specific to Olympus cameras (Epson, Konica, Minolta and Agfa...).
     * </p>
     */
    OlympusImageProcessingMakernoteDirectory,

    /**
     * <p>
     * Directory for tags specific to Olympus cameras (Epson, Konica, Minolta and Agfa...).
     * </p>
     */
    OlympusMakernoteDirectory,

    /**
     * <p>
     * Directory for raw development 2 makernotes specific to Olympus cameras (Epson, Konica, Minolta and Agfa...).
     * </p>
     */
    OlympusRawDevelopment2MakernoteDirectory,

    /**
     * <p>
     * Directory for raw development 2 makernotes specific to Olympus cameras (Epson, Konica, Minolta and Agfa...).
     * </p>
     */
    OlympusRawDevelopmentMakernoteDirectory,

    /**
     * <p>
     * Directory for raw info makernotes tags specific to Olympus cameras (Epson, Konica, Minolta and Agfa...).
     * </p>
     */
    OlympusRawInfoMakernoteDirectory,

    /**
     * <p>
     * Directory for tags specific to Panasonic cameras.
     * </p>
     */
    PanasonicMakernoteDirectory,

    /**
     * <p>
     * Directory for raw distortion tags specific to Panasonic cameras.
     * </p>
     */
    PanasonicRawDistortionDirectory,

    /**
     * <p>
     * Directory for raw Ifd0 tags specific to Panasonic cameras.
     * </p>
     */
    PanasonicRawIfd0Directory,

    /**
     * <p>
     * Directory for raw info tags specific to Panasonic cameras.
     * </p>
     */
    PanasonicRawWbInfo2Directory,

    /**
     * <p>
     * Directory for raw info tags specific to Panasonic cameras.
     * </p>
     */
    PanasonicRawWbInfoDirectory,

    /**
     * <p>
     * Directory for basic metadata for Pcx files.
     * </p>
     */
    PcxDirectory,

    /**
     * <p>
     * Directory for tags specific to Pentax cameras.
     * </p>
     */
    PentaxMakernoteDirectory,

    /**
     * <p>
     * Directory for tags specific to Pentax (type 2) cameras.
     * </p>
     */
    PentaxType2MakernoteDirectory,

    /**
     * <p>
     * Directory for the metadata found in the APPD segment of a JPEG file saved by Photoshop.
     * </p>
     */
    PhotoshopDirectory,

    /**
     * <p>
     * Directory for chromaticities metadata for Png files.
     * </p>
     */
    PngChromaticitiesDirectory,

    /**
     * <p>
     * Directory for basic metadata for Png files.
     * </p>
     */
    PngDirectory,

    /**
     * <p>
     * Directory for Epson proprietary metadata.
     * </p>
     */
    PrintIMDirectory,

    /**
     * <p>
     * Directory for header metadata for Psd files.
     * </p>
     */
    PsdHeaderDirectory,

    /**
     * <p>
     * Directory for QuickTime file type metadata.
     * </p>
     */
    QuickTimeFileTypeDirectory,

    /**
     * <p>
     * Directory for QuickTime metadata header tags.
     * </p>
     */
    QuickTimeMetadataHeaderDirectory,

    /**
     * <p>
     * Directory for QuickTime movie header tags.
     * </p>
     */
    QuickTimeMovieHeaderDirectory,

    /**
     * <p>
     * Directory for QuickTime track header metadata.
     * </p>
     */
    QuickTimeTrackHeaderDirectory,

    /**
     * <p>
     * Directory for tags specific to Reconyx HyperFire 2 cameras.
     * </p>
     */
    ReconyxHyperFire2MakernoteDirectory,

    /**
     * <p>
     * Directory for tags specific to Reconyx HyperFire cameras.
     * </p>
     */
    ReconyxHyperFireMakernoteDirectory,

    /**
     * <p>
     * Directory for tags specific to Reconyx UltraFire cameras.
     * </p>
     */
    ReconyxUltraFireMakernoteDirectory,

    /**
     * <p>
     * Directory for tags specific to Ricoh cameras.
     * </p>
     */
    RicohMakernoteDirectory,

    /**
     * <p>
     * Directory for tags specific to certain 'newer' Samsung cameras.
     * </p>
     */
    SamsungType2MakernoteDirectory,

    /**
     * <p>
     * Directory for tags specific to Sanyo cameras.
     * </p>
     */
    SanyoMakernoteDirectory,

    /**
     * <p>
     * Directory for tags specific to Sigma cameras.
     * </p>
     */
    SigmaMakernoteDirectory,

    /**
     * <p>
     * Directory for tags specific to Sony (type 1) cameras.
     * </p>
     */
    SonyType1MakernoteDirectory,

    /**
     * <p>
     * Directory for tags specific to Sony (type 6) cameras.
     * </p>
     */
    SonyType6MakernoteDirectory,

    /**
     * <p>
     * Directory for developer metadata for Tga files.
     * </p>
     */
    TgaDeveloperDirectory,

    /**
     * <p>
     * Directory for extention metadata for Tga files.
     * </p>
     */
    TgaExtensionDirectory,

    /**
     * <p>
     * Directory for header metadata for Tga files.
     * </p>
     */
    TgaHeaderDirectory,

    /**
     * <p>
     * Directory for fact metadata for Wav files.
     * </p>
     */
    WavFactDirectory,

    /**
     * <p>
     * Directory for format metadata for Wav files.
     * </p>
     */
    WavFormatDirectory,

    /**
     * <p>
     * Directory for basic metadata for WebP files.
     * </p>
     */
    WebPDirectory,

    /**
     * <p>
     * Directory for basic metadata for Xmp files.
     * </p>
     */
    XmpDirectory,

    /**
     * <p>
     * Undefined directory.
     * </p>
     */
    Undefined,
}
