package com.wutsi.blog.app.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.app.config.SecurityConfiguration
import com.wutsi.blog.app.security.oauth.OAuthUser
import com.wutsi.platform.core.logging.KVLogger
import java.net.URLEncoder
import java.util.UUID

abstract class AbstractLoginController(
    protected val logger: KVLogger,
    protected val objectMapper: ObjectMapper,
) {
    protected abstract fun toOAuthUser(attrs: Map<String, Any>): OAuthUser

    protected fun getSigninUrl(user: OAuthUser): String {
        val token = UUID.randomUUID().toString()
        val json = URLEncoder.encode(objectMapper.writeValueAsString(user), "utf-8")
        return SecurityConfiguration.OAUTH_SIGNIN_PATTERN +
            "?${SecurityConfiguration.PARAM_ACCESS_TOKEN}=$token" +
            "&${SecurityConfiguration.PARAM_USER}=$json"
    }
}
