package com.wutsi.blog.app.reader

import com.wutsi.blog.app.AbstractPageController
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.ModelAttributeName
import com.wutsi.blog.app.util.PageName
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import javax.servlet.http.HttpServletRequest

@Controller
class WutsiErrorController(
    requestContext: RequestContext,
    private val logger: KVLogger,
) : ErrorController, AbstractPageController(requestContext) {
    override fun pageName(): String = ""

    @GetMapping("/error")
    fun error(request: HttpServletRequest, model: Model): String {
        val message = request.getAttribute("javax.servlet.error.message") as String
        logger.add("error_message", message)

        val exception = request.getAttribute("javax.servlet.error.exception") as Throwable?
        if (exception != null) {
            logger.setException(exception)
        }

        val code: Int = request.getAttribute("javax.servlet.error.status_code") as Int
        logger.add("error_code", code)

        model.addAttribute(ModelAttributeName.PAGE, toPage(code))
        model.addAttribute("errorCode", code)
        return "reader/error"
    }

    private fun toPage(code: Int?) = createPage(
        name = pageName(code),
        title = requestContext.getMessage("page.home.metadata.title"),
        description = requestContext.getMessage("page.home.metadata.description"),
    )

    private fun pageName(code: Int?): String {
        if (code == 400) {
            return PageName.ERROR_400
        } else if (code == 403) {
            return PageName.ERROR_403
        } else if (code == 404) {
            return PageName.ERROR_404
        } else {
            return PageName.ERROR_500
        }
    }
}
