package com.wutsi.blog.app.page.settings.monetization

import com.wutsi.blog.app.page.AbstractPageController
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/me/settings/monetization")
class MonetizationController(
    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    override fun pageName() = PageName.SETTINGS_MONETIZATION

    @GetMapping
    fun index(model: Model): String {
        val user = requestContext.currentUser()!!
        return if (!user.blog) {
            "redirect:${user.slug}"
        } else {
            "settings/monetization/index"
        }
    }
}
