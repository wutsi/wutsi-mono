package com.wutsi.blog.app.page.admin

import com.wutsi.blog.app.page.AbstractPageController
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.util.PageName
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/me/syndicate")
class StorySyndicateController(
    private val service: StoryService,
    private val logger: KVLogger,
    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    override fun pageName() = PageName.STORY_SYNDICATE

    @GetMapping
    fun index(
        @RequestParam(required = false) error: String? = null,
        model: Model,
    ): String {
        if (error != null) {
            model.addAttribute("error", requestContext.getMessage(error, "error.syndicate_error"))
        }
        return "admin/syndicate"
    }

    @GetMapping("/import")
    fun import(@RequestParam url: String): String {
        try {
            val id = service.import(url)
            return "redirect:/editor/$id"
        } catch (ex: Exception) {
            logger.setException(ex)
            return "redirect:/me/syndicate?error=" + errorKey(ex)
        }
    }
}
