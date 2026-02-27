# openize.isobmff.ItemPropertyEntryAssociation

Item property association entry data.

## Methods

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**toString()** | **String** | Text summary of the box. | 

## Fields

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**essential** | **boolean** | Set to 1 indicates that the associated property is essential to the item, otherwise it is non-essential. | 
**property_index** | **int** | Is either 0 indicating that no property is associated (the essential indicator shall also be 0), or is the 1-based index of the associated property box in the ItemPropertyContainerBox contained in the same ItemPropertiesBox. | 

[[Back to API_README]](API_README.md)