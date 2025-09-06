# openize.heic.decoder.ImageFrameType

Type of an image frame.

## Enumeration

Name | Value | Description | Notes
------------ | ------------- | ------------- | -------------
**hvc1** | 0x68766331 | HEVC coded image frame.| 
**iden** | 0x6964656e | Identity transformation.<br />Cropping and/or rotation by 90, 180, or 270 degrees, imposed through the respective transformative properties.| 
**iovl** | 0x696f766c | Image Overlay.<br />Overlaying any number of input images in indicated order and locations onto the canvas of the output image. | 
**grid** | 0x67726964 | Image Grid.<br />Reconstructing a grid of input images of the same width and height. | 
**Exif** | 0x45786966 | Exif metadata.<br />Exchangeable image file format metadata. | 
**mime** | 0x6d696d65 | MIME metadata.<br />Resource Description Framework metadata. | 
**tmap** | 0x746d6170 | Tone map.<br />HDR metadata. | 
**uri** | 0x75726920 | Uri. | 

[[Back to API_README]](API_README.md)