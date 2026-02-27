# openize.isobmff.LayerSelectorProperty

If the decoding of a multi-layer image item results into more than one reconstructed image, the 'lsel' item property shall be associated with the image item.Otherwise, the 'lsel' item property shall not be associated with an image item.

This property is used to select which of the reconstructed images is described by subsequent descriptive item properties in the item property association order and manipulated by transformative item properties, if any, to generate an output image of the image item.

## Methods

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**toString()** | **String** | Text summary of the box. | 

## Fields

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**layer_id** | **int** | Specifies the layer identifier of the image among the reconstructed images that is described by subsequent descriptive item properties in the item property association order and manipulated by transformative item properties, if any, to generate an output image of the image item.The semantics of layer_id are specific to the coding format and are therefore defined for each coding format for which the decoding of a multi-layer image item can result into more than one reconstructed images. | 

## Constructors

Name | Description | Parameters
------------ | ------------- | -------------
**LayerSelectorProperty** | Create the box object from the bitstream and box size. | BitStreamReader **stream** - File stream.<br />long **size** - Box size in bytes.

[[Back to API_README]](API_README.md)