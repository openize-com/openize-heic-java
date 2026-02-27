# openize.isobmff.Box

Structure for storing data in IsoBmff files.

## Methods

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**toString()** | **String** | Text summary of the box. | 

## Static methods

Name | Type | Description | Parameters
------------ | ------------- | ------------- | -------------
**parseBox(BitStreamReader stream)** | **Box** | Read next box from stream. | BitStreamReader **stream** - File stream reader.
**setExternalConstructor(BoxType type, ExternalBoxConstructor parser)** | **void** | Add external constructor for unimplemented box type. | BoxType **type** - Box type.<br />ExternalBoxConstructor **parser** - External box constructor.
**uintToString(long value)** | **String** | Convert long value to string with ASCII coding. | long **value** - Unsigned integer.

## Fields

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**size** | **long** | An integer that specifies the number of bytes in this box, including all its fields and contained boxes; if size is 1 then the actual size is in the field largesize; if size is 0, then this box is the last one in the file, and its contents extend to the end of the file. | 
**type** | **BoxType** | Identifies the box type; standard boxes use a compact type, which is normally four printable characters, to permit ease of identification, and is shown so in the boxes below. User extensions use an extended type; in this case, the type field is set to 'uuid'. | 

## Interfaces

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**ExternalBoxConstructor** | **Box** | External box constructor for unimplemented box types. | BitStreamReader **stream** - Stream reader.<br />long **size** - Box size in bytes.

## Constructors

Name | Description | Parameters
------------ | ------------- | -------------
**Box** | Create the box object from the bitstream. | BitStreamReader **stream** - File stream.
**Box** | Create the box object from the box type and box size in bytes.<br />This constructor doesn't read data from the stream. | BoxType **boxtype** - Box type integer.<br />long **size** - Box size in bytes.
**Box** | Create the box object from the bitstream and box type. | BitStreamReader **stream** - File stream.<br />BoxType **boxtype** - Box type integer.

[[Back to API_README]](API_README.md)
