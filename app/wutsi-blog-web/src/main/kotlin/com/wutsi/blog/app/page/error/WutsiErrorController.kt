package com.wutsi.blog.app.page.error

import com.wutsi.blog.app.AbstractPageController
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.ModelAttributeName
import com.wutsi.blog.app.util.PageName
import com.wutsi.platform.core.logging.KVLogger
import jakarta.servlet.http.HttpServletRequest
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/error")
class WutsiErrorController(
    requestContext: RequestContext,
    private val logger: KVLogger,
) : ErrorController, AbstractPageController(requestContext) {
    fun toPageName(): String = ""

    @GetMapping
    fun error(request: HttpServletRequest, model: Model): String {
        val message = request.getAttribute("jakarta.servlet.error.message") as String?
        logger.add("error_message", message)

        val exception = request.getAttribute("jakarta.servlet.error.exception") as Throwable?
        if (exception != null) {
            logger.setException(exception)
        }

        val code: Int = request.getAttribute("jakarta.servlet.error.status_code") as Int
        logger.add("error_code", code)

        model.addAttribute(ModelAttributeName.PAGE, toPage())
        model.addAttribute("errorCode", code)
        return "error/default"
    }

    @GetMapping("/account_suspended")
    fun accountSuspended(model: Model): String {
        model.addAttribute(ModelAttributeName.PAGE, toPage())
        return "error/account_suspended"
    }

    private fun toPage() = createPage(
        name = pageName(),
        title = requestContext.getMessage("page.home.metadata.title"),
        description = requestContext.getMessage("page.home.metadata.description"),
    )

    override fun pageName() = PageName.ERROR
}
