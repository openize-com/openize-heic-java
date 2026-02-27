# openize.isobmff.BitStreamReader

The BitStreamReader class is designed to read bits from a specified stream.
It reads a minimal amount of bytes from the stream into an intermediate buffer and then reads the bits from the buffer, returning the read value.
If there is still enough data in the buffer, the data is read from it.

## Methods

Name | Type | Description | Parameters
------------ | ------------- | ------------- | -------------
**getBitPosition()** | **long** | Gets the current position within the bitstream.<br />The bitstream position is x8 of stream position, adjusted according to the number of bits read from the latest byte. | 
**setBytePosition()** | **void** | Sets the current position within the bitstream. | long **bytePosition** - The new byte position within the bitstream.
**notByteAligned()** | **boolean** | Indicates if the current position in the bitstream is on a byte boundary.<br />Returns `false` if the current position in the bitstream is on a byte boundary, `true` otherwise. | 
**moreData()** | **boolean** | Indicates if there are more data in the bitstream.<br />True if there are more data in the bitstream, false otherwise. | 
**fillBufferFromStream()** | **int** | Fill reader buffer with data from stream.<br />Returns the tolal amount of bytes read into the buffer. | 
**read(int bitCount)** | **int** | Reads the specified number of bits from the stream.<br />Returns the integer value. | int **bitCount** - The required number of bits to read.
**readString()** | **String** | Reads bytes as ASCII characters until '\0'.<br />Returns the string value. | 
**readFlag()** | **boolean** | Reads one bit and returns true if it is 1, otherwise false.<br />Returns the boolean value. | 
**peek(int bitCount)** | **int** | Peeks the specified number of bits from the stream.<br />This method does not change the position of the underlying stream, state of the reader remains unchanged.<br />Returns the integer value. | int **bitCount** - The required number of bits to read.
**skipBits(int bitsNumber)** | **void** | Skip the specified number of bits in the stream. | int **bitCount** - Number of bits to skip.
**getBit(long position)** | **int** | Reads bit at the specified position.<br />This method does not change the position of the underlying stream, state of the reader remains unchanged.<br />This method is approximately 50% slower than the Read method. | long **position** - The position of stream in bits to read.

## Fields

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**stream** | **IOStream** | File stream. | 
**state** | **BitReaderState** | Bit reader state. | 

## Constructors

Name | Description | Parameters
------------ | ------------- | -------------
**BitStreamReader** | The constructor takes a Stream object and an optional buffer size as parameters. | Stream **stream** - The source stream.<br />int **bufferSize** - The buffer size.

[[Back to API_README]](API_README.md)