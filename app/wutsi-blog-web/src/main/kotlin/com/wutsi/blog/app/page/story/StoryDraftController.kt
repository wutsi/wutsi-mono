package com.wutsi.blog.app.page.story

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.client.story.SearchStoryRequest
import com.wutsi.blog.client.story.StorySortStrategy
import com.wutsi.blog.client.story.StoryStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("/me/draft")
class StoryDraftController(
    service: StoryService,
    requestContext: RequestContext,
) : AbstractStoryListController(service, requestContext) {
    override fun pageName() = PageName.STORY_DRAFT

    override fun viewName() = "page/story/draft"

    override fun fetchStories(limit: Int, offset: Int): List<StoryModel> {
        val userId = requestContext.currentUser()?.id
        return service.search(
            SearchStoryRequest(
                userIds = if (userId == null) emptyList() else listOf(userId),
                status = StoryStatus.draft,
                sortBy = StorySortStrategy.modified,
                limit = limit,
                offset = offset,
            ),
        )
    }

    @GetMapping("/{id}/delete")
    @ResponseBody
    fun delete(@PathVariable id: Long): Map<String, String> {
        service.delete(id)
        return mapOf("id" to id.toString())
    }
}
