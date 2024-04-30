package com.wutsi.blog.app.service.ejs.filter

import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.ejs.EJSFilter
import com.wutsi.platform.core.image.Dimension
import com.wutsi.platform.core.image.ImageService
import com.wutsi.platform.core.image.Transformation
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class ImageEJSFilter(
    private val imageKitService: ImageService,
    private val requestContext: RequestContext,
    private val desktopThumbnailLargeWidth: Int,
    private val mobileThumbnailLargeWidth: Int,
) : EJSFilter {
    override fun filter(story: StoryModel, html: Document) {
        html.select("img")
            .forEach {
                filter(it)
            }
    }

    private fun filter(img: Element) {
        if (requestContext.isMobileUserAgent()) {
            filter(img, mobileThumbnailLargeWidth)
        } else {
            filter(img, desktopThumbnailLargeWidth)
        }
    }

    private fun filter(img: Element, maxWidth: Int) {
        val url = img.attr("src")
        val width = attrAsInt(img, "width")
        if (width > maxWidth) {
            img.attr("src", imageKitService.transform(url, Transformation(Dimension(width = maxWidth))))
            img.attr("width", maxWidth.toString())
            img.removeAttr("height")
        } else {
            img.attr("src", imageKitService.transform(url))
        }
    }

    private fun attrAsInt(elt: Element, name: String): Int {
        try {
            return elt.attr(name).toInt()
        } catch (ex: Exception) {
            return 0
        }
    }
}
