package com.wutsi.blog.app.page.legal

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class PrivacyController(
    requestContext: RequestContext,
) : AbstractLegalController(requestContext) {
    override fun contentPath() = "/html/legal/privacy"

    override fun pageName() = PageName.LEGAL_PRIVACY

    override fun page() = createPage(
        title = requestContext.getMessage("page.privacy.title"),
        description = requestContext.getMessage("page.privacy.description"),
    )

    @GetMapping("/privacy")
    override fun index(model: Model): String {
        return super.index(model)
    }
}
