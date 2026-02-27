# openize.isobmff.FullBox

Structure for storing data in IsoBmff files with specified box version and flags.

## Fields

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**version** | **byte** | An integer that specifies the version of this format of the box. | 
**flags** | **BitArray** | A map of flags. | 

## Constructors

Name | Description | Parameters
------------ | ------------- | -------------
**FullBox** | Create the box object from the bitstream. | BitStreamReader **stream** - File stream.
**FullBox** | Create the box object from the bitstream and box type. | BitStreamReader **stream** - File stream.<br />BoxType **boxtype** - Box type integer.
**FullBox** | Create the box object from the bitstream, box type and size. | BitStreamReader **stream** - File stream.<br />BoxType **boxtype** - Box type integer.<br />long **size** - Box size in bytes.
**FullBox** | Create the box object from the box type, size, version and flags.<br />This constructor doesn't read data from the stream. | BoxType **boxtype** - Box type integer.<br />long **size** - Box size in bytes.<br />byte **version** - The version of this format of the box.<br />int **flags** - The map of flags.

[[Back to API_README]](API_README.md)