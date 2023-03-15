package com.wutsi.platform.core.image

interface ImageService {
    fun transform(url: String, transformation: Transformation? = null): String
}
