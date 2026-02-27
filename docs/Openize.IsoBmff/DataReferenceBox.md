# openize.isobmff.DataReferenceBox

The data reference object contains a table of data references (normally URLs) that declare the location(s) of the media data used within the presentation.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**Children** | **ObservableCollection<Box>** | Observable collection of the nested boxes. | 

## Methods
**toString()** | **String** | Text summary of the box. | 

## Fields

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**entry_count** | **long** | The count of data references. | 
**entries** | **List<DataEntryUrlBox>** | The list of data references. | 

## Constructors

Name | Description | Parameters
------------ | ------------- | -------------
**DataReferenceBox** | Create the box object from the bitstream. | BitStreamReader **stream** - File stream.

[[Back to API_README]](API_README.md)