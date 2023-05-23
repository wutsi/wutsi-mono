package com.wutsi.blog.app.page.story

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.story.model.StoryModel
import com.wutsi.blog.app.page.story.service.StoryService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.client.story.SearchStoryRequest
import com.wutsi.blog.client.story.StorySortStrategy
import com.wutsi.blog.client.story.StoryStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/me/story/published")
class StoryPublishedController(
    service: StoryService,
    requestContext: RequestContext,
) : AbstractStoryListController(service, requestContext) {
    override fun pageName() = PageName.STORY_PUBLISHED

    override fun viewName() = "page/story/published"

    override fun fetchStories(limit: Int, offset: Int): List<StoryModel> {
        val userId = requestContext.currentUser()?.id
        return service.search(
            SearchStoryRequest(
                userIds = if (userId == null) emptyList() else listOf(userId),
                status = StoryStatus.published,
                sortBy = StorySortStrategy.published,
                limit = limit,
                offset = offset,
            ),
        )
    }
}
