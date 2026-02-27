# openize.isobmff.ItemPropertiesBox

The ItemPropertiesBox enables the association of any item with an ordered set of item properties.
Item properties are small data records.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**Children** | **ObservableCollection<Box>** | Observable collection of the nested boxes. | 

## Methods

Name | Type | Description | Parameters
------------ | ------------- | ------------- | -------------
**toString()** | **String** | Text summary of the box. | 
**getProperties** | **Map<Long, List<Box>>** | Returns properties in a convinient form factor.<br/>Returns dictionary with Lists of boxes that can be accessed by item id. | 

## Fields

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**property_container** | **ItemPropertyContainerBox** | Contains an implicitly indexed list of item properties. | 
**association** | **List<ItemPropertyAssociation>** | Associates items with item properties. | 

## Constructors

Name | Description | Parameters
------------ | ------------- | -------------
**ItemPropertiesBox** | Create the box object from the bitstream, box size and start position. | BitStreamReader **stream** - File stream.<br />long **size** - Box size in bytes.<br />long **startPos** - Start position in bits.

[[Back to API_README]](API_README.md)