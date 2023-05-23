package com.wutsi.blog.app.page.story.service

import com.wutsi.platform.core.image.Dimension
import com.wutsi.platform.core.image.ImageService
import com.wutsi.platform.core.image.Transformation
import com.wutsi.platform.core.storage.StorageService
import org.springframework.stereotype.Service
import java.net.URL

@Service
class HtmlImageService(private val transform: StorageService, private val imageKit: ImageService) {
    fun sizes() = ""

    fun srcset(url: String): String {
        if (url.startsWith("data") || !transform.contains(URL(url))) {
            return ""
        }

        return widths()
            .map { imageKit.transform(url, Transformation(Dimension(width = it.toInt()))) + " ${it}w" }
            .joinToString()
    }

    // See https://getbootstrap.com/docs/4.0/layout/overview/
    private fun medias() = arrayListOf("320", "640", "1024")

    private fun widths() = arrayListOf("320", "640", "1024")
}
