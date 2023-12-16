package com.wutsi.blog.app.page.create

import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.UserService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.app.util.StringUtils.toUsername
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/create")
class CreateController(
    userService: UserService,
    requestContext: RequestContext,
) : AbstractCreateController(userService, requestContext) {
    override fun pageName() = PageName.CREATE
    override fun pagePath() = "create/index"
    override fun redirectUrl() = "/create/email"
    override fun attributeName() = "name"
    override fun value() = requestContext.currentUser()?.name

    override fun toValue(value: String?) = toUsername(value)

    @GetMapping
    override fun index(model: Model): String {
        val user = requestContext.currentUser()
        if (user != null && user.blog) {
            return "redirect:${user.slug}"
        }

        return super.index(model)
    }

    override fun page() = createPage(
        title = requestContext.getMessage("page.create.metadata.title"),
        description = requestContext.getMessage("page.create.metadata.description"),
    )
}
