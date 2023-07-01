package com.wutsi.platform.core.image.imagekit

import com.wutsi.platform.core.image.ImageService
import com.wutsi.platform.core.image.Transformation

/**
 * Implementation of [ImageService] based on https://www.imagekit.io
 */
class ImageKitService(
    private val originUrl: String,
    private val endpoint: String,
) : ImageService {
    override fun transform(url: String, transformation: Transformation?): String {
        if (!accept(url)) {
            return url
        }

        val xurl = endpoint + url.substring(originUrl.length)
        val i = xurl.lastIndexOf('/')
        val prefix = xurl.substring(0, i)
        val suffix = xurl.substring(i)
        val tr = toString(transformation)
        return prefix + tr + suffix
    }

    private fun accept(url: String) = url.startsWith(originUrl)

    private fun toString(tx: Transformation?): String {
        val sb = StringBuilder()

        // Dimension
        if (tx?.dimension?.width != null) {
            sb.append("w-${tx.dimension.width}")
        }
        if (tx?.dimension?.height != null) {
            if (sb.isNotEmpty()) {
                sb.append(",")
            }
            sb.append("h-${tx.dimension.height}")
        }

        // Aspect ratio
        if (tx?.aspectRatio != null) {
            if (sb.isNotEmpty()) {
                sb.append(",")
            }
            sb.append("ar-${tx.aspectRatio.width}-${tx.aspectRatio.height}")
        }

        // Cropping
        val focus = tx?.focus?.let { "fo-${it.name.lowercase()}" }
        if (focus != null) {
            if (sb.isNotEmpty()) {
                sb.append(",")
            }
            sb.append(focus)
        }

        return if (sb.isEmpty()) "" else "/tr:$sb"
    }
}
