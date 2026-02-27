# openize.isobmff.ItemLocationBox

The item location box provides a directory of resources in this or other files, by locating their container, their offset within that container, and their length.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**Children** | **ObservableCollection<IlocItem>** | Observable collection of the location items. | 

## Methods

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**toString()** | **String** | Text summary of the box. | 

## Fields

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**offset_size** | **byte** | Indicates the length in bytes of the offset field. | 
**length_size** | **byte** | Indicates the length in bytes of the length field. | 
**base_offset_size** | **byte** | Indicates the length in bytes of the base_offset field. | 
**index_size** | **byte** | Indicates the length in bytes of the extent.index field. | 
**item_count** | **long** | Counts the number of items in the location item array. | 
**items** | **IlocItem[]** | Array of the location items. | 

## Constructors

Name | Description | Parameters
------------ | ------------- | -------------
**ItemLocationBox** | Create the box object from the bitstream and box size. | BitStreamReader **stream** - File stream.<br />long **size** - Box size in bytes.

[[Back to API_README]](API_README.md)