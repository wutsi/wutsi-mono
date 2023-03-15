package com.wutsi.platform.core.security.feign

import com.wutsi.platform.core.security.TokenProvider
import feign.RequestInterceptor
import feign.RequestTemplate
import org.slf4j.LoggerFactory

class FeignAuthorizationRequestInterceptor(
    private val tokenProvider: TokenProvider,
) : RequestInterceptor {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(FeignAuthorizationRequestInterceptor::class.java)
    }

    override fun apply(request: RequestTemplate) {
        val token = tokenProvider.getToken()
        if (token != null) {
            request.header("Authorization", "Bearer $token")
        } else {
            LOGGER.debug("No token available in the header.")
        }
    }
}
