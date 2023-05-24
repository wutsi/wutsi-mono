package com.wutsi.blog.app.page.story.service

import com.wutsi.platform.core.image.Dimension
import com.wutsi.platform.core.image.ImageService
import com.wutsi.platform.core.image.Transformation
import com.wutsi.platform.core.storage.StorageService
import org.springframework.stereotype.Service
import java.net.MalformedURLException
import java.net.URL

@Service
class HtmlImageService(private val storage: StorageService, private val imageKit: ImageService) {
    fun sizes() = ""

    fun srcset(url: String): String {
        try {
            if (url.startsWith("data") || !storage.contains(URL(url))) {
                return ""
            }

            return widths()
                .map { imageKit.transform(url, Transformation(Dimension(width = it.toInt()))) + " ${it}w" }
                .joinToString()
        } catch (e: MalformedURLException) {
            return ""
        }
    }

    // See https://getbootstrap.com/docs/4.0/layout/overview/
    private fun widths() = arrayListOf("320", "640", "1024")
}
