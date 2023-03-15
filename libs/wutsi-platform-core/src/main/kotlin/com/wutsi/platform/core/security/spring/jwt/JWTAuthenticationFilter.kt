package com.wutsi.platform.core.security.spring.jwt

import com.wutsi.platform.core.security.spring.AnonymousAuthentication
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.util.matcher.RequestMatcher
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JWTAuthenticationFilter(
    requestMatcher: RequestMatcher,
) : AbstractAuthenticationProcessingFilter(requestMatcher) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(JWTAuthenticationFilter::class.java)
    }

    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication? {
        val token = getToken(request)
        val auth = if (token != null) {
            JWTAuthentication.of(token)
        } else {
            LOGGER.debug("${request.method} ${request.requestURI} - No token found in the header")
            AnonymousAuthentication()
        }

        return authenticationManager.authenticate(auth)
    }

    override fun successfulAuthentication(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain, authentication: Authentication) {
        SecurityContextHolder.getContext().authentication = authentication
        chain.doFilter(request, response)
    }

    private fun getToken(request: HttpServletRequest): String? {
        val value = request.getHeader("Authorization") ?: return null
        return if (value.startsWith("Bearer ", ignoreCase = true)) {
            value.substring(7)
        } else {
            null
        }
    }
}
