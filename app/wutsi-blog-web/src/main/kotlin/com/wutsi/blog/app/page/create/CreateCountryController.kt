package com.wutsi.blog.app.page.create

import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.UserService
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/create/country")
class CreateCountryController(
    userService: UserService,
    requestContext: RequestContext,
) : AbstractCreateController(userService, requestContext) {
    override fun pageName() = PageName.CREATE_COUNTRY
    override fun pagePath() = "create/country"
    override fun redirectUrl() = "/create/review"
    override fun attributeName() = "country"
    override fun value() = requestContext.currentUser()?.country
}
