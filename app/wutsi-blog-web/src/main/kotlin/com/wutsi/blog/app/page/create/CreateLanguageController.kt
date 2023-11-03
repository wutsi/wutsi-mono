package com.wutsi.blog.app.page.create

import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.UserService
import com.wutsi.blog.app.util.PageName
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import java.util.Locale

@Controller
@RequestMapping("/create/language")
class CreateLanguageController(
    userService: UserService,
    requestContext: RequestContext,
) : AbstractCreateController(userService, requestContext) {
    override fun pageName() = PageName.CREATE_LANGUAGE
    override fun pagePath() = "create/language"
    override fun redirectUrl() = "/create/review"
    override fun attributeName() = "language"
    override fun value() = requestContext.currentUser()?.language ?: LocaleContextHolder.getLocale().language

    override fun index(model: Model): String {
        val languages = Locale.getISOLanguages().map { lang -> Locale(lang) }.sortedBy { it.displayLanguage }
        model.addAttribute("languages", languages)
        return super.index(model)
    }
}
