package com.wutsi.blog.app.page.reader

import com.wutsi.blog.app.AbstractPageController
import com.wutsi.blog.app.backend.AuthenticationBackend
import com.wutsi.blog.app.service.AccessTokenStorage
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.PageName
import com.wutsi.platform.core.logging.KVLogger
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/logout")
class LogoutController(
    private val backend: AuthenticationBackend,
    private val tokenStorage: AccessTokenStorage,
    private val logger: KVLogger,
    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    @GetMapping()
    fun index(model: Model, request: HttpServletRequest, response: HttpServletResponse): String {
        try {
            val accessToken = tokenStorage.get(request)
            if (accessToken != null) {
                tokenStorage.delete(response)
                backend.logout(accessToken)

                SecurityContextHolder.clearContext()
            }
        } catch (ex: Exception) {
            logger.add("LogoutException", ex.javaClass.name)
            logger.add("LogoutExceptionMessage", ex.message)
        } finally {
            request.logout()
        }
        return "redirect:/"
    }

    override fun pageName() = PageName.LOGOUT
}
