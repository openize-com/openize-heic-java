# openize.isobmff.EntityToGroupBox

The EntityToGroupBox specifies an entity group.

## Methods

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**toString()** | **String** | Text summary of the box. | 

## Fields

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**group_id** | **long** | A non-negative integer assigned to the particular grouping that shall not be equal to any group_id value of any other EntityToGroupBox, any item_ID value of the hierarchy level(file, movie. or track) that contains the GroupsListBox, or any track_ID value(when the GroupsListBox is contained in the file level). | 
**group_id** | **long** | The number of entity_id values mapped to this entity group. | 
**entities** | **long[]** | Array of identificators of items that are present in the hierarchy level(file, movie or track) that contains the GroupsListBox, or to a track, when a track with track_ID equal to entity_id is present and the GroupsListBox is contained in the file level. | 

## Constructors

Name | Description | Parameters
------------ | ------------- | -------------
**EntityToGroupBox** | Create the box object from the bitstream. | BitStreamReader **stream** - File stream.

[[Back to API_README]](API_README.md)