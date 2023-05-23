package com.wutsi.blog.app.page.login

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.login.service.AuthenticationService
import com.wutsi.blog.app.page.settings.model.UserModel
import com.wutsi.blog.app.page.settings.service.UserService
import com.wutsi.blog.app.util.PageName
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.web.savedrequest.SavedRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.net.URL
import java.net.URLDecoder
import java.util.regex.Pattern
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("/login")
class LoginController(
    private val userService: UserService,
    private val authenticationService: AuthenticationService,
    @Value("\${wutsi.application.server-url}") private val serverUrl: String,
    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(LoginController::class.java)
        private const val REASON_CREATE_BLOG = "create-blog"
        private const val REASON_FOLLOW = "follow"
        private val PATH_FOLLOW = Pattern.compile("/@/(.*)/follow")
    }

    @GetMapping()
    fun index(
        @RequestParam(required = false) error: String? = null,
        @RequestParam(required = false) reason: String? = null,
        @RequestParam(required = false) redirect: String? = null,
        @RequestParam(required = false) `return`: String? = null,
        @RequestHeader(required = false) referer: String? = null,
        model: Model,
        request: HttpServletRequest,
    ): String {
        model.addAttribute("error", error)
        val redirectUrl = getRedirectURL(request)
        val xreason = getReason(reason, redirectUrl)
        model.addAttribute("createBlog", xreason == REASON_CREATE_BLOG)
        model.addAttribute("info", info(xreason))
        model.addAttribute("title", title(xreason))
        model.addAttribute("return", `return`)
        if (xreason == REASON_FOLLOW) {
            model.addAttribute("blog", getBlogToFollow(redirectUrl!!))
            model.addAttribute("followBlog", true)
        }

        model.addAttribute("googleUrl", authenticationService.loginUrl("/login/google", redirect))
        model.addAttribute("facebookUrl", authenticationService.loginUrl("/login/facebook", redirect))
        model.addAttribute("githubUrl", authenticationService.loginUrl("/login/github", redirect))
        model.addAttribute("twitterUrl", authenticationService.loginUrl("/login/twitter", redirect))
        model.addAttribute("linkedinUrl", authenticationService.loginUrl("/login/linkedin", redirect))
        model.addAttribute("yahooUrl", authenticationService.loginUrl("/login/yahoo", redirect))

        loadTargetUser(xreason, redirectUrl, model)
        return "page/login/index"
    }

    override fun pageName() = PageName.LOGIN

    private fun getReason(reason: String?, redirectUrl: URL?): String? {
        if (reason != null) {
            return reason
        }

        if (redirectUrl != null && URL(serverUrl).host == redirectUrl.host) {
            if (redirectUrl.path.startsWith("/create")) {
                return REASON_CREATE_BLOG
            } else if (PATH_FOLLOW.matcher(redirectUrl.path.toLowerCase()).matches()) {
                return REASON_FOLLOW
            }
        }
        return null
    }

    private fun getRedirectURL(request: HttpServletRequest): URL? {
        val savedRequest = request.session.getAttribute("SPRING_SECURITY_SAVED_REQUEST") as SavedRequest?
            ?: return null
        return URL(savedRequest.redirectUrl)
    }

    private fun title(reason: String?): String {
        val default = "page.login.header1.login"
        val key = if (reason != null) {
            "page.login.header1.$reason"
        } else {
            default
        }

        return requestContext.getMessage(key, default)
    }

    private fun info(reason: String?): String {
        val default = "page.login.info.login"
        val key = if (reason != null) {
            "page.login.info.$reason"
        } else {
            default
        }

        return requestContext.getMessage(key, default)
    }

    private fun loadTargetUser(reason: String?, redirectUrl: URL?, model: Model) {
        if (redirectUrl == null || reason != REASON_FOLLOW) {
            return
        }

        val userId = splitQuery(redirectUrl)["userId"]
        if (userId != null) {
            try {
                val blog = userService.get(userId.toLong())
                model.addAttribute("blog", blog)
            } catch (ex: Exception) {
                LOGGER.error("Unable to fetch User#$userId", ex)
            }
        }
    }

    private fun splitQuery(url: URL): Map<String, String> {
        if (url.query == null) {
            return emptyMap()
        }

        val queryPairs: MutableMap<String, String> = LinkedHashMap()
        val pairs = url.query.split("&").toTypedArray()
        for (pair in pairs) {
            val idx = pair.indexOf("=")
            queryPairs[decode(pair.substring(0, idx))] = decode(pair.substring(idx + 1))
        }
        return queryPairs
    }

    private fun decode(value: String): String =
        URLDecoder.decode(value, "UTF-8")

    private fun getBlogToFollow(redirectUrl: URL): UserModel? {
        try {
            val matcher = PATH_FOLLOW.matcher(redirectUrl.path.toLowerCase())
            while (matcher.find()) {
                val name = matcher.group(1)
                return userService.get(name)
            }
        } catch (ex: Exception) {
            LOGGER.error("Unable to resolve user from $redirectUrl", ex)
        }
        return null
    }
}
