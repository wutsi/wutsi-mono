package com.wutsi.blog.story.servlet

import com.wutsi.blog.story.service.ViewService
import com.wutsi.platform.core.tracing.TracingContext
import org.slf4j.LoggerFactory
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

class ViewFilter(
    private val viewService: ViewService,
    private val traceContext: TracingContext,
) : Filter {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ViewFilter::class.java)
    }

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        chain.doFilter(request, response)
        if ((request as HttpServletRequest).method == "GET") {
            try {
                val storyId = request.servletPath.substring(10).toLong() // /v1/story/{id}
                val deviceId = traceContext.deviceId()
                viewService.add(deviceId, storyId)
            } catch (ex: Exception) {
                LOGGER.warn("Unable to extract ID from ${request.servletPath}", ex)
            }
        }
    }
}
