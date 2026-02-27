# openize.isobmff.MetaBox

A common base structure that contains general metadata.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**Children** | **ObservableCollection<Box>** | Observable collection of the nested boxes. | 
**hdlr** | **HandlerBox** | Handler box. | 
**pitm** | **PrimaryItemBox** | Primary item box. | 
**iloc** | **ItemLocationBox** | Item location box. | 
**iinf** | **ItemInfoBox** | Item info box. | 
**iprp** | **ItemPropertiesBox** | Item properties box. | 
**iref** | **ItemReferenceBox** | Item reference box. | 
**idat** | **ItemDataBox** | Item data box. | 
**grpl** | **GroupsListBox** | Item group box. | 

## Methods

Name | Type | Description | Parameters
------------ | ------------- | ------------- | -------------
**tryGetBox(BoxType type)** | **Box** | Try to get specified box. Return null if required box not available. | BoxType **type** - Box type.
**toString()** | **String** | Text summary of the box. | 

## Fields

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**boxes** | **List<Box>** | List of nested boxes. | 

## Constructors

Name | Description | Parameters
------------ | ------------- | -------------
**MetaBox** | Create the box object from the bitstream, box size and start position. | BitStreamReader **stream** - File stream.<br />long **size** - Box size in bytes.<br />long **startPos** - Start position in bits.

[[Back to API_README]](API_README.md)