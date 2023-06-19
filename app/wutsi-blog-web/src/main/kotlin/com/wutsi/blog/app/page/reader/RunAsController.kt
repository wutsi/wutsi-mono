package com.wutsi.blog.app.page.reader

import com.wutsi.blog.app.AbstractPageController
import com.wutsi.blog.app.form.RunAsForm
import com.wutsi.blog.app.service.AuthenticationService
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.PageName
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.ForbiddenException
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("/login/as")
class RunAsController(
    private val service: AuthenticationService,
    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    override fun pageName() = PageName.RUN_AS

    @GetMapping()
    fun index(
        @RequestParam(required = false) error: String? = null,
        @RequestHeader(required = false) referer: String? = null,
        model: Model,
        request: HttpServletRequest,
    ): String {
        ensureSuperUser()
        if (error != null) {
            model.addAttribute("error", requestContext.getMessage(error))
        }
        model.addAttribute("form", RunAsForm())
        return "reader/login-as"
    }

    @PostMapping
    fun submit(@ModelAttribute form: RunAsForm): String {
        ensureSuperUser()
        try {
            val name = form.name.toLowerCase()
            service.runAs(name)
            return "redirect:/@/$name"
        } catch (ex: Exception) {
            val error = errorKey(ex)
            return "redirect:/login/as?error=$error"
        }
    }

    private fun ensureSuperUser() {
        if (requestContext.currentSuperUser() == null) {
            throw ForbiddenException(Error("permission_denied"))
        }
    }
}
