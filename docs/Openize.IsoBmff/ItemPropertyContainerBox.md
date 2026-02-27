# openize.isobmff.ItemPropertyContainerBox

Contains an implicitly indexed list of item properties.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**Children** | **ObservableCollection<Box>** | Observable collection of the nested boxes. | 

## Methods

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**toString()** | **String** | Text summary of the box. | 

## Methods

Name | Type | Description | Parameters
------------ | ------------- | ------------- | -------------
**GetPropertyByIndex** | **Box** | Returns property by index. | int **id** - Property index.

## Fields

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**items** | **Map<Integer, Box>** | Dictionary of properties. | 

## Constructors

Name | Description | Parameters
------------ | ------------- | -------------
**ItemPropertyContainerBox** | Create the box object from the bitstream and start position. | BitStreamReader **stream** - File stream.<br />long **startPos** - Start position in bits.

[[Back to API_README]](API_README.md)