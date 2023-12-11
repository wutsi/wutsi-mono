package com.wutsi.blog.app.page.settings.store

import com.wutsi.blog.app.AbstractPageController
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/me/store/success")
class StoreSuccessController(
    requestContext: RequestContext
) : AbstractPageController(requestContext) {
    override fun pageName() = PageName.SETTINGS_STORE_SUCCESS

    @GetMapping
    fun index(model: Model): String =
        "settings/store/success"
}
