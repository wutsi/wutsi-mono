package com.wutsi.blog.app.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.ejs.EJSEJSFilterSet
import com.wutsi.blog.app.service.ejs.filter.ButtonEJSFilter
import com.wutsi.blog.app.service.ejs.filter.ImageEJSFilter
import com.wutsi.blog.app.service.ejs.filter.LinkTargetEJSFilter
import com.wutsi.editorjs.html.EJSHtmlReader
import com.wutsi.editorjs.html.EJSHtmlWriter
import com.wutsi.editorjs.html.tag.TagProvider
import com.wutsi.editorjs.json.EJSJsonReader
import com.wutsi.editorjs.json.EJSJsonWriter
import com.wutsi.platform.core.image.ImageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class EditorJSConfiguration(
    private val objectMapper: ObjectMapper,
    private val requestContext: RequestContext,
    @Value("\${wutsi.application.server-url}") private val websiteUrl: String,
    @Value("\${wutsi.image.story.mobile.large.width}") private val mobileThumbnailLargeWidth: Int,
    @Value("\${wutsi.image.story.desktop.large.width}") private val desktopThumbnailLargeWidth: Int,

) {
    @Bean
    fun htmlWriter() = EJSHtmlWriter(tagProvider())

    @Bean
    fun htmlReader() = EJSHtmlReader(tagProvider())

    @Bean
    fun jsonReader() = EJSJsonReader(objectMapper)

    @Bean
    fun jsonWriter() = EJSJsonWriter(objectMapper)

    @Bean
    fun tagProvider() = TagProvider()

    @Autowired
    @Bean
    fun ejsFilterSet(
        imageService: ImageService,
        requestContext: RequestContext,
    ) = EJSEJSFilterSet(
        arrayListOf(
            LinkTargetEJSFilter(websiteUrl),
            ImageEJSFilter(
                imageService,
                requestContext,
                desktopThumbnailLargeWidth,
                mobileThumbnailLargeWidth,
            ),
            ButtonEJSFilter(),
        ),
    )
}
