package com.wutsi.blog.app.page.legal

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class AboutController(requestContext: RequestContext) : AbstractLegalController(requestContext) {
    override fun pageName() = PageName.LEGAL_ABOUT

    override fun contentPath() = "/html/legal/about"

    override fun page() = createPage(
        title = requestContext.getMessage("page.about.metadata.title"),
        description = requestContext.getMessage("page.about.metadata.description"),
    )

    @GetMapping("/about")
    override fun index(model: Model): String {
        return super.index(model)
    }
}
