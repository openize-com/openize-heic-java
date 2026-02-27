# openize.isobmff.ItemInfoBox

The item information box provides extra information about file entries.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**Children** | **ObservableCollection<ItemInfoEntry>** | Observable collection of entries of extra information. | 

## Methods

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**toString()** | **String** | Text summary of the box. | 

## Fields

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**entry_count** | **long** | A count of the number of entries in the info entry array. | 
**item_infos** | **ItemInfoEntry[]** | Array of entries of extra information, each entry is formatted as a box.<br />This array is sorted by increasing item_ID in the entry records. | 

## Constructors

Name | Description | Parameters
------------ | ------------- | -------------
**ItemInfoBox** | Create the box object from the bitstream and box size. | BitStreamReader **stream** - File stream.<br />long **size** - Box size in bytes.

[[Back to API_README]](API_README.md)