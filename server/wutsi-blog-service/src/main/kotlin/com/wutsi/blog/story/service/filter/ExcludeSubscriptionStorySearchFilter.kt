package com.wutsi.blog.story.service.filter

import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.service.StorySearchFilter
import com.wutsi.blog.subscription.dao.SubscriptionRepository
import org.springframework.stereotype.Service

@Service
class ExcludeSubscriptionStorySearchFilter(
    private val dao: SubscriptionRepository,
) : StorySearchFilter {
    override fun filter(request: SearchStoryRequest, stories: List<StoryEntity>): List<StoryEntity> {
        val userId = request.searchContext?.userId
        if (!request.excludeStoriesFromSubscriptions || userId == null || stories.isEmpty()) {
            return stories
        }

        val userIds = dao.findBySubscriberId(userId).map { it.userId }
        return stories.filter { story -> !userIds.contains(story.userId) }
    }
}
