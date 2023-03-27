package com.wutsi.application.servlet

import com.auth0.jwt.JWT
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.security.TokenBlacklistService
import com.wutsi.platform.core.security.TokenProvider
import org.springframework.security.web.util.matcher.RequestMatcher
import java.time.Clock
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class TokenVerifierFilter(
    private val blacklist: TokenBlacklistService,
    private val tokenProvider: TokenProvider,
    private val requestMatcher: RequestMatcher,
    private val clock: Clock,
    private val logger: KVLogger,
) : Filter {
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        if (shouldVerifyToken(request as HttpServletRequest)) {
            val token = tokenProvider.getToken()
            if (token != null) {
                if (blacklist.contains(token)) {
                    logger.add("token_blacklisted", true)
                    authenticationFailed(response, "Logged out")
                    return
                } else if (expired(token)) {
                    logger.add("token_expired", true)
                    authenticationFailed(response, "Expired")
                    return
                }
            }
        }
        chain.doFilter(request, response)
    }

    private fun authenticationFailed(response: ServletResponse, message: String) {
        (response as HttpServletResponse).sendError(401, message)
    }

    private fun expired(token: String): Boolean {
        val jwt = JWT.decode(token)
        val expires = jwt.expiresAt.time
        return expires < clock.millis()
    }

    private fun shouldVerifyToken(request: HttpServletRequest): Boolean =
        requestMatcher.matches(request)
}
