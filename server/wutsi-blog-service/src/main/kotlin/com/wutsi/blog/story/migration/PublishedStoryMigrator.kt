package com.wutsi.blog.story.migration

import com.wutsi.blog.event.EventType
import com.wutsi.blog.story.domain.Story
import com.wutsi.blog.story.dto.StoryPublishedEventPayload
import com.wutsi.blog.story.service.StoryService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class PublishedStoryMigrator(
    private val service: StoryService,
) : StoryMigrator {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun migrate(item: Story) {
        val payload = StoryPublishedEventPayload(
            access = item.access,
            tags = item.tags.map { it.name },
            tagline = item.tagline,
            summary = item.summary ?: "",
            title = item.title ?: "",
            topicId = item.topicId,
        )
        service.notify(
            EventType.STORY_PUBLISHED_EVENT,
            item.id!!,
            item.userId,
            item.publishedDateTime?.time ?: System.currentTimeMillis(),
            payload,
        )
    }
}
