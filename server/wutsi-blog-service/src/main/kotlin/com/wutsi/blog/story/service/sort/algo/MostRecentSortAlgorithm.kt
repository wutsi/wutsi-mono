package com.wutsi.blog.story.service.sort.algo

import com.wutsi.blog.SortOrder
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.client.story.SortStoryRequest
import com.wutsi.blog.story.dto.StorySortStrategy
import com.wutsi.blog.story.service.StoryService
import com.wutsi.blog.story.service.sort.SortAlgorithm
import org.springframework.stereotype.Service

@Service("MostRecentSortAlgorithm")
class MostRecentSortAlgorithm(
    private val storyService: StoryService,
) : SortAlgorithm {
    override fun sort(request: SortStoryRequest): List<Long> {
        val story = storyService.searchStories(
            SearchStoryRequest(
                storyIds = request.storyIds,
                sortBy = StorySortStrategy.PUBLISHED,
                sortOrder = SortOrder.DESCENDING,
                limit = request.storyIds.size,
            ),
        )
        return story.map { it.id!! }
    }
}
