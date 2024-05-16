package com.wutsi.blog.app.servlet

import com.wutsi.blog.app.util.CookieHelper
import com.wutsi.blog.app.util.CookieName
import com.wutsi.platform.core.tracking.ChannelDetector
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.filter.OncePerRequestFilter

@Service
class ChannelFilter(
    private val detector: ChannelDetector,
    @Value("\${wutsi.application.server-url}") private val serverUrl: String,
) : OncePerRequestFilter() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ChannelFilter::class.java)
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val referer = request.getHeader("Referer")
        if (referer.isNullOrEmpty()) {
            CookieHelper.remove(CookieName.CHANNEL, response)
        } else if (isExternal(referer)) {
            val ua = request.getHeader("User-Agent")
            val url = request.requestURL?.toString() ?: ""
            val channel = detector.detect(url, referer, ua)
            CookieHelper.put(CookieName.CHANNEL, channel.name, request, response)
        }
        filterChain.doFilter(request, response)
    }

    private fun isExternal(referer: String): Boolean =
        try {
            extractDomain(referer) != extractDomain(serverUrl)
        } catch (ex: Exception) {
            LOGGER.warn("Unexpected error. referer=$referer", ex)
            true
        }

    private fun extractDomain(url: String): String {
        var domainName: String = url
        var index: Int = url.indexOf("://")
        if (index != -1) {
            // keep everything after the "://"
            domainName = domainName.substring(index + 3)
        }

        index = domainName.indexOf('/')
        if (index != -1) {
            // keep everything before the '/'
            domainName = domainName.substring(0, index)
        }

        return domainName.replaceFirst("www.", "")
    }
}
