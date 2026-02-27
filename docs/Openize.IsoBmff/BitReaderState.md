# openize.isobmff.BitReaderState

Supporting reader structure that contains buffer with read data, position of cursor and other supporting data.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**Buffer** | **byte[]** | Buffer data read from stream. | 
**BufferActiveLength** | **int** | Buffer size in bytes. | 
**BufferPosition** | **int** | Tracks the current byte position in the buffer. | 
**BitIndex** | **int** | Tracks the current bit position in the buffer. | 
**LastStreamPosition** | **long** | Last position of the stream the data was read from. | 

## Methods

Name | Type | Description | Parameters
------------ | ------------- | -------------
**reset** | **void** | Resets buffer to empty state. | 

[[Back to API_README]](API_README.md)