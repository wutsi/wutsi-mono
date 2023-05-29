package com.wutsi.blog.app.page.create

import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.UserService
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/create/email")
class CreateEmailController(
    userService: UserService,
    requestContext: RequestContext,
) : AbstractCreateController(userService, requestContext) {
    override fun pageName() = PageName.CREATE_EMAIL

    override fun pagePath() = "page/create/email"

    override fun redirectUrl() = requestContext.currentUser()!!.slug

    override fun attributeName() = "email"

    override fun value() = requestContext.currentUser()?.email
}
