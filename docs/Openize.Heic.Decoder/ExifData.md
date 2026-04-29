# openize.heic.decoder.ExifData

Exchangeable image file format class.
Grants access to raw EXIF-data and to data by specific tags.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**DirectoriesList** | **List<com.drew.metadata.Directory>** | List of EXIF directories in frame. | 
**TagList** | **List<com.drew.metadata.Tag>** | List of all tags that present in directories. | 
**RawBytes** | **byte[]** | Raw EXIF data in bytes. | 

## Constructors

Name | Description | Parameters
------------ | ------------- | -------------
**ExifData** | Create EXIF-data object. | **BitStreamWithNalSupport** <b>stream</b> - File stream.<br />**HeicImage** <b>image</b> - Parent heic image.<br />**HeicImageFrame** <b>exifFrame</b> - EXIF HEIC frame.

## Methods

Name | Type | Description | Parameters
------------ | ------------- | ------------- | -------------
**getExifRawData<T>** | **&lt;T&gt;** | Returns the string value for the particular tag type and the directory specified as a class. | **Class&lt;T&gt;** **dirType** - The directory class.<br/> int <b>tagType</b> - The tag type identifier. | 
**getExifRawData** | **Object** | Returns the raw value for the particular tag type and the directory specified as a class. | int <b>tagType</b> - The tag type identifier. | 
**getExifString&lt;T&gt;** | **String** | Returns the string value for the particular tag type and the directory specified as a parameter. |  **Class&lt;T&gt;** **dirType** - The directory class.<br/>ExifDirectoryType <b>dirType</b> - The directory type identifier.<br />int <b>tagType</b> - The tag type identifier. | 
**getExifString** | **String** | Returns the string value for the particular tag type and the directory specified as a parameter. | ExifDirectoryType <b>dirType</b> - The directory type identifier.<br />int <b>tagType</b> - The tag type identifier. | 

[[Back to API_README]](API_README.md)