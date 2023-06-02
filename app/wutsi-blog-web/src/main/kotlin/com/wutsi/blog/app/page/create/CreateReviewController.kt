package com.wutsi.blog.app.page.create

import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.UserService
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/create/review")
class CreateReviewController(
    userService: UserService,
    requestContext: RequestContext,
) : AbstractCreateController(userService, requestContext) {
    override fun pageName() = PageName.CREATE_REVIEW

    override fun pagePath() = "create/review"

    override fun redirectUrl() = "/create/success"

    override fun attributeName() = "blog"

    override fun value(): String = "true"
}
