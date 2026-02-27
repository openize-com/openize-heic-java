# openize.isobmff.PixelInformationProperty

The PixelInformationProperty descriptive item property indicates the number and bit depth of colour components in the reconstructed image of the associated image item.

## Methods

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**toString()** | **String** | Text summary of the box. | 

## Fields

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**num_channels** | **byte** | This field signals the number of channels by each pixel of the reconstructed image ofthe associated image item. | 
**bits_per_channel** | **byte[]** | This field indicates the bits per channel for the pixels of the reconstructed image of the associated image item. | 

## Constructors

Name | Description | Parameters
------------ | ------------- | -------------
**PixelInformationProperty** | Create the box object from the bitstream and box size. | BitStreamReader **stream** - File stream.<br />long **size** - Box size in bytes.

[[Back to API_README]](API_README.md)