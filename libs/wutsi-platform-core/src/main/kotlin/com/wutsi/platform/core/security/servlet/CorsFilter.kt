package com.wutsi.platform.core.security.servlet

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletResponse

class CorsFilter : Filter {
    override fun doFilter(
        req: ServletRequest,
        resp: ServletResponse,
        chain: FilterChain,
    ) {
        (resp as HttpServletResponse).addHeader(
            "Access-Control-Allow-Origin",
            "*",
        )
        resp.addHeader(
            "Access-Control-Allow-Methods",
            "GET, OPTIONS, HEAD, PUT, POST, DELETE",
        )
        resp.addHeader("Access-Control-Allow-Headers", "*")
        resp.addHeader("Access-Control-Expose-Headers", "*")
        chain.doFilter(req, resp)
    }
}
