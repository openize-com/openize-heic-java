# openize.isobmff.DataEntryUrlBox

Data reference URL that declare the location of the media data used within the presentation.
The data reference index in the sample description ties entries in this table to the samples in the track.
A track may be split over several sources in this way.

## Methods

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**toString()** | **String** | Text summary of the box. | 

## Fields

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**name** | **String** | Name of the entry. | 
**location** | **String** | Location of the entry. | 

## Constructors

Name | Description | Parameters
------------ | ------------- | -------------
**DataEntryUrlBox** | Create the box object from the bitstream and box size. | BitStreamReader **stream** - File stream.<br />long **size** - Box size in bytes.

[[Back to API_README]](API_README.md)