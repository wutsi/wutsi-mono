package com.wutsi.blog.app.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.scribejava.core.oauth.OAuth20Service
import com.wutsi.blog.app.config.OAuthConfiguration
import com.wutsi.blog.app.config.SecurityConfiguration
import com.wutsi.blog.app.security.oauth.OAuthUser
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/login/yahoo")
@ConditionalOnProperty(value = ["wutsi.toggles.sso-yahoo"], havingValue = "true")
class YahooLoginController(
    logger: KVLogger,
    objectMapper: ObjectMapper,
    @Qualifier(OAuthConfiguration.YAHOO_OAUTH_SERVICE) private val oauth: OAuth20Service,
) : AbstractOAuth20LoginController(logger, objectMapper) {
    override fun getOAuthService() = oauth

    override fun getUserUrl() = "https://api.login.yahoo.com/openid/v1/userinfo"

    override fun toOAuthUser(attrs: Map<String, Any>) = OAuthUser(
        id = attrs["sub"].toString(),
        fullName = attrs["name"].toString(),
        email = attrs["email"]?.toString(),
        pictureUrl = attrs["picture"]?.toString(),
        provider = SecurityConfiguration.PROVIDER_YAHOO,
    )
}
