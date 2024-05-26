package com.wutsi.blog.story.service

import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.service.filter.BubbleDownViewedStorySearchFilter
import com.wutsi.blog.story.service.filter.DedupUserStorySearchFilter
import com.wutsi.blog.story.service.filter.PreferredCategoryStorySearchFilter
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Service

@Service
class StorySearchFilterSet(
    private val preferredCategoryStorySearchFilter: PreferredCategoryStorySearchFilter,
    private val bubbleDownViewedStorySearchFilter: BubbleDownViewedStorySearchFilter,
    private val dedupUserStorySearchFilter: DedupUserStorySearchFilter,
    private val logger: KVLogger,
) : StorySearchFilter {
    private val filters = listOf(
        preferredCategoryStorySearchFilter, // MUST BE FIRST
        bubbleDownViewedStorySearchFilter, // MUST BE BEFORE THE LAST
        dedupUserStorySearchFilter // MUST BE THE LAST
    )

    override fun filter(request: SearchStoryRequest, stories: List<StoryEntity>): List<StoryEntity> {
        if (stories.isEmpty()) {
            return stories
        }

        var count = 0
        logger.add("story_filter_$count", stories.size)

        var result = stories
        filters.forEach { filter ->
            result = filter.filter(request, result)

            count++
            logger.add("story_filter_${count}_" + filter.javaClass.simpleName, result.size)
        }
        return result
    }
}
