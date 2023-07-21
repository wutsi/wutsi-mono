package com.wutsi.blog.story.tools

import com.wutsi.blog.event.EventType
import com.wutsi.blog.story.dao.StoryContentRepository
import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.story.dto.StoryCreatedEventPayload
import com.wutsi.blog.story.dto.StoryImportedEventPayload
import com.wutsi.blog.story.dto.StoryPublishedEventPayload
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.blog.story.service.StoryService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrNull

@Service
class StoryMigrator(
    private val service: StoryService,
    private val contentDao: StoryContentRepository,
) {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun migrate(item: StoryEntity) {
        if (item.sourceUrl.isNullOrEmpty()) {
            create(item)
        } else {
            import(item)
        }

        if (item.status == StoryStatus.PUBLISHED) {
            publish(item)
        }

        if (item.deleted) {
            delete(item)
        }
    }

    private fun create(item: StoryEntity) {
        val payload = StoryCreatedEventPayload(
            title = item.title,
            content = contentDao.findByStoryAndLanguage(item, item.language).getOrNull()?.content,
        )
        service.notify(EventType.STORY_CREATED_EVENT, item.id!!, item.userId, item.publishedDateTime!!.time, payload)
    }

    private fun import(item: StoryEntity) {
        val payload = StoryImportedEventPayload(item.sourceUrl!!)
        service.notify(EventType.STORY_IMPORTED_EVENT, item.id!!, item.userId, item.creationDateTime.time, payload)
    }

    private fun publish(item: StoryEntity) {
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

    private fun delete(item: StoryEntity) {
        service.notify(
            EventType.STORY_DELETED_EVENT,
            item.id!!,
            item.userId,
            item.deletedDateTime?.time ?: System.currentTimeMillis(),
        )
    }
}
