package com.wutsi.blog.app.page.reader

import com.wutsi.blog.app.page.AbstractPageController
import com.wutsi.blog.app.form.UnsubscribeForm
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.SubscriptionService
import com.wutsi.blog.app.service.UserService
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class UnsubscribeController(
    private val userService: UserService,
    private val subscriptionService: SubscriptionService,

    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    override fun pageName(): String =
        PageName.UNSUBSCRIBE

    @GetMapping("/@/{name}/unsubscribe")
    fun unsubscribe(
        @PathVariable name: String,
        @RequestParam(required = false) email: String? = null,
        model: Model,
    ): String {
        val blog = userService.get(name)
        model.addAttribute("blog", blog)
        model.addAttribute("page", getPage())
        model.addAttribute(
            "form",
            UnsubscribeForm(
                userId = blog.id,
                email = email
            )
        )
        return "reader/unsubscribe"
    }

    @PostMapping("/unsubscribe")
    fun submit(
        @ModelAttribute form: UnsubscribeForm,
        model: Model,
    ): String {
        subscriptionService.unsubscribe(form)

        val blog = userService.get(form.userId)
        return "redirect:/@/${blog.name}/unsubscribed?email=${form.email}"
    }
}
