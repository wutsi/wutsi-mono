package com.wutsi.blog.app.page.settings.wpp

import com.wutsi.blog.app.page.AbstractPageController
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/me/partner/success")
class WPPSuccessController(
    requestContext: RequestContext
) : AbstractPageController(requestContext) {
    override fun pageName() = PageName.SETTINGS_WPP_SUCCESS

    @GetMapping
    fun index(model: Model): String =
        "settings/wpp/success"
}
