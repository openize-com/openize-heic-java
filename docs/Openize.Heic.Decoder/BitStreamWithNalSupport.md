# openize.heic.decoder.io.BitStreamWithNalSupport

The BitStreamWithNalSupport class is designed to read bits from a specified stream.
It allows to ignore specified byte sequences while reading.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**CurrentImageId** | **long** | Dictionary of images context information. | 

## Methods

Name | Type | Description | Parameters
------------ | ------------- | ------------- | -------------
**createNewImageContext** | **void** | Creates an image context object. | long **imageId** - Image identifier.
**deleteImageContext** | **void** | Deletes the image context object by id. | long **imageId** - Image identifier.
**turnOnNalUnitMode** | **void** | Turns on Nal Unit reader mode which ignores specified by standard byte sequences. | 
**turnOffNulUnitMode** | **void** | Turns off Nal Unit reader mode. | 
**read** | **int** | Reads the specified number of bits from the stream. | int **bitCount** - The required number of bits to read.
**readString** | **String** | Reads bytes as ASCII characters until '\0'. | 
**readFlag** | **boolean** | Reads one bit and returns true if it is 1, otherwise false. | 
**skipBits** | **void** | Skip the specified number of bits in the stream. | int **bitsNumber** - Number of bits to skip.
**readUev** | **long** | Read an unsigned integer 0-th order Exp-Golomb-coded syntax element with the left bit first. | 
**readSev** | **int** | Read an signed integer 0-th order Exp-Golomb-coded syntax element with the left bit first. | 

## Constructors

Name | Description | Parameters
------------ | ------------- | -------------
**BitStreamWithNalSupport** | Creates a class object with a stream object and an optional buffer size as parameters. | Stream **stream** - The source stream.<br />int **bufferSize** = 4 - The buffer size. 

[[Back to API_README]](API_README.md)