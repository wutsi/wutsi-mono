package com.wutsi.blog.app.page.reader

import com.wutsi.blog.app.page.AbstractPageController
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.PageName
import jakarta.servlet.http.HttpServletRequest
import org.apache.commons.io.IOUtils
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import java.nio.charset.Charset

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
        val content = getContent(name)
        model.addAttribute("content", content)
        model.addAttribute("page", createPage(name))
        return "reader/legal"
    }

    private fun getContent(name: String): String {
        val language = LocaleContextHolder.getLocale().language
        val content = LegalController::class.java.getResource("/html/legal/${name}_$language.html")
            ?: LegalController::class.java.getResource("/html/legal/$name.html")

        return IOUtils.toString(content, Charset.forName("utf-8"))
    }

    private fun createPage(name: String) = createPage(
        title = requestContext.getMessage(
            if (name == "about") "wutsi.slogan" else "page.$name.metadata.title"
        ),
        description = requestContext.getMessage(
            if (name == "about") "wutsi.tagline" else "page.$name.metadata.description"
        ),
    )
}
