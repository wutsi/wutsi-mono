package com.wutsi.blog.app.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.scribejava.core.oauth.OAuth20Service
import com.wutsi.blog.app.config.OAuthConfiguration
import com.wutsi.blog.app.config.SecurityConfiguration
import com.wutsi.blog.app.security.oauth.OAuthUser
import com.wutsi.blog.client.channel.ChannelType
import com.wutsi.platform.core.logging.KVLogger
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import java.net.URLEncoder

@Controller
@RequestMapping("/login/linkedin")
@ConditionalOnProperty(value = ["wutsi.toggles.sso-linkedin"], havingValue = "true")
class LinkedinLoginController(
    logger: KVLogger,
    objectMapper: ObjectMapper,
    @Qualifier(OAuthConfiguration.LINKEDIN_OAUTH_SERVICE) private val oauth: OAuth20Service,
) : AbstractOAuth20LoginController(logger, objectMapper) {
    override fun getOAuthService() = oauth

    override fun getUserUrl() = "https://api.linkedin.com/v2/me"

    override fun toOAuthUser(attrs: Map<String, Any>) = OAuthUser(
        id = attrs["id"].toString(),
        fullName = attrs["localizedFirstName"].toString() + " " + attrs["localizedLastName"].toString(),
        email = attrs["email"]?.toString(),
        pictureUrl = null,
        provider = SecurityConfiguration.PROVIDER_LINKEDIN,
    )

    override fun getConnectUrl(request: HttpServletRequest): String {
        val code = request.getParameter("code")
        val accessToken = getOAuthService().getAccessToken(code).accessToken
        val user = toOAuthUser(accessToken)

        return "/me/settings/channel/create?" +
            "id=${user.id}" +
            "&accessToken=$accessToken" +
            "&accessTokenSecret=-" +
            "&name=" + URLEncoder.encode(user.fullName, "utf-8") +
            // "&pictureUrl=${user.pictureUrl}" +
            "&type=" + ChannelType.linkedin
    }

    override fun getError(request: HttpServletRequest) = request.getParameter("error_code")
}
