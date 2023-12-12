package com.wutsi.blog.app.page.settings.store

import com.wutsi.blog.app.AbstractPageController
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/me/store/create")
class StoreCreateController(
    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    override fun pageName() = PageName.SETTINGS_STORE_CREATE

    @GetMapping
    fun index(model: Model): String {
        return "settings/store/create"
    }

    @GetMapping("/submit")
    fun submit(): String {
        val user = requestContext.currentUser()
        if (user?.canCreateStore == true) {
            storeService.create()
            return "redirect:/me/store/success"
        } else {
            return "redirect:/store"
        }
    }
}
