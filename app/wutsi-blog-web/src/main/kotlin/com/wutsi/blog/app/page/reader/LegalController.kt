package com.wutsi.blog.app.page.reader

import com.wutsi.blog.app.AbstractPageController
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.PageName
import org.apache.commons.io.IOUtils
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import java.nio.charset.Charset
import javax.servlet.http.HttpServletRequest

@Controller
class LegalController(
    requestContext: RequestContext,
    private val request: HttpServletRequest,
) : AbstractPageController(requestContext) {
    override fun shouldBeIndexedByBots() = true

    override fun pageName(): String =
        when (request.servletPath.lowercase()) {
            "/about" -> PageName.LEGAL_ABOUT
            "/privacy" -> PageName.LEGAL_PRIVACY
            else -> PageName.LEGAL_TERMS
        }

    @GetMapping("/about")
    fun about(model: Model): String {
        return load("about", model)
    }

    @GetMapping("/privacy")
    fun privacy(model: Model): String {
        return load("privacy", model)
    }

    @GetMapping("/terms")
    fun terms(model: Model): String {
        return load("terms", model)
    }

    private fun load(name: String, model: Model): String {
        val path = "/html/legal/$name.html"
        val content = IOUtils.toString(LegalController::class.java.getResource(path), Charset.forName("utf-8"))
        model.addAttribute("content", content)
        model.addAttribute("page", createPage(name))
        return "reader/legal"
    }

    private fun createPage(name: String) = createPage(
        title = requestContext.getMessage("page.$name.metadata.title"),
        description = requestContext.getMessage("page.$name.metadata.description"),
    )
}
