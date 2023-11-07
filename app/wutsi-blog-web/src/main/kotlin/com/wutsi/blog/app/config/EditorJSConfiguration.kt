package com.wutsi.blog.app.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.Toggles
import com.wutsi.blog.app.service.ejs.EJSFilterSet
import com.wutsi.blog.app.service.ejs.EJSInterceptorSet
import com.wutsi.blog.app.service.ejs.filter.AttachesEJSFilter
import com.wutsi.blog.app.service.ejs.filter.ButtonEJSFilter
import com.wutsi.blog.app.service.ejs.filter.DonateBannerEJSFilter
import com.wutsi.blog.app.service.ejs.filter.ImageEJSFilter
import com.wutsi.blog.app.service.ejs.filter.LinkEJSFilter
import com.wutsi.blog.app.service.ejs.filter.LinkTargetEJSFilter
import com.wutsi.blog.app.service.ejs.filter.SubscribeBannerEJSFilter
import com.wutsi.blog.app.service.ejs.interceptor.DonateEJSInterceptor
import com.wutsi.blog.app.service.ejs.interceptor.SubscribeEJSInterceptor
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
    private val toggles: Toggles,

    @Value("\${wutsi.application.server-url}") private val websiteUrl: String,
    @Value("\${wutsi.image.story.mobile.large.width}") private val mobileImageLargeWidth: Int,
    @Value("\${wutsi.image.story.desktop.large.width}") private val desktopImageLargeWidth: Int,
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
    ) = EJSFilterSet(
        arrayListOf(
            LinkTargetEJSFilter(websiteUrl),
            ImageEJSFilter(
                imageService,
                requestContext,
                desktopImageLargeWidth,
                mobileImageLargeWidth,
            ),
            ButtonEJSFilter(),
            SubscribeBannerEJSFilter(requestContext),
            DonateBannerEJSFilter(requestContext),
            AttachesEJSFilter(),
            LinkEJSFilter(websiteUrl), // IMPORTANT: Must be the last!!!
        ),
    )

    @Bean
    fun ejsInterceptorSet(): EJSInterceptorSet =
        EJSInterceptorSet(
            interceptors = listOf(
                SubscribeEJSInterceptor(requestContext),
                DonateEJSInterceptor(requestContext, toggles),
            ),
        )
}
