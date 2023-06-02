package com.wutsi.blog.app.page.create

import com.wutsi.blog.app.AbstractPageController
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.UserService
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/create/review")
class CreateSuccessController(
    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    open fun index(model: Model): String {
        model.addAttribute("blog", )
        return "create/review"
    }

}
