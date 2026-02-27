# openize.isobmff.ItemProtectionBox

The item protection box provides an array of item protection information, for use by the Item Information Box.

## Methods

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**toString()** | **String** | Text summary of the box. | 

## Fields

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**protection_count** | **int** | Count of protection informarion schemas. | 
**protection_information** | **ProtectionSchemeInfoBox[]** | Array of protecyion informarion schemas. | 

## Constructors

Name | Description | Parameters
------------ | ------------- | -------------
**ItemProtectionBox** | Create the box object from the bitstream and box size. | BitStreamReader **stream** - File stream.<br />long **size** - Box size in bytes.

[[Back to API_README]](API_README.md)