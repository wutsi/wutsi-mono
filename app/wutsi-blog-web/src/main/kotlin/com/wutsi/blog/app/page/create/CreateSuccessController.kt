package com.wutsi.blog.app.page.create

import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.UserService
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/create/success")
class CreateSuccessController(
    userService: UserService,
    requestContext: RequestContext,
) : AbstractCreateController(userService, requestContext) {
    override fun pageName() = PageName.CREATE_SUCCESS
    override fun pagePath() = ""
    override fun redirectUrl() = ""
    override fun attributeName() = ""
    override fun value(): String = ""

    override fun index(model: Model): String {
        model.addAttribute("blog", requestContext.currentUser())
        return "create/success"
    }
}
