# openize.isobmff.ItemDataBox

This box contains the data of metadata items that use the construction method indicating that an item’s data extents are stored within this box.

## Methods

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**toString()** | **String** | Text summary of the box. | 

## Fields

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**data** | **byte[]** | The contained meta data in raw format. | 

## Constructors

Name | Description | Parameters
------------ | ------------- | -------------
**ItemDataBox** | Create the box object from the bitstream, box size and start position. | BitStreamReader **stream** - File stream.<br />long **size** - Box size in bytes.<br />long **startPos** - Start position in bits.

[[Back to API_README]](API_README.md)