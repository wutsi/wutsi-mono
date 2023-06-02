package com.wutsi.blog.app.page.create

import com.wutsi.blog.app.AbstractPageController
import com.wutsi.blog.app.form.UserAttributeForm
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.UserService
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

abstract class AbstractCreateController(
    protected val userService: UserService,
    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    abstract fun pagePath(): String

    abstract fun redirectUrl(): String

    abstract fun attributeName(): String

    abstract fun value(): String?

    override fun page() = createPage(
        title = requestContext.getMessage("page.create.metadata.title"),
        description = requestContext.getMessage("page.create.metadata.description"),
    )

    @GetMapping
    open fun index(model: Model): String {
        val value = value()
        model.addAttribute("value", value)
        return pagePath()
    }

    @GetMapping("/submit")
    open fun submit(
        @RequestParam(required = false) value: String? = null,
        model: Model,
    ): String {
        try {
            userService.updateAttribute(
                UserAttributeForm(
                    name = attributeName(),
                    value = value,
                ),
            )
            return "redirect:" + redirectUrl()
        } catch (ex: Exception) {
            val error = errorKey(ex)
            model.addAttribute("error", requestContext.getMessage(error))
            model.addAttribute("value", value)
            return pagePath()
        }
    }
}
