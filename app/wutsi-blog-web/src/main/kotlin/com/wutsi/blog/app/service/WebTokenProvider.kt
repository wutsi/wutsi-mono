package com.wutsi.blog.app.service

import com.wutsi.platform.core.security.TokenProvider
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Primary
@Service
class WebTokenProvider(
    private val accessHolder: CurrentSessionHolder,
) : TokenProvider {
    override fun getToken(): String? =
        accessHolder.accessToken()
}
