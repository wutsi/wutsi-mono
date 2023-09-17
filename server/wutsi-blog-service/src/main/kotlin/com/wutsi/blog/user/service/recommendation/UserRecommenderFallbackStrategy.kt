package com.wutsi.blog.story.service.recommendation

import com.wutsi.blog.SortOrder
import com.wutsi.blog.story.dto.RecommendStoryRequest
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.dto.StorySortStrategy
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.blog.story.service.StoryRecommenderStrategy
import com.wutsi.blog.story.service.StoryService
import org.springframework.stereotype.Service

@Service
class StoryRecommenderFallbackStrategy(
    private val storyService: StoryService,
) : StoryRecommenderStrategy {
    override fun recommend(request: RecommendStoryRequest): List<Long> =
        storyService.searchStories(
            SearchStoryRequest(
                status = StoryStatus.PUBLISHED,
                sortBy = StorySortStrategy.POPULARITY,
                sortOrder = SortOrder.DESCENDING,
                limit = request.limit,
                bubbleDownViewedStories = true,
            ),
        ).mapNotNull { it.id }
}
