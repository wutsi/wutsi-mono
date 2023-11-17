package com.wutsi.blog.app.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.account.dto.Link
import com.wutsi.blog.app.security.oauth.OAuthAuthenticationProvider
import com.wutsi.blog.app.security.oauth.OAuthUser
import com.wutsi.blog.app.security.service.AuthenticationSuccessHandlerImpl
import com.wutsi.blog.app.service.AuthenticationService
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.platform.core.logging.KVLogger
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping
class EmailLoginController(
    private val service: AuthenticationService,
    private val requestContext: RequestContext,
    objectMapper: ObjectMapper,
    logger: KVLogger
) : AbstractLoginController(logger, objectMapper) {
    @GetMapping("/login/email/callback")
    fun callback(@RequestParam(name = "link-id") linkId: String): String {
        val link = service.getLink(linkId).link
        storeIntoSession(link)
        val url = getSigninUrl(
            toOAuthUser(
                mapOf(
                    "email" to link.email.lowercase()
                )
            )
        )
        logger.add("redirect_url", url)
        return "redirect:$url"
    }

    private fun storeIntoSession(link: Link) {
        val session = requestContext.request.session
        if (link.storyId != null) {
            session.setAttribute(OAuthAuthenticationProvider.SESSION_ATTRIBUTE_STORY_ID, link.storyId)
            session.setAttribute(OAuthAuthenticationProvider.SESSION_ATTRIBUTE_REFERER, "story")
        } else {
            session.removeAttribute(OAuthAuthenticationProvider.SESSION_ATTRIBUTE_STORY_ID)
            session.removeAttribute(OAuthAuthenticationProvider.SESSION_ATTRIBUTE_REFERER)
        }

        if (link.referer != null) {
            session.setAttribute(OAuthAuthenticationProvider.SESSION_ATTRIBUTE_REFERER, link.referer)
        } else {
            session.removeAttribute(OAuthAuthenticationProvider.SESSION_ATTRIBUTE_REFERER)
        }

        if (link.redirectUrl != null) {
            session.setAttribute(AuthenticationSuccessHandlerImpl.SESSION_ATTRIBUTE_REDIRECT_URL, link.redirectUrl)
        } else {
            session.removeAttribute(AuthenticationSuccessHandlerImpl.SESSION_ATTRIBUTE_REDIRECT_URL)
        }
    }

    override fun toOAuthUser(attrs: Map<String, Any>) = OAuthUser(
        provider = "email",
        email = attrs["email"]!!.toString(),
        id = DigestUtils.md5Hex(attrs["email"].toString())
    )
}
