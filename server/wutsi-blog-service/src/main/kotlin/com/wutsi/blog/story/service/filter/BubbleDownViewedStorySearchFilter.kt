package com.wutsi.blog.story.service.filter

import com.wutsi.blog.security.service.SecurityManager
import com.wutsi.blog.story.dao.ViewRepository
import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.service.StorySearchFilter
import com.wutsi.platform.core.tracing.TracingContext
import org.springframework.stereotype.Service

@Service
class BubbleDownViewedStorySearchFilter(
    private val viewDao: ViewRepository,
    private val securityManager: SecurityManager,
    private val tracingContext: TracingContext,
) : StorySearchFilter {
    override fun filter(request: SearchStoryRequest, stories: List<StoryEntity>): List<StoryEntity> {
        if (!request.bubbleDownViewedStories) {
            return stories
        }

        val viewedIds = viewDao.findStoryIdsByUserIdOrDeviceId(
            request.searchContext?.userId ?: securityManager.getCurrentUserId(),
            tracingContext.deviceId()
        )
        val result = mutableListOf<StoryEntity>()
        result.addAll(
            // Add stories not viewed
            stories.filter { !viewedIds.contains(it.id) },
        )
        result.addAll(
            // Add the stories viewed
            stories.filter { viewedIds.contains(it.id) },
        )
        return result
    }
}
