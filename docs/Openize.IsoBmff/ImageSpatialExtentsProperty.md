# openize.isobmff.ImageSpatialExtentsProperty

The ImageSpatialExtentsProperty documents the width and height of the associated image item.
Every image item shall be associated with one property of this type, prior to the association of all transformative properties.

## Methods

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**toString()** | **String** | Text summary of the box. | 

## Fields

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**image_width** | **long** | The width of the reconstructed image in pixels. | 
**image_height** | **long** | The height of the reconstructed image in pixels. | 

## Constructors

Name | Description | Parameters
------------ | ------------- | -------------
**ImageSpatialExtentsProperty** | Create the box object from the bitstream and box size. | BitStreamReader **stream** - File stream.<br />long **size** - Box size in bytes.

[[Back to API_README]](API_README.md)