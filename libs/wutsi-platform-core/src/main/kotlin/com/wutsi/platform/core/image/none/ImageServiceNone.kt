package com.wutsi.platform.core.image.none

import com.wutsi.platform.core.image.ImageService
import com.wutsi.platform.core.image.Transformation

class ImageServiceNone : ImageService {
    override fun transform(url: String, transformation: Transformation?): String =
        url
}
