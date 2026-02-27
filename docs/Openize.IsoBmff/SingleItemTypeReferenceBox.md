# openize.isobmff.SingleItemTypeReferenceBox

Collects all references for one item of a specific type.

## Methods

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**toString()** | **String** | Text summary of the box. | 

## Fields

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**from_item_ID** | **long** | The ID of the item that refers to other items. | 
**reference_count** | **long** | The number of references. | 
**to_item_ID** | **long[]** | The array of the IDs of the item referred to. | 

## Constructors

Name | Description | Parameters
------------ | ------------- | -------------
**SingleItemTypeReferenceBox** | Create the box object from the bitstream and box size. | BitStreamReader **stream** - File stream.<br />long **size** - Box size in bytes.

[[Back to API_README]](API_README.md)