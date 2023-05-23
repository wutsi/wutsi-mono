package com.wutsi.blog.app.common.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.app.util.CookieHelper
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Service

@ConfigurationProperties(prefix = "wutsi.toggles")
class Toggles {
    var channel: Boolean = false
    var channelTwitter: Boolean = false
    var channelFacebook: Boolean = false
    var channelLinkedin: Boolean = false
    var channelTelegram: Boolean = false
    var createBlog: Boolean = false
    var comment: Boolean = false
    var earning: Boolean = false
    var facebookPixel: Boolean = false
    var follow: Boolean = false
    var googleOneTapSignIn: Boolean = false
    var imageKit: Boolean = false
    var like: Boolean = false
    var nextAction: Boolean = false
    var pin: Boolean = false
    var pwa: Boolean = false
    var pwaAddToHomescreen = false
    var pwaBadge: Boolean = false
    var pwaPushNotification: Boolean = false
    var recommendation: Boolean = false
    var translation: Boolean = false
    var ssoFacebook: Boolean = false
    var ssoGoogle: Boolean = false
    var ssoGithub: Boolean = false
    var ssoTwitter: Boolean = false
    var ssoLinkedin: Boolean = false
    var ssoYahoo: Boolean = false
    var qaLogin: Boolean = false
    var wpp: Boolean = false
    var statistics: Boolean = false
    var tracking: Boolean = false
}

@Service
@EnableConfigurationProperties(Toggles::class)
class TogglesHolder(
    private val toggles: Toggles,
    private val requestContext: RequestContext,
    private val objectMapper: ObjectMapper,
) {
    companion object {
        const val COOKIE_FACEBOOK = "toggle.fb"
    }

    fun get(): Toggles {
        val value = objectMapper.readValue(
            objectMapper.writeValueAsString(toggles),
            Toggles::class.java,
        )
        value.channelFacebook = CookieHelper.get(COOKIE_FACEBOOK, requestContext.request) != null
        return value
    }
}
