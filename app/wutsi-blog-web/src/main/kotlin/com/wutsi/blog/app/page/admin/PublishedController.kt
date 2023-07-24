package com.wutsi.blog.app.page.admin

import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.dto.StorySortStrategy
import com.wutsi.blog.story.dto.StoryStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/me/published")
class PublishedController(
    service: StoryService,
    requestContext: RequestContext,
) : AbstractStoryListController(service, requestContext) {
    override fun pageName() = PageName.STORY_PUBLISHED

    override fun viewName() = "admin/published"

    override fun moreUrl() = "/me/published/more"

    override fun fetchStories(limit: Int, offset: Int): List<StoryModel> {
        val userId = requestContext.currentUser()?.id
        return service.search(
            SearchStoryRequest(
                userIds = if (userId == null) emptyList() else listOf(userId),
                status = StoryStatus.PUBLISHED,
                sortBy = StorySortStrategy.PUBLISHED,
                limit = limit,
                offset = offset,
            ),
        )
    }

    @GetMapping("/unpublish")
    fun unpublish(@RequestParam(name = "story-id") storyId: Long): String {
        service.unpublish(storyId)
        return "redirect:/me/published"
    }
}
