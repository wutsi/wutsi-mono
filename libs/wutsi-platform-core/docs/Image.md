# Image

This module for processing images.

## Configuration

| Property                    | Default Value | Description                                 |
|-----------------------------|---------------|---------------------------------------------|
| wutsi.platform.image.type   | `none`        | Type of image service: `image-kit`, `none`  |

### ImageKit configuration

These are the additional configurations when `wutsi.platform.image.type=image-kit`, to
use [ImageKit](https://www.imagekit.io) for processing images.

| Property                               | Default Value | Description           |
|----------------------------------------|---------------|-----------------------|
| wutsi.platform.image.type.origin-url   |               | ImageKit origin URL   |
| wutsi.platform.image.type.endpoint-url |               | ImageKit endpoint URL |
