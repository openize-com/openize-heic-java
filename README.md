# Openize.HEIC for Java

![Openize.HEIC](publish/openize_heic_java.png)

Openize.HEIC is an open source SDK implementing the ISO/IEC 23008-12:2017 HEIF file format decoder.

It is written from scratch and has a plain Java API to enable a simple integration into other software.

## Supported features

Openize.HEIC has support for:
* HEIC coded static images;
  * I slices;
  * 4:2:0, 4:2:2 and 4:4:4 chroma subsampling.
* HEIC coded animations that use several I slices;
* multiple images in a file;
* alpha channels, depth maps, thumbnails, auxiliary images;
* correct color transform according to embedded color profiles;
* image transformations (crop, mirror, rotate), overlay images.

Openize.HEIC doesn't support:
* HDR images;
* reading EXIF and XMP metadata;
* color transform according to EXIF contained color profiles;
* HEIC coded animations that use P and B slices;
* deblocking filter.

## Usage examples

### Read .heic file to int array with argb32 data

``` java
try (IOFileStream fs = new IOFileStream("filename.heic", IOMode.READ))
{
    HeicImage image = HeicImage.load(fs);
    int[] pixels = image.getInt32Array(openize.heic.decoder.PixelFormat.Argb32);
}
```

### Convert .heic file to .jpg using Java ImageIO

``` java
try (IOFileStream fs = new IOFileStream("filename.heic", IOMode.READ))
{
    HeicImage image = HeicImage.load(fs);
     
    var width = (int)image.Width;
    var height = (int)image.Height;	
	BufferedImage image2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

	final int[] dstArray = ((DataBufferInt) image2.getRaster()
                                                  .getDataBuffer()).getData();

    int[] pixels = frames.get(key).getInt32Array(PixelFormat.Argb32, dstArray);

	
	if (pixels != dstArray)
	{
		image2.setRGB(0, 0, width, height, pixels, 0, width);
	}
	
	ImageIO.write(image2, "JPEG", new File("output.jpg"));
}
```

### Convert .heic file to .png using Java ImageIO
``` java
try (IOFileStream fs = new IOFileStream("filename.heic", IOMode.READ))
{
    HeicImage image = HeicImage.load(fs);
     
    var width = (int)image.Width;
    var height = (int)image.Height;
	
	BufferedImage image2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

	final int[] dstArray = ((DataBufferInt) image2.getRaster()
                                                  .getDataBuffer()).getData();

    int[] pixels = frames.get(key).getInt32Array(PixelFormat.Argb32, dstArray);

	if (pixels != dstArray)
	{
		image2.setRGB(0, 0, width, height, pixels, 0, width);
	}
		
	ImageIO.write(image2, "PNG", new File("output.png"));
}
```

### Convert .heic file to .png using Java ImageIO
``` java
try (IOFileStream fs = new IOFileStream("filename.heic", IOMode.READ))
{
    HeicImage image = HeicImage.load(fs);

    int width = (int)image.getWidth();
    int height = (int)image.getHeight();

	BufferedImage outImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	final int[] dstArray = ((DataBufferInt) outImage.getRaster()
                                                  .getDataBuffer()).getData();
	int[] pixels = image.getInt32Array(PixelFormat.Argb32, dstArray);

	if (pixels != dstArray)
	{
		outImage.setRGB(0, 0, width, height, pixels, 0, width);
	}
	ImageIO.write(outImage, "PNG", new File("output.png"));
}
```

### Convert .heic collection to a set of .png files
``` java
try (IOFileStream fs = new IOFileStream("filename.heic", IOMode.READ))
{
	HeicImage image = HeicImage.load(fs);

	final Map<Long, HeicImageFrame> frames = image.getFrames();
	for (Long key : frames.keySet())
	{
		int width = (int)frames.get(key).getWidth();
		int height = (int)frames.get(key).getHeight();
		
		BufferedImage image2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		final int[] dstArray = ((DataBufferInt) image2.getRaster()
                                                  .getDataBuffer()).getData();
												  
		int[] pixels = frames.get(key).getInt32Array(PixelFormat.Argb32, dstArray);
		
		if (pixels != dstArray)
		{
			image2.setRGB(0, 0, width, height, pixels, 0, width);
		}
		
		ImageIO.write(image2, "PNG", new File("output"+key+".png"));
	}
}
```

## Documentation

All public classes, methods and properties are documented in corresponding API_README:
* [/Openize.Heic.Decoder/docs/API_README.md](/docs/Openize.Heic.Decoder/API_README.md) for Openize.Heic.Decoder;
* [/Openize.IsoBmff/docs/API_README.md](/docs/Openize.IsoBmff/API_README.md) for Openize.IsoBmff.

### HeicImage

