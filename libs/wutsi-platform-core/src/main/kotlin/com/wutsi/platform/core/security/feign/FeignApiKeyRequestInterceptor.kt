package com.wutsi.platform.core.security.feign

import feign.RequestInterceptor
import feign.RequestTemplate

class FeignApiKeyRequestInterceptor(
    private val apiKey: String?,
) : RequestInterceptor {
    override fun apply(request: RequestTemplate) {
        if (!apiKey.isNullOrEmpty()) {
            request.header("X-Api-Key", apiKey)
        }
    }
}
