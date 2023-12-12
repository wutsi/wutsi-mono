package com.wutsi.blog.app.page.settings.wpp

import com.wutsi.blog.app.page.AbstractPageController
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.UserService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.story.dto.WPPConfig
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/me/partner/join")
class WPPJoinController(
    requestContext: RequestContext,
    private val userService: UserService
) : AbstractPageController(requestContext) {
    override fun pageName() = PageName.SETTINGS_WPP_JOIN

    @GetMapping
    fun index(model: Model): String {
        model.addAttribute("wpp", WPPConfig)
        return "settings/wpp/join"
    }

    @GetMapping("/submit")
    fun submit(): String {
        val user = requestContext.currentUser()
        if (user?.canJoinWPP == true) {
            userService.joinWPP()
            return "redirect:/me/partner/success"
        } else {
            return "redirect:/partner"
        }
    }
}
