package com.wutsi.blog.story.config

import com.wutsi.blog.story.service.ViewService
import com.wutsi.blog.story.servlet.ViewFilter
import com.wutsi.platform.core.tracing.TracingContext
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ViewConfiguration(
    private val viewService: ViewService,
    private val traceContext: TracingContext,
) {
    @Bean
    fun viewFilterBean(): FilterRegistrationBean<ViewFilter> {
        val bean = FilterRegistrationBean<ViewFilter>()
        bean.filter = ViewFilter(viewService, traceContext)
        bean.addUrlPatterns("/v1/story/*")
        return bean
    }
}
