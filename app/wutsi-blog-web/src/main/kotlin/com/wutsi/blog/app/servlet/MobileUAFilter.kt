package com.wutsi.blog.app.servlet

import au.com.flyingkite.mobiledetect.UAgentInfo
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Service
import org.springframework.web.filter.OncePerRequestFilter

@Service
class MobileUAFilter : OncePerRequestFilter() {
    companion object {
        const val ATTRIBUTE_UA_INFO = "com.wutsi.ua_info"
        const val ATTRIBUTE_UA_MOBILE = "com.wutsi.ua_mobile"
        const val ATTRIBUTE_UA_WEBVIEW = "com.wutsi.ua_web_view"
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val ua = getUAgentInfo(request)
        request.setAttribute(ATTRIBUTE_UA_MOBILE, ua.detectMobileQuick())
        request.setAttribute(ATTRIBUTE_UA_WEBVIEW, isWebview(ua, request))
        filterChain.doFilter(request, response)
    }

    private fun isWebview(ua: UAgentInfo, request: HttpServletRequest): Boolean {
        val userAgent = request.getHeader("User-Agent")

        return (ua.detectIos() && !userAgent.lowercase().contains("safari")) ||
                (ua.detectAndroid() && !userAgent.lowercase().contains("wv")) ||
                (userAgent.contains("FB_IAB")) || // Facebook In App Browser
                (userAgent.contains("FBAV")) // Facebook App Version
    }

    private fun getUAgentInfo(request: HttpServletRequest): UAgentInfo {
        var ua = request.getAttribute(ATTRIBUTE_UA_INFO) as UAgentInfo?
        if (ua == null) {
            ua = UAgentInfo(
                request.getHeader("User-Agent"),
                request.getHeader("Accept"),
            )
            request.setAttribute(ATTRIBUTE_UA_INFO, ua)
        }
        return ua
    }
}
