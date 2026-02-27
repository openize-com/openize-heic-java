# openize.io.IOStream

Interface provides support both reading and writing to a file/memory/etc.

## Methods

Name | Type | Description | Parameters
------------ | ------------- | ------------- | -------------
**read** | **int** | Reads up to `dst`. length bytes of data into an array of bytes. This method blocks until at least one byte of input is available. | byte[] **dst** - the buffer into which the data is read.
**read** | **int** | Reads up to `dst`. length bytes of data into an array of bytes. This method blocks until at least one byte of input is available. | byte[] **dst** - the buffer into which the data is read.<br/> int **offset** - the start offset in array dst at which the data is written.<br/>int **count** - the maximum number of bytes read.
**write** | **void** | Writes `data.length` bytes from the specified byte array to this stream, starting at the current position. | byte[] **data** - the data to write.
**write** | **void** | Writes `count` bytes from the specified byte array starting at offset `offset` to this file. | byte[] **data** - the data to write.<br/> int **offset** - the start offset in the data.<br/>int **count** - the number of bytes to write.
**setPosition** | **long** | Sets the current stream position to `newPosition`. |long **newPosition** - the new stream position.
**getPosition** | **long** | Gets the current stream position. |
**seek** | **void** | Sets the stream offset, measured from the given `mode`. | long **newPosition** - the new position started from begin/current/end position according to `mode`. <br/> IOSeekMode **mode** - the seeking mode
**getLength** | **long** | Returns the length of this file. |
**setLength** | **void** | long **newLength** - the desired length of the stream. |


[[Back to API_README]](API_README.md)