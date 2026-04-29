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

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.ErrorDirectory;
import com.drew.metadata.Tag;
import com.drew.metadata.adobe.AdobeJpegDirectory;
import com.drew.metadata.avi.AviDirectory;
import com.drew.metadata.bmp.BmpHeaderDirectory;
import com.drew.metadata.eps.EpsDirectory;
import com.drew.metadata.exif.*;
import com.drew.metadata.exif.makernotes.*;
import com.drew.metadata.file.FileTypeDirectory;
import com.drew.metadata.gif.*;
import com.drew.metadata.icc.IccDirectory;
import com.drew.metadata.ico.IcoDirectory;
import com.drew.metadata.iptc.IptcDirectory;
import com.drew.metadata.jfif.JfifDirectory;
import com.drew.metadata.jfxx.JfxxDirectory;
import com.drew.metadata.jpeg.HuffmanTablesDirectory;
import com.drew.metadata.jpeg.JpegCommentDirectory;
import com.drew.metadata.jpeg.JpegDirectory;
import com.drew.metadata.mp3.Mp3Directory;
import com.drew.metadata.pcx.PcxDirectory;
import com.drew.metadata.photoshop.DuckyDirectory;
import com.drew.metadata.photoshop.PhotoshopDirectory;
import com.drew.metadata.photoshop.PsdHeaderDirectory;
import com.drew.metadata.png.PngChromaticitiesDirectory;
import com.drew.metadata.png.PngDirectory;
import com.drew.metadata.webp.WebpDirectory;
import com.drew.metadata.xmp.XmpDirectory;
import openize.heic.decoder.io.BitStreamWithNalSupport;
import openize.isobmff.IlocItem;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


/**
 * <p>
 * Exchangeable image file format class.
 * Grants access to raw exif-data and to data by specific tags.
 * </p>
 */
public class ExifData
{
    /**
     * <p>
     * List of exif directories in frame.
     * </p>
     */
    private final List<Directory> directoriesList;

    /**
     * <p>
     * Raw exif data in bytes.
     * </p>
     */
    private final /*Byte*/ byte[] rawBytes;

    /**
     * <p>
     * Create exif-data object.
     * </p>
     *
     * @param stream    File stream.
     * @param image     Parent HEIC image.
     * @param exifFrame Exif HEIC frame.
     * @throws RuntimeException Unexpected Exif header
     */
    ExifData(BitStreamWithNalSupport stream, HeicImage image, HeicImageFrame exifFrame)
    {
        IlocItem locationBox = Arrays.stream(image.getHeader()
                                                  .getMeta()
                                                  .getiloc().items)
                                     .filter((item) -> item.item_ID == exifFrame.getID())
                                     .findFirst()
                                     .orElseThrow(() -> new RuntimeException("Unexpected Exif header"));

        stream.setBytePosition(locationBox.base_offset + locationBox.extents[0].offset);
        long end = locationBox.base_offset + locationBox.extents[0].offset + locationBox.extents[0].length;

        int offset = stream.read(32); // 0x00000006

        if (offset == 6)
        {
            int define = stream.read(32); // 0x45786966 "Exif"
            int zero = stream.read(16); // 0x0000 "\0\0"

            if (define != 0x45786966 || zero != 0x0000)
            {
                throw new RuntimeException("Unexpected Exif header");
            }
        }
        else if (offset > 0)
        {
            throw new RuntimeException("Unexpected Exif header");
        }

        this.rawBytes = new /*Byte*/byte[(int) (locationBox.extents[0].length - 4 - offset)];

        for (int i = 0; stream.getBitPosition() / 8 < end; i++)
        {
            getRawBytes()[i] = (/*Byte*/byte) stream.read(8);
        }

        try
        {
            Iterable<Directory> directories = ImageMetadataReader.readMetadata(new ByteArrayInputStream(getRawBytes()))
                                                                 .getDirectories();
            this.directoriesList = StreamSupport.stream(directories.spliterator(), false)
                                            .filter(x -> !(x instanceof FileTypeDirectory))
                                            .collect(Collectors.toList());
        }
        catch (ImageProcessingException | IOException e)
        {
            throw new openize.io.IOException(e.getMessage(), e);
        }
    }

    /**
     * <p>
     * List of exif directories in frame.
     * </p>
     */
    public final List<Directory> getDirectoriesList()
    {
        return directoriesList;
    }

    /**
     * <p>
     * List of all tags that present in directories.
     * </p>
     */
    public final List<Tag> getTagList()
    {
        return getDirectoriesList().stream()
                                   .flatMap(d -> d.getTags()
                                                  .stream())
                                   .collect(Collectors.toList());
    }

    /**
     * <p>
     * Raw exif data in bytes.
     * </p>
     */
    public final /*Byte*/byte[] getRawBytes()
    {
        return rawBytes;
    }

    /**
     * <p>
     * Returns the string value for the particular tag type and the directory specified as a class.
     * </p>
     *
     * @param <T>     The directory class
     * @param dirType The directory class
     * @param tagType The tag type identifier
     * @return The string value for the particular tag type
     */
    public final <T extends Directory> String getExifString(Class<T> dirType, int tagType)
    {
        Directory directory = getDirectoriesList().stream()
                                                  .filter(dirType::isInstance)
                                                  .findFirst()
                                                  .orElse(null);
        return (directory != null ? directory.getDescription(tagType) : null);
    }

    /**
     * <p>
     * Returns the raw value for the particular tag type and the directory specified as a class.
     * </p>
     *
     * @param <T>     The directory class
     * @param dirType The directory class
     * @param tagType The tag type identifier
     *                <p>{@code T}: The directory class</p>
     * @return The raw value for the particular tag type.
     */
    public final <T extends Directory> Object getExifRawData(Class<T> dirType, int tagType)
    {
        Directory directory = getDirectoriesList().stream()
                                                  .filter(dirType::isInstance)
                                                  .findFirst()
                                                  .orElse(null);
        return (directory != null ? directory.getObject(tagType) : null);
    }

    /**
     * <p>
     * Returns the string value for the particular tag type and the directory specified as a parameter.
     * </p>
     *
     * @param dirType The directory type identifier
     * @param tagType The tag type identifier
     * @return The string value for the particular tag type
     */
    public final String getExifString(ExifDirectoryType dirType, int tagType)
    {
        Directory directory = getExifDirectory(dirType);
        return (directory != null ? directory.getDescription(tagType) : null);
    }

    /**
     * <p>
     * Returns the raw value for the particular tag type and the directory specified as a parameter.
     * </p>
     *
     * @param dirType The directory type identifier
     * @param tagType The tag type identifier
     * @return The raw value for the particular tag type
     */
    public final Object getExifRawData(ExifDirectoryType dirType, int tagType)
    {
        Directory directory = getExifDirectory(dirType);
        return (directory != null ? directory.getObject(tagType) : null);
    }


    /**
     * <p>
     * Gets the first directory of the specified type from the exif data.
     * </p>
     *
     * @param dirType The directory type identifier
     * @return {@link Directory} object.
     * @throws UnsupportedOperationException Throws an exception for unknown directory types.
     */
    private Directory getExifDirectory(ExifDirectoryType dirType)
    {
        switch (dirType)
        {
            // ExifDirectoryBase nested:

            case ExifIfd0Directory:
                return getDirectoriesList().stream()
                                           .filter(ExifIFD0Directory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case ExifImageDirectory:
                return getDirectoriesList().stream()
                                           .filter(ExifImageDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case ExifInteropDirectory:
                return getDirectoriesList().stream()
                                           .filter(ExifInteropDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case ExifSubIfdDirectory:
                return getDirectoriesList().stream()
                                           .filter(ExifSubIFDDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case ExifThumbnailDirectory:
                return getDirectoriesList().stream()
                                           .filter(ExifThumbnailDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case GpsDirectory:
                return getDirectoriesList().stream()
                                           .filter(GpsDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);

            // Directory nested:

            case AdobeJpegDirectory:
                return getDirectoriesList().stream()
                                           .filter(AdobeJpegDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case AppleMakernoteDirectory:
                return getDirectoriesList().stream()
                                           .filter(AppleMakernoteDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case AviDirectory:
                return getDirectoriesList().stream()
                                           .filter(AviDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case BmpHeaderDirectory:
                return getDirectoriesList().stream()
                                           .filter(BmpHeaderDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case CanonMakernoteDirectory:
                return getDirectoriesList().stream()
                                           .filter(CanonMakernoteDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case CasioType1MakernoteDirectory:
                return getDirectoriesList().stream()
                                           .filter(CasioType1MakernoteDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case CasioType2MakernoteDirectory:
                return getDirectoriesList().stream()
                                           .filter(CasioType2MakernoteDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
//            case DjiMakernoteDirectory:
//                return getDirectoriesList().<DjiMakernoteDirectory>ofType().firstOrDefault();
            case DuckyDirectory:
                return getDirectoriesList().stream()
                                           .filter(DuckyDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case EpsDirectory:
                return getDirectoriesList().stream()
                                           .filter(EpsDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case ErrorDirectory:
                return getDirectoriesList().stream()
                                           .filter(ErrorDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
//            case FileMetadataDirectory:
//                return getDirectoriesList().<FileMetadataDirectory>ofType().firstOrDefault();
            case FileTypeDirectory:
                return getDirectoriesList().stream()
                                           .filter(FileTypeDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
//            case FlirCameraInfoDirectory:
//                return getDirectoriesList().<FlirCameraInfoDirectory>ofType().firstOrDefault();
//            case FlirHeaderDirectory:
//                return getDirectoriesList().<FlirHeaderDirectory>ofType().firstOrDefault();
//            case FlirMakernoteDirectory:
//                return getDirectoriesList().<FlirMakernoteDirectory>ofType().firstOrDefault();
//            case FlirRawDataDirectory:
//                return getDirectoriesList().stream().filter(GpsDirectory.class::isInstance).findFirst().orElse(null);
//                return getDirectoriesList().<FlirRawDataDirectory>ofType().firstOrDefault();
            case FujifilmMakernoteDirectory:
                return getDirectoriesList().stream()
                                           .filter(FujifilmMakernoteDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
//            case GeoTiffDirectory:
//                return getDirectoriesList().<GeoTiffDirectory>ofType().firstOrDefault();
            case GifAnimationDirectory:
                return getDirectoriesList().stream()
                                           .filter(GifAnimationDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case GifCommentDirectory:
                return getDirectoriesList().stream()
                                           .filter(GifCommentDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case GifControlDirectory:
                return getDirectoriesList().stream()
                                           .filter(GifControlDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case GifHeaderDirectory:
                return getDirectoriesList().stream()
                                           .filter(GifHeaderDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case GifImageDirectory:
                return getDirectoriesList().stream()
                                           .filter(GifImageDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
//            case HeicImagePropertiesDirectory:
//                return getDirectoriesList().<HeicImagePropertiesDirectory>ofType().firstOrDefault();
//            case HeicThumbnailDirectory:
//                return getDirectoriesList().stream().filter(GpsDirectory.class::isInstance).findFirst().orElse(null);
//                return getDirectoriesList().<HeicThumbnailDirectory>ofType().firstOrDefault();
            case HuffmanTablesDirectory:
                return getDirectoriesList().stream()
                                           .filter(HuffmanTablesDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case IccDirectory:
                return getDirectoriesList().stream()
                                           .filter(IccDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case IcoDirectory:
                return getDirectoriesList().stream()
                                           .filter(IcoDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case IptcDirectory:
                return getDirectoriesList().stream()
                                           .filter(IptcDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case JfifDirectory:
                return getDirectoriesList().stream()
                                           .filter(JfifDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case JfxxDirectory:
                return getDirectoriesList().stream()
                                           .filter(JfxxDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case JpegCommentDirectory:
                return getDirectoriesList().stream()
                                           .filter(JpegCommentDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case JpegDirectory:
                return getDirectoriesList().stream()
                                           .filter(JpegDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
//            case JpegDnlDirectory:
//                return getDirectoriesList().<JpegDnlDirectory>ofType().firstOrDefault();
            case KodakMakernoteDirectory:
                return getDirectoriesList().stream()
                                           .filter(KodakMakernoteDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case KyoceraMakernoteDirectory:
                return getDirectoriesList().stream()
                                           .filter(KyoceraMakernoteDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case LeicaMakernoteDirectory:
                return getDirectoriesList().stream()
                                           .filter(LeicaMakernoteDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case LeicaType5MakernoteDirectory:
                return getDirectoriesList().stream()
                                           .filter(LeicaType5MakernoteDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case Mp3Directory:
                return getDirectoriesList().stream()
                                           .filter(Mp3Directory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
//            case NetpbmHeaderDirectory:
//                return getDirectoriesList().<NetpbmHeaderDirectory>ofType().firstOrDefault();
            case NikonPictureControl1Directory:
                return getDirectoriesList().stream()
                                           .filter(NikonPictureControl1Directory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case NikonPictureControl2Directory:
                return getDirectoriesList().stream()
                                           .filter(NikonPictureControl2Directory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case NikonType1MakernoteDirectory:
                return getDirectoriesList().stream()
                                           .filter(NikonType1MakernoteDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case NikonType2MakernoteDirectory:
                return getDirectoriesList().stream()
                                           .filter(NikonType2MakernoteDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case OlympusCameraSettingsMakernoteDirectory:
                return getDirectoriesList().stream()
                                           .filter(OlympusCameraSettingsMakernoteDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case OlympusEquipmentMakernoteDirectory:
                return getDirectoriesList().stream()
                                           .filter(OlympusEquipmentMakernoteDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case OlympusFocusInfoMakernoteDirectory:
                return getDirectoriesList().stream()
                                           .filter(OlympusFocusInfoMakernoteDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case OlympusImageProcessingMakernoteDirectory:
                return getDirectoriesList().stream()
                                           .filter(OlympusImageProcessingMakernoteDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case OlympusMakernoteDirectory:
                return getDirectoriesList().stream()
                                           .filter(OlympusMakernoteDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case OlympusRawDevelopment2MakernoteDirectory:
                return getDirectoriesList().stream()
                                           .filter(OlympusRawDevelopment2MakernoteDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case OlympusRawDevelopmentMakernoteDirectory:
                return getDirectoriesList().stream()
                                           .filter(OlympusRawDevelopmentMakernoteDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case OlympusRawInfoMakernoteDirectory:
                return getDirectoriesList().stream()
                                           .filter(OlympusRawInfoMakernoteDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case PanasonicMakernoteDirectory:
                return getDirectoriesList().stream()
                                           .filter(PanasonicMakernoteDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case PanasonicRawDistortionDirectory:
                return getDirectoriesList().stream()
                                           .filter(PanasonicRawDistortionDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case PanasonicRawIfd0Directory:
                return getDirectoriesList().stream()
                                           .filter(PanasonicRawIFD0Directory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case PanasonicRawWbInfo2Directory:
                return getDirectoriesList().stream()
                                           .filter(PanasonicRawWbInfo2Directory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case PanasonicRawWbInfoDirectory:
                return getDirectoriesList().stream()
                                           .filter(PanasonicRawWbInfoDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case PcxDirectory:
                return getDirectoriesList().stream()
                                           .filter(PcxDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case PentaxMakernoteDirectory:
                return getDirectoriesList().stream()
                                           .filter(PentaxMakernoteDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
//            case PentaxType2MakernoteDirectory:
//                return getDirectoriesList().<PentaxType2MakernoteDirectory>ofType().firstOrDefault();
            case PhotoshopDirectory:
                return getDirectoriesList().stream()
                                           .filter(PhotoshopDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case PngChromaticitiesDirectory:
                return getDirectoriesList().stream()
                                           .filter(PngChromaticitiesDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case PngDirectory:
                return getDirectoriesList().stream()
                                           .filter(PngDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case PrintIMDirectory:
                return getDirectoriesList().stream()
                                           .filter(PrintIMDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case PsdHeaderDirectory:
                return getDirectoriesList().stream()
                                           .filter(PsdHeaderDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
//            case QuickTimeFileTypeDirectory:
//                return getDirectoriesList().<QuickTimeFileTypeDirectory>ofType().firstOrDefault();
//            case QuickTimeMetadataHeaderDirectory:
//                return getDirectoriesList().stream().filter(GpsDirectory.class::isInstance).findFirst().orElse(null);
//                return getDirectoriesList().<QuickTimeMetadataHeaderDirectory>ofType().firstOrDefault();
//            case QuickTimeMovieHeaderDirectory:
//                return getDirectoriesList().stream().filter(GpsDirectory.class::isInstance).findFirst().orElse(null);
//                return getDirectoriesList().<QuickTimeMovieHeaderDirectory>ofType().firstOrDefault();
//            case QuickTimeTrackHeaderDirectory:
//                return getDirectoriesList().stream().filter(GpsDirectory.class::isInstance).findFirst().orElse(null);
//                return getDirectoriesList().<QuickTimeTrackHeaderDirectory>ofType().firstOrDefault();
            case ReconyxHyperFire2MakernoteDirectory:
                return getDirectoriesList().stream()
                                           .filter(ReconyxHyperFire2MakernoteDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case ReconyxHyperFireMakernoteDirectory:
                return getDirectoriesList().stream()
                                           .filter(ReconyxHyperFireMakernoteDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case ReconyxUltraFireMakernoteDirectory:
                return getDirectoriesList().stream()
                                           .filter(ReconyxUltraFireMakernoteDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case RicohMakernoteDirectory:
                return getDirectoriesList().stream()
                                           .filter(RicohMakernoteDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case SamsungType2MakernoteDirectory:
                return getDirectoriesList().stream()
                                           .filter(SamsungType2MakernoteDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case SanyoMakernoteDirectory:
                return getDirectoriesList().stream()
                                           .filter(SanyoMakernoteDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case SigmaMakernoteDirectory:
                return getDirectoriesList().stream()
                                           .filter(SigmaMakernoteDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case SonyType1MakernoteDirectory:
                return getDirectoriesList().stream()
                                           .filter(SonyType1MakernoteDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case SonyType6MakernoteDirectory:
                return getDirectoriesList().stream()
                                           .filter(SonyType6MakernoteDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
//            case TgaDeveloperDirectory:
//                return getDirectoriesList().<TgaDeveloperDirectory>ofType().firstOrDefault();
//            case TgaExtensionDirectory:
//                return getDirectoriesList().stream().filter(GpsDirectory.class::isInstance).findFirst().orElse(null);
//                return getDirectoriesList().<TgaExtensionDirectory>ofType().firstOrDefault();
//            case TgaHeaderDirectory:
//                return getDirectoriesList().stream().filter(GpsDirectory.class::isInstance).findFirst().orElse(null);
//                return getDirectoriesList().<TgaHeaderDirectory>ofType().firstOrDefault();
//            case WavFactDirectory:
//                return getDirectoriesList().<WavFactDirectory>ofType().firstOrDefault();
//            case WavFormatDirectory:
//                return getDirectoriesList().stream().filter(GpsDirectory.class::isInstance).findFirst().orElse(null);
//                return getDirectoriesList().<WavFormatDirectory>ofType().firstOrDefault();
            case WebPDirectory:
                return getDirectoriesList().stream()
                                           .filter(WebpDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            case XmpDirectory:
                return getDirectoriesList().stream()
                                           .filter(XmpDirectory.class::isInstance)
                                           .findFirst()
                                           .orElse(null);
            default:
                throw new UnsupportedOperationException("Undefined Exif directory type");
        }
    }
}
