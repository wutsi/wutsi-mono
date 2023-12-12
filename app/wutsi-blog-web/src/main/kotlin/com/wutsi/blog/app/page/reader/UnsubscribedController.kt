package com.wutsi.blog.app.page.reader

import com.wutsi.blog.app.page.AbstractPageController
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.UserService
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

@Controller
class UnsubscribedController(
    private val userService: UserService,

    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    override fun pageName(): String =
        PageName.UNSUBSCRIBED

    @GetMapping("/@/{name}/unsubscribed")
    fun unsubscribed(
        @PathVariable name: String,
        @RequestParam email: String? = null,
        model: Model,
    ): String {
        val blog = userService.get(name)
        model.addAttribute("blog", blog)
        model.addAttribute("email", email)
        model.addAttribute("page", page())
        return "reader/unsubscribed"
    }

    override fun page() = createPage(
        title = requestContext.getMessage("page.unsubscribed.title"),
        description = ""
    )
}
