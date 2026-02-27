# openize.isobmff.FileTypeBox

Type of the container box.
Used definitions from ISO IEC 14496 Part 12, ISO IEC 14496 Part 15 and ISO IEC 23008 Part 12.

## Enumeration

Name | Value | Description | Location
------------ | ------------- | ------------- | -------------
**ftyp** | 0x66747970 | File type and compatibility. | Located in the root.
**mdat** | 0x6d646174 | Media data container. | Located in the root
**free** | 0x66726565 | Free space. | 
**skip** | 0x736b6970 | Free space. | 
**moov** | 0x6d6f6f76 | Container for all the movie metadata. | Located in the root.
**meta** | 0x6d657461 | Metadata. | Located in the root.
**hdlr** | 0x68646c72 | Handler, declares the metadata (handler) type. | Located in the 'meta' box.
**dinf** | 0x64696e66 | Data information box, container. | Located in the 'meta' box.
**dref** | 0x64726566 | Data reference box, declares source(s) of items. | Located in the 'dinf' box.
**url** | 0x75726c20 | Data reference box, declares source(s) of metadata items. | 
**urn** | 0x75726e20 | Data reference box, declares source(s) of metadata items. | 
**iloc** | 0x696c6f63 | Item location. | Located in the 'meta' box.
**idat** | 0x69646174 | Item data. | Located in the 'meta' box.
**iinf** | 0x69696e66 | Item information. | Located in the 'meta' box.
**infe** | 0x696e6665 | Item information entry. | Located in the 'iinf' box.
**iref** | 0x69726566 | Item reference box. | Located in the 'meta' box.
**dimg** | 0x64696d67 | Derived image item. | Located in the 'iref' box.
**auxl** | 0x6175786c | Auxiliary media for the indicated track (e.g. depth map or alpha plane for video). | Located in the 'iref' box.
**thmb** | 0x74686d62 | Thumbnails for the referenced track. | Located in the 'iref' box.
**pitm** | 0x7069746d | Primary item reference. | Located in the 'meta' box.
**iprp** | 0x69707270 | Item properties. | Located in the 'meta' box.
**ipma** | 0x69706d61 | Item property association. | Located in the 'iprp' box.
**ipco** | 0x6970636f | List of item properties. | Located in the 'iprp' box.
**av1C** | 0x61763143 | AV1 coded item property. | Located in the 'ipco' box.
**hvcC** | 0x68766343 | HEVC coded item property. | Located in the 'ipco' box.
**ispe** | 0x69737065 | Width and height item property. | Located in the 'ipco' box.
**pasp** | 0x70617370 | Pixel aspect ratio item property. | Located in the 'ipco' box.
**colr** | 0x636f6c72 | Colour information item property. | Located in the 'ipco' box.
**clli** | 0x636c6c69 | Content light level info. | Located in the 'clli' box.
**pixi** | 0x70697869 | Bit depth item property. | Located in the 'ipco' box.
**rloc** | 0x726c6f63 | Position item property. | Located in the 'ipco' box.
**auxC** | 0x61757843 | Auxiliary images association item property. | Located in the 'ipco' box.
**clap** | 0x636c6170 | Transformation item property. | Located in the 'ipco' box.
**irot** | 0x69726f74 | Rotation item property. | Located in the 'ipco' box.
**lsel** | 0x6c73656c | Multi-layer item property. | Located in the 'ipco' box.
**imir** | 0x696d6972 | Mirroring item property. | Located in the 'ipco' box.
**oinf** | 0x6f696e66 | Operation points information item property. | Located in the 'ipco' box.
**udes** | 0x75646573 | User description item property. | Located in the 'ipco' box.
**cdsc** | 0x63647363 | Content describtion item property. | Located in the 'ipco' box.
**grpl** | 0x6772706c | Grouping property box. | Located in the 'meta' box.
**altr** | 0x616c7472 | Alternatives entity group. | Located in the 'grpl' box.
**ster** | 0x73746572 | Stereo pair entity group. | Located in the 'grpl' box.
**tsyn** | 0x7473796e | A time-synchronized capture entity group. | Located in the 'grpl' box.
**ipro** | 0x6970726f | Item protection. | Located in the root.
**sinf** | 0x73696e66 | Protection scheme information box. | Located in the 'ipro' box.
**frma** | 0x66726d61 | Original format box. | Located in the 'sinf' box.
**schm** | 0x7363686d | Scheme type box. | Located in the 'sinf' box.
**schi** | 0x73636869 | Scheme information box. | Located in the 'sinf' box.
**uuid** | 0x75756964 | Extended type. | 

[[Back to API_README]](API_README.md)