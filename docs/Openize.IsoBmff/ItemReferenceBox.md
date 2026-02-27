# openize.isobmff.ItemReferenceBox

Contains all the linking of one item to others via typed references.
All references for one item of a specific type are collected into a single item type reference box.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**Children** | **ObservableCollection<SingleItemTypeReferenceBox>** | Observable collection of the references. | 

## Methods

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**toString()** | **String** | Text summary of the box. | 

## Fields

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**references** | **List<SingleItemTypeReferenceBox>** | List of references. | 

## Constructors

Name | Description | Parameters
------------ | ------------- | -------------
**ItemReferenceBox** | Create the box object from the bitstream, box size and start position. | BitStreamReader **stream** - File stream.<br />long **size** - Box size in bytes.<br />long **startPos** - Start position in bits.

[[Back to API_README]](API_README.md)