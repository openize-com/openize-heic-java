# openize.isobmff.DataInformationBox

The data information box contains objects that declare the location of the media information in a track.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**Children** | **ObservableCollection<Box>** | Observable collection of the nested boxes. | 

## Methods
**toString()** | **String** | Text summary of the box. | 

## Fields

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**dref** | **DataReferenceBox** | The data reference object contains a table of data references (normally URLs) that declare the location(s) of the media data used within the presentation. | 

## Constructors

Name | Description | Parameters
------------ | ------------- | -------------
**DataInformationBox** | Create the box object from the bitstream and box size. | BitStreamReader **stream** - File stream.<br />long **size** - Box size in bytes.

[[Back to API_README]](API_README.md)