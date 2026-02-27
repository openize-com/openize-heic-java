# openize.isobmff.AuxiliaryTypeProperty

AuxiliaryTypeProperty box includes a URN identifying the type of the auxiliary image.
AuxiliaryTypeProperty may additionally include other fields, as required by the URN.

## Methods

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**toString** | **String** | Text summary of the box. | 

## Fields

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**aux_type** | **String** | A null-terminated UTF-8 character string of the Uniform Resource Name (URN) used to identify the type of the associated auxiliary image item. | 
**aux_subtype** | **byte[]** | Zero or more bytes until the end of the box. The semantics of these bytes depend on the value of aux_type. | 

## Constructors

Name | Description | Parameters
------------ | ------------- | -------------
**AuxiliaryTypeProperty** | Create the box object from the bitstream and box size. | BitStreamReader **stream** - File stream.<br/>long **size** - Box size in bytes.

[[Back to API_README]](API_README.md)