#### Methods
Name | Type | Description | Parameters | Notes
------------ | ------------- | ------------- | ------------- | -------------
**load** | **HeicImage** | Reads the file meta data and creates a class object for further decoding of the file contents. | `Stream stream` - File stream. | This operation does not decode pixels. Use the default frame methods GetByteArray or GetInt32Array afterwards in order to decode pixels.
**canLoad** | **boolean** | Checks if the stream can be read as a heic image. Returns true if file header contains heic signarure, false otherwise | `Stream stream` - File stream. | 
**getByteArray** | **byte[]** | Get pixel data of the default image frame in the format of byte array.<br />Each three or four bytes (the count depends on the pixel format) refer to one pixel left to right top to bottom line by line.<br />Returns null if frame does not contain image data. In general, it equals to `dstArray`. | `PixelFormat pixelFormat` - Pixel format that defines the order of colors and the presence of alpha byte.<br />`Rectangle boundsRectangle` - Bounds of the requested area.<br/>`byte[] dstArray` - Byte array for storing the pixel values. If it is `null` or its length is less than necessary the new array will be allocated and returned.
**getInt32Array** | **int[]** | Get pixel data of the default image frame in the format of integer array.<br />Each int value refers to one pixel left to right top to bottom line by line.<br />Returns null if frame does not contain image data. In general, it equals to `dstArray`. | `PixelFormat pixelFormat` - Pixel format that defines the order of colors.<br />`Rectangle boundsRectangle` - Bounds of the requested area.<br/>`int[] dstArray` - Integer array for storing the argb values. If it is `null` or its length is less than necessary the new array will be allocated and returned.


#### Properties
Name | Type | Description
------------ | ------------- | ------------- 
**Frames** | **Map<Long, HeicImageFrame>** | Dictionary of public Heic image frames with access by identifier. 
**AllFrames** | **Map<Long, HeicImageFrame>** | Dictionary of all Heic image frames with access by identifier. 
**DefaultFrame** | **HeicImageFrame** | Returns the default image frame, which is specified in meta data. 

### HeicImageFrame

#### Methods
Name | Type | Description | Parameters
------------ | ------------- | ------------- | -------------
**getByteArray** | **byte[]** | Get pixel data in the format of byte array. Each three or four bytes (the count depends on the pixel format) refer to one pixel left to right top to bottom line by line. In general, it equals to `dstArray`. | `PixelFormat pixelFormat` - Pixel format that defines the order of colors and the presence of alpha byte. `Rectangle boundsRectangle` - Bounds of the requested area.<br/>`byte[] dstArray` - Byte array for storing the pixel values. If it is `null` or its length is less than necessary the new array will be allocated and returned.
**getInt32Array** | **int[]** | Get pixel data in the format of integer array. Each int value refers to one pixel left to right top to bottom line by line. In general, it equals to `dstArray`. | `PixelFormat pixelFormat` - Pixel format that defines the order of colors. `Rectangle boundsRectangle` - Bounds of the requested area.<br/>`int[] dstArray` - Integer array for storing the argb values. If it is `null` or its length is less than necessary the new array will be allocated and returned.
**getTextData** | **String** | Get frame text data. Exists only for mime frame types. | 

### Properties
Name | Type | Description
------------ | ------------- | ------------- 
**ImageType** | **ImageFrameType** | Type of an image frame content.
**Width** | **long** | Width of the image frame in pixels. 
**Height** | **long** | Height of the image frame in pixels.
**HasAlpha** | **boolean** | Indicates the presence of transparency of transparency layer. True if frame is linked with alpha data frame, false otherwise.
**IsHidden** | **boolean** | Indicates the fact that frame is marked as hidden. True if frame is hidden, false otherwise.
**IsImage** | **boolean** | Indicates the fact that frame contains image data. True if frame is image, false otherwise.
**IsDerived** | **boolean** | Indicates the fact that frame contains image transform data and is inherited from another frame(-s). True if frame is derived, false otherwise.
**DerivativeType** | **BoxType** | Indicates the type of derivative content if the frame is derived.
**AuxiliaryReferenceType** | **AuxiliaryReferenceType** | Indicates the type of auxiliary reference layer if the frame type is auxiliary.
**NumberOfChannels** | **byte** | Number of channels with color data.
**BitsPerChannel** | **byte[]** | Bits per channel with color data.

## License
Openize.HEIC is available under [Openize License](https://github.com/openize-heic/Openize.HEIC-for-Java/blob/main/LICENSE).
> [!CAUTION]
> Openize does not and cannot grant You a patent license for the utilization of HEVC/H.265 image compression/decompression technologies.

Openize.HEIC uses Openize.IsoBmff that is distributed under [MIT License](https://github.com/openize-heic/Openize.HEIC-for-Java/blob/main/licenses/Openize.IsoBmff/LICENSE).

## OSS Notice
Sample files used for tests and located in the "https://github.com/openize-heic/Openize.HEIC-for-Java/blob/main/Openize.Heic.Tests/TestsData/samples/nokia" folder belong to Nokia Technologies and are used according to [Nokia High-Efficiency Image File Format (HEIF) License](https://github.com/nokiatech/heif/blob/master/LICENSE.TXT)

> Licensed Field means the non-commercial purposes of evaluation, testing and academic research in each non-commercial case to use, run, modify (in a way that still complies with the Specification) and copy the Software to (a) generate, using one or more encoded pictures as inputs, a file complying with the Specification and including the one or more encoded pictures that were given as inputs; and/or (b) read a file complying with the Specification, resulting into one or more encoded pictures included in the file as outputs.
