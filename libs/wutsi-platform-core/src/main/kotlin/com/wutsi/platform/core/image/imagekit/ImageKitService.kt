package com.wutsi.platform.core.image.imagekit

import com.wutsi.platform.core.image.Format
import com.wutsi.platform.core.image.ImageService
import com.wutsi.platform.core.image.OverlayType
import com.wutsi.platform.core.image.Transformation
import java.net.URLEncoder

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
        val sb = mutableListOf<String>()

        // Dimension
        if (tx?.dimension?.width != null) {
            sb.add("w-${tx.dimension.width}")
        }
        if (tx?.dimension?.height != null) {
            sb.add("h-${tx.dimension.height}")
        }

        // Aspect ratio
        if (tx?.aspectRatio != null) {
            sb.add("ar-${tx.aspectRatio.width}-${tx.aspectRatio.height}")
        }

        // Cropping
        val focus = tx?.focus?.let { "fo-${it.name.lowercase()}" }
        if (focus != null) {
            sb.add(focus)
        }

        // Format
        val format = when (tx?.format) {
            Format.JPG -> "f-jpg"
            Format.PNG -> "f-png"
            Format.GIF -> "f-gif"
            else -> null
        }
        if (format != null) {
            sb.add(format)
        }

        // Layer
        val overlayType = when (tx?.overlay?.type) {
            OverlayType.TEXT -> "l-text"
            OverlayType.IMAGE -> "l-image"
            else -> null
        }
        if (overlayType != null && tx?.overlay?.input != null) {
            sb.add(overlayType)
            sb.add("i-" + URLEncoder.encode(tx.overlay.input, "utf-8"))
            if (tx.overlay.dimension?.width != null) {
                sb.add("w-${tx.overlay.dimension.width}")
            }
            if (tx.overlay.dimension?.height != null) {
                sb.add("h-${tx.overlay.dimension.height}")
            }
            sb.add("l-end")
        }

        return if (sb.isEmpty()) "" else "/tr:" + sb.joinToString(separator = ",")
    }
}
