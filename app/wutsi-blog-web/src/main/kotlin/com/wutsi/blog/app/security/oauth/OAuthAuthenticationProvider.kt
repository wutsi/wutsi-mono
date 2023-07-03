package com.wutsi.blog.app.security.oauth

import com.wutsi.blog.account.dto.LoginUserCommand
import com.wutsi.blog.app.backend.AuthenticationBackend
import com.wutsi.blog.app.backend.IpApiBackend
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
    private val ipApiBackend: IpApiBackend,
    private val logger: KVLogger,
) : AuthenticationProvider {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(OAuthAuthenticationFilter::class.java)
    }

    override fun authenticate(auth: Authentication): Authentication {
        val authentication = auth as OAuthTokenAuthentication
        return authenticate(authentication)
    }

    private fun authenticate(authentication: OAuthTokenAuthentication): Authentication {
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
                country = resolveCountry(),
            ),
        )

        authentication.isAuthenticated = true
        requestContext.request.getSession(true).maxInactiveInterval = 84600 // 1d
        return authentication
    }

    override fun supports(clazz: Class<*>) = OAuthTokenAuthentication::class.java == clazz

    private fun resolveCountry(): String? {
        val ip = requestContext.loadRemoteIp(requestContext.request)
        if (ip.isNullOrEmpty()) {
            return null
        }

        return try {
            val country = ipApiBackend.resolve(ip).country
            logger.add("country", country)
            country
        } catch (ex: Exception) {
            LOGGER.warn("Unable to resolve country from $ip", ex)
            null
        }
    }
}
