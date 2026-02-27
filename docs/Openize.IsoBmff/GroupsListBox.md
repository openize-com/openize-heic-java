# openize.isobmff.GroupsListBox

An entity group is a grouping of items, which may also group tracks. The entities in an entity group share a particular characteristic or have a particular relationship, as indicated by the grouping type.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**Children** | **ObservableCollection<GroupsListBox>** | Observable collection of the nested boxes. | 

## Methods

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**toString()** | **String** | Text summary of the box. | 

## Fields

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**boxes** | **List<GroupsListBox>** | List of nested boxes. | 

## Constructors

Name | Description | Parameters
------------ | ------------- | -------------
**GroupsListBox** | Create the box object from the bitstream and box size. | BitStreamReader **stream** - File stream.<br />long **size** - Box size in bytes.

[[Back to API_README]](API_README.md)