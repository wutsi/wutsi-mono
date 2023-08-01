package com.wutsi.blog.story.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.event.EventType.STORY_ATTACHMENT_DOWNLOADED_EVENT
import com.wutsi.blog.event.EventType.STORY_CREATED_EVENT
import com.wutsi.blog.event.EventType.STORY_IMPORTED_EVENT
import com.wutsi.blog.event.EventType.STORY_IMPORT_FAILED_EVENT
import com.wutsi.blog.event.EventType.STORY_PUBLICATION_SCHEDULED_EVENT
import com.wutsi.blog.event.EventType.STORY_PUBLISHED_EVENT
import com.wutsi.blog.event.EventType.STORY_UPDATED_EVENT
import com.wutsi.blog.event.RootPayloadDeserializer
import com.wutsi.blog.story.dto.StoryAttachmentDownloadedEventPayload
import com.wutsi.blog.story.dto.StoryCreatedEventPayload
import com.wutsi.blog.story.dto.StoryImportFailedEventPayload
import com.wutsi.blog.story.dto.StoryImportedEventPayload
import com.wutsi.blog.story.dto.StoryPublicationScheduledEventPayload
import com.wutsi.blog.story.dto.StoryPublishedEventPayload
import com.wutsi.blog.story.dto.StoryUpdatedEventPayload
import com.wutsi.event.store.PayloadDeserializer
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class StoryPayloadDeserializer(
    private val root: RootPayloadDeserializer,
    private val objectMapper: ObjectMapper,
) : PayloadDeserializer {
    @PostConstruct
    fun init() {
        root.register(STORY_CREATED_EVENT, this)
        root.register(STORY_IMPORTED_EVENT, this)
        root.register(STORY_IMPORT_FAILED_EVENT, this)
        root.register(STORY_PUBLISHED_EVENT, this)
        root.register(STORY_PUBLICATION_SCHEDULED_EVENT, this)
        root.register(STORY_UPDATED_EVENT, this)
        root.register(STORY_ATTACHMENT_DOWNLOADED_EVENT, this)
    }

    override fun deserialize(type: String, payload: String): Any? =
        when (type) {
            STORY_IMPORTED_EVENT -> objectMapper.readValue(payload, StoryImportedEventPayload::class.java)
            STORY_IMPORT_FAILED_EVENT -> objectMapper.readValue(payload, StoryImportFailedEventPayload::class.java)
            STORY_PUBLISHED_EVENT -> objectMapper.readValue(payload, StoryPublishedEventPayload::class.java)
            STORY_PUBLICATION_SCHEDULED_EVENT -> objectMapper.readValue(
                payload,
                StoryPublicationScheduledEventPayload::class.java,
            )
            STORY_CREATED_EVENT -> objectMapper.readValue(payload, StoryCreatedEventPayload::class.java)
            STORY_UPDATED_EVENT -> objectMapper.readValue(payload, StoryUpdatedEventPayload::class.java)
            STORY_ATTACHMENT_DOWNLOADED_EVENT -> objectMapper.readValue(
                payload,
                StoryAttachmentDownloadedEventPayload::class.java,
            )
            else -> null
        }
}
