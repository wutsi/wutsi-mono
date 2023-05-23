package com.wutsi.blog.app.page.story

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.story.model.StoryModel
import com.wutsi.blog.app.page.story.service.StoryService
import org.slf4j.LoggerFactory
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

abstract class AbstractStoryListController(
    protected val service: StoryService,
    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    protected abstract fun viewName(): String

    protected abstract fun fetchStories(limit: Int, offset: Int): List<StoryModel>

    @GetMapping
    fun index(
        @RequestParam(defaultValue = "20") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int,
        @RequestParam(required = false, name = "pubid") publishedId: Long? = null,
        model: Model,
    ): String {
        val stories = fetchStories(limit, offset)
        model.addAttribute("stories", stories)

        if (publishedId != null) {
            loadPublishedStory(publishedId, model)
        }

        return viewName()
    }

    private fun loadPublishedStory(publishedId: Long, model: Model) {
        try {
            val published = service.get(publishedId)
            model.addAttribute("publishedStory", published)
        } catch (ex: Exception) {
            LoggerFactory.getLogger(javaClass).warn("Unable to load published story", ex)
        }
    }
}
