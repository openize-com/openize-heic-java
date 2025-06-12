# openize.heic.decoder.HeicImageFrame

Heic image frame class.
Contains hevc coded data or meta data.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**ImageType** | **ImageFrameType** | Type of an image frame content. | 
**Width** | **long** | Width of the image frame in pixels. | 
**Height** | **long** | Height of the image frame in pixels. | 
**HasAlpha** | **boolean** | Indicates the presence of transparency of transparency layer.<br />True if frame is linked with alpha data frame, false otherwise. | 
**IsHidden** | **boolean** | Indicates the fact that frame is marked as hidden.<br />True if frame is hidden, false otherwise. | 
**IsImage** | **boolean** | Indicates the fact that frame contains image data.<br />True if frame is image, false otherwise. | 
**IsDerived** | **boolean** | Indicates the fact that frame contains image transform data and is inherited from another frame(-s).<br />True if frame is derived, false otherwise. | 
**DerivativeType** | **BoxType** | Indicates the type of derivative content if the frame is derived. | 
**AuxiliaryReferenceType** | **AuxiliaryReferenceType** | Indicates the type of auxiliary reference layer if the frame type is auxiliary. | 
**NumberOfChannels** | **byte** | Number of channels with color data. | 
**BitsPerChannel** | **byte[]** | Bits per channel with color data. | 

## Methods

Name | Type | Description | Parameters
------------ | ------------- | ------------- | -------------
**getByteArray** | **byte[]** | Get pixel data in the format of byte array.<br />Each three or four bytes (the count depends on the pixel format) refer to one pixel left to right top to bottom line by line.<br />Returns null if frame does not contain image data. In general, it equals to ***dstArray***. | **PixelFormat **<br/>***pixelFormat*** - Pixel format that defines the order of colors and the presence of alpha byte.<br />**Rectangle**<br/>***boundsRectangle*** - Bounds of the requested area.<br/>**byte[]**<br/>***dstArray*** - Byte array for storing the pixel values. If it is `null` or its length is less than necessary the new array will be allocated and returned.
**getInt32Array** | **int[]** | Get pixel data in the format of integer array.<br />Each int value refers to one pixel left to right top to bottom line by line.<br />Returns null if frame does not contain image data. In general, it equals to ***dstArray***. | **PixelFormat**<br/>***pixelFormat*** - Pixel format that defines the order of colors.<br />**Rectangle**<br/>***boundsRectangle*** - Bounds of the requested area.<br/>**int[]**<br/>***dstArray*** - Integer array for storing the argb values. If it is `null` or its length is less than necessary the new array will be allocated and returned.
**getTextData** | **String** | Get frame text data.<br />Exists only for mime frame types. | 

## Fields

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**hvcConfig** | **HEVCDecoderConfigurationRecord** | Hevc decoder configuration information from Isobmff container. | 
**rawPixels** | **int[][][]** | Raw YUV pixel data. <br />Multidimantional array: chroma or luma index, then two-dimentional array with x and y navigation. | 

[[Back to API_README]](API_README.md)