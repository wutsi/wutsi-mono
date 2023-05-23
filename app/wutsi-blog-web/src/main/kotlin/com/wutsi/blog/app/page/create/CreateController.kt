package com.wutsi.blog.app.page.create

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.settings.service.UserService
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/create")
class CreateController(
    userService: UserService,
    requestContext: RequestContext,
) : AbstractCreateController(userService, requestContext) {
    override fun pageName() = PageName.CREATE

    override fun pagePath() = "page/create/index"

    override fun redirectUrl() = "/create/email"

    override fun attributeName() = "name"

    override fun value() = requestContext.currentUser()?.name
}
