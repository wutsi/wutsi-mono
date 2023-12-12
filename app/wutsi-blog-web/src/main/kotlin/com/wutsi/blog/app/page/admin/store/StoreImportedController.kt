package com.wutsi.blog.app.page.admin.store

import com.wutsi.blog.app.AbstractPageController
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/me/store/imported")
class StoreImportedController(
    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    override fun pageName() = PageName.STORE_IMPORTED

    @GetMapping
    fun index(model: Model): String {
        return "admin/store/imported"
    }
}
