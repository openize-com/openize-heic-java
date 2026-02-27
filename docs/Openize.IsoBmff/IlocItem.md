# openize.isobmff.IlocItem

Data class for organised storage on location data.

## Methods

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**toString()** | **String** | Text summary of the box. | 

## Fields

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**item_ID** | **long** | An arbitrary integer 'name' for this resource which can be used to refer to it. | 
**construction_method** | **byte** | Indicates the location of data:<br />0 means data are located in the current file in mdat box;<br />1 means data are located in the current file in idat box;<br />2 means data are located in the external file. | 
**data_reference_index** | **long** | Contains either zero (‘this file’) or a 1‐based index into the data references in the data information box. | 
**base_offset** | **long** | A base value for offset calculations within the referenced data.<br />If base_offset_size equals 0, base_offset takes the value 0, i.e. it is unused. | 
**extent_count** | **long** | Provides the count of the number of extents into which the resource is fragmented; it must have the value 1 or greater. | 
**extents** | **IlocItemExtent[]** | Array of extent data. | 

[[Back to API_README]](API_README.md)