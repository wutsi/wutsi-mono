package com.wutsi.blog.app.page.settings.monetization

import com.wutsi.blog.app.page.AbstractPageController
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/me/settings/monetization/success")
class MonetizationSuccessController(
    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    override fun pageName() = PageName.SETTINGS_MONETIZATION_SUCCESS

    @GetMapping
    fun success(model: Model): String {
        return "settings/monetization/success"
    }
}
