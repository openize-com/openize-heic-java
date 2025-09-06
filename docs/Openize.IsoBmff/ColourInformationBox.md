# openize.isobmff.ColourInformationBox

Contains colour information about the image.
If colour information is supplied in both this box, and also in the video bitstream, this box takes precedence, and over‐rides the information in the bitstream.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**ColourType** | **String** | Color type code as a string. | 

## Methods

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**toString()** | **String** | Text summary of the box. | 

## Fields

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**colour_type** | **long** | An indication of the type of colour information supplied. | 
**colour_primaries** | **int** | Indicates the chromaticity coordinates of the source primaries. | 
**transfer_characteristics** | **int** | Indicates the reference opto-electronic transfer characteristic. | 
**matrix_coefficients** | **int** | Describes the matrix coefficients used in deriving luma and chroma signals from the green, blue, and red. | 
**full_range_flag** | **boolean** | Indicates the black level and range of the luma and chroma signals as derived from E′Y, E′PB, and E′PR or E′R, E′G, and E′B real-valued component signals. | 

## Constructors

Name | Description | Parameters
------------ | ------------- | -------------
**ColourInformationBox** | Create the box object from the bitstream and box size. | BitStreamReader **stream** - File stream.<br />long **size** - Box size in bytes.

[[Back to API_README]](API_README.md)