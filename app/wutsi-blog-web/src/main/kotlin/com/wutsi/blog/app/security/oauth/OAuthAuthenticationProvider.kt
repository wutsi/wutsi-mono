package com.wutsi.blog.app.security.oauth

import com.wutsi.blog.account.dto.LoginUserCommand
import com.wutsi.blog.app.backend.AuthenticationBackend
import com.wutsi.blog.app.security.servlet.OAuthAuthenticationFilter
import com.wutsi.blog.app.service.IpApiService
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.platform.core.logging.KVLogger
import org.slf4j.LoggerFactory
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

@Component
class OAuthAuthenticationProvider(
    private val backend: AuthenticationBackend,
    private val requestContext: RequestContext,
    private val ipApiService: IpApiService,
    private val logger: KVLogger,
) : AuthenticationProvider {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(OAuthAuthenticationFilter::class.java)
        val SESSION_ATTRIBUTE_STORY_ID = "com.wutsi.story_id"
        val SESSION_ATTRIBUTE_REFERER = "com.wutsi.referer"
    }

    override fun authenticate(auth: Authentication): Authentication {
        val authentication = auth as OAuthTokenAuthentication
        return authenticate(authentication)
    }

    private fun authenticate(authentication: OAuthTokenAuthentication): Authentication {
        // Authenticate
        val user = authentication.principal.user
        backend.login(
            LoginUserCommand(
                accessToken = authentication.accessToken,
                provider = authentication.principal.user.provider,
                pictureUrl = user.pictureUrl,
                fullName = user.fullName,
                email = user.email,
                providerUserId = user.id,
                language = LocaleContextHolder.getLocale().language,
                country = ipApiService.resolveCountry(),
                ip = requestContext.remoteIp(),
                referer = requestContext.request.session.getAttribute(SESSION_ATTRIBUTE_REFERER)?.toString(),
                storyId = try {
                    requestContext.request.session.getAttribute(SESSION_ATTRIBUTE_STORY_ID)?.toString()?.toLong()
                } catch (ex: Exception) {
                    null
                },
            ),
        )

        authentication.isAuthenticated = true
        return authentication
    }

    override fun supports(clazz: Class<*>) = OAuthTokenAuthentication::class.java == clazz

}
