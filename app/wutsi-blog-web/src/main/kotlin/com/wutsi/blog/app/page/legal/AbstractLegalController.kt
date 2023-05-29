package com.wutsi.blog.app.page.legal

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.service.RequestContext
import org.apache.commons.io.IOUtils
import org.springframework.ui.Model

abstract class AbstractLegalController(
    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    override fun shouldBeIndexedByBots() = true

    protected abstract fun contentPath(): String

    open fun index(model: Model): String {
        val path = contentPath() + ".html"
        val content = IOUtils.toString(AbstractLegalController::class.java.getResource(path))
        model.addAttribute("content", content)
        return "page/legal/index"
    }
}
