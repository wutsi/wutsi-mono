package com.wutsi.blog.story.service.similarity

import com.wutsi.blog.SortOrder
import com.wutsi.blog.story.dao.StoryRepository
import com.wutsi.blog.story.dto.SearchSimilarStoryRequest
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.dto.StorySortStrategy
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.blog.story.service.StoryService
import com.wutsi.blog.story.service.StorySimilarityStrategy
import org.springframework.stereotype.Service

@Service
class StorySimilarityFallbackStrategy(
    private val storyDao: StoryRepository,
    private val storyService: StoryService,
) : StorySimilarityStrategy {
    /**
     * Return stories associated with the authors
     */
    override fun search(request: SearchSimilarStoryRequest): List<Long> {
        val stories = storyDao.findAllById(request.storyIds)
        return storyService.searchStories(
            SearchStoryRequest(
                status = StoryStatus.PUBLISHED,
                userIds = stories.map { it.userId },
                sortBy = StorySortStrategy.PUBLISHED,
                sortOrder = SortOrder.DESCENDING,
                limit = request.limit + request.storyIds.size,
                bubbleDownViewedStories = true,
            ),
        ).filter { !request.storyIds.contains(it.id) }
            .mapNotNull { it.id }
            .take(request.limit)
    }
}
