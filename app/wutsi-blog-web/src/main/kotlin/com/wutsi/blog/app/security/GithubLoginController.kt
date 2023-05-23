package com.wutsi.blog.app.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.scribejava.core.oauth.OAuth20Service
import com.wutsi.blog.app.config.OAuthConfiguration
import com.wutsi.blog.app.config.SecurityConfiguration
import com.wutsi.blog.app.security.oauth.OAuthUser
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/login/github")
class GithubLoginController(
    logger: KVLogger,
    objectMapper: ObjectMapper,
    @Qualifier(OAuthConfiguration.GITHUB_OAUTH_SERVICE) private val oauth: OAuth20Service,
) : AbstractOAuth20LoginController(logger, objectMapper) {
    override fun getOAuthService() = oauth

    override fun getUserUrl() = "https://api.github.com/user"

    override fun toOAuthUser(attrs: Map<String, Any>) = OAuthUser(
        id = attrs["login"].toString(),
        fullName = githubFullName(attrs),
        email = attrs["email"]?.toString(),
        pictureUrl = attrs["avatar_url"]?.toString(),
        provider = SecurityConfiguration.PROVIDER_GITHUB,
    )

    private fun githubFullName(attrs: Map<String, Any>): String {
        val name = attrs["name"]?.toString()
        return if (name == null || name.isEmpty()) attrs["login"]!!.toString() else name
    }
}
