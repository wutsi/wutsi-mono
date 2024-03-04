package com.wutsi.blog.app.servlet

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Service
import org.springframework.web.filter.OncePerRequestFilter
import ua_parser.Client
import ua_parser.Parser

@Service
class BotFilter : OncePerRequestFilter() {
    companion object {
        const val ATTRIBUTE_UA_BOT = "com.wutsi.ua_bot"
        const val ATTRIBUTE_UA_CLIENT = "com.wutsi.ua_client"
    }

    private val parser = Parser()
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val ua = getUAClient(request)
        request.setAttribute(ATTRIBUTE_UA_BOT, ua?.device?.family?.equals("spider", true))
        filterChain.doFilter(request, response)
    }

    private fun getUAClient(request: HttpServletRequest): Client? {
        var ua = request.getAttribute(ATTRIBUTE_UA_CLIENT) as Client?
        if (ua == null) {
            ua = parser.parse(request.getHeader("User-Agent"))
            request.setAttribute(ATTRIBUTE_UA_CLIENT, ua)
        }
        return ua
    }
}
