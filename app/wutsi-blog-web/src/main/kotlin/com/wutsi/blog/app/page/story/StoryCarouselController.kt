package com.wutsi.blog.app.page.story

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.client.story.SearchStoryRequest
import com.wutsi.blog.client.story.StorySortStrategy.recommended
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class StoryCarouselController(
    private val storyService: StoryService,
    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    override fun pageName(): String = PageName.STORY_CAROUSEL

    @GetMapping("/story/carousel")
    fun search(
        @RequestParam(required = false, defaultValue = "-1") topicId: Long = -1,
        @RequestParam(required = false) title: String? = null,
        model: Model,
    ): String {
        val users = mutableSetOf<Long>()
        val stories = mutableListOf<StoryModel>()
        storyService.search(
            request = SearchStoryRequest(
                topicId = if (topicId > -1) topicId else null,
                sortBy = recommended,
                limit = 50,
            ),
        ).forEach {
            if (!users.contains(it.user.id) && !it.user.testUser) {
                users.add(it.user.id)
                stories.add(it)
            }
        }
        model.addAttribute("title", title)
        model.addAttribute("stories", stories.take(3))
        return "page/story/carousel"
    }
}
