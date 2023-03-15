package com.wutsi.platform.core.security.servlet

import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse

class CorsFilter : Filter {
    override fun doFilter(
        req: ServletRequest,
        resp: ServletResponse,
        chain: FilterChain,
    ) {
        (resp as javax.servlet.http.HttpServletResponse).addHeader(
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
