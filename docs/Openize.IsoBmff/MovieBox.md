# openize.isobmff.MovieBox

The metadata for a presentation is stored in the single Movie Box which occurs at the top‐level of a file. 

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**Children** | **ObservableCollection<Box>** | Observable collection of the nested boxes. | 

## Methods

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**toString()** | **String** | Text summary of the box. | 

## Fields

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**boxes** | **List<Box>** | List of nested boxes. | 

## Constructors

Name | Description | Parameters
------------ | ------------- | -------------
**MovieBox** | Create the box object from the bitstream, box size and start position. | BitStreamReader **stream** - File stream.<br />long **size** - Box size in bytes.<br />long **startPos** - Start position in bits.

[[Back to API_README]](API_README.md)