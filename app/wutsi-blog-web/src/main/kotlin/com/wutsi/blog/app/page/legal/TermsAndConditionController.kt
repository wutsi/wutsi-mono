package com.wutsi.blog.app.page.legal

import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class TermsAndConditionController(
    requestContext: RequestContext,
) : AbstractLegalController(requestContext) {
    override fun contentPath() = "/html/legal/terms"

    override fun pageName() = PageName.LEGAL_TERMS

    override fun page() = createPage(
        title = requestContext.getMessage("page.terms.title"),
        description = requestContext.getMessage("page.terms.description"),
    )

    @GetMapping("/terms")
    override fun index(model: Model): String {
        return super.index(model)
    }
}
