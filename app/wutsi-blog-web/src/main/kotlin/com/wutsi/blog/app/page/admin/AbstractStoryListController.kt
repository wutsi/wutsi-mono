package com.wutsi.blog.app.page.admin

import com.wutsi.blog.app.AbstractPageController
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

abstract class AbstractStoryListController(
    protected val service: StoryService,
    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    companion object {
        const val LIMIT = 20
    }

    protected abstract fun viewName(): String

    protected abstract fun moreUrl(): String

    protected abstract fun fetchStories(limit: Int, offset: Int): List<StoryModel>

    @GetMapping
    fun index(model: Model): String {
        more(offset = 0, model)
        return viewName()
    }

    @GetMapping("/more")
    fun more(
        @RequestParam(defaultValue = "0") offset: Int,
        model: Model,
    ): String {
        val stories = fetchStories(LIMIT, offset)
        if (stories.isNotEmpty()) {
            model.addAttribute("stories", stories)
            model.addAttribute("wallet", getWallet(stories[0].user))
            if (stories.size >= LIMIT) {
                model.addAttribute("moreUrl", moreUrl() + "?offset=" + (offset + LIMIT))
            }
        }
        return "admin/fragment/stories"
    }
}
