package com.wutsi.blog.story.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.event.EventHandler
import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType.STORY_CREATED_EVENT
import com.wutsi.blog.event.EventType.STORY_DELETED_EVENT
import com.wutsi.blog.event.EventType.STORY_PUBLISHED_EVENT
import com.wutsi.blog.event.RootEventHandler
import com.wutsi.platform.core.stream.Event
import org.apache.commons.text.StringEscapeUtils
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class StoryEventHandler(
    private val root: RootEventHandler,
    private val objectMapper: ObjectMapper,
    private val service: StoryService,
) : EventHandler {
    @PostConstruct
    fun init() {
        root.register(STORY_CREATED_EVENT, this)
        root.register(STORY_PUBLISHED_EVENT, this)
        root.register(STORY_DELETED_EVENT, this)
    }

    override fun handle(event: Event) {
        when (event.type) {
            STORY_CREATED_EVENT -> service.onCreated(
                objectMapper.readValue(
                    decode(event.payload),
                    EventPayload::class.java,
                ),
            )

            STORY_PUBLISHED_EVENT -> service.onPublished(
                objectMapper.readValue(
                    decode(event.payload),
                    EventPayload::class.java,
                ),
            )

            STORY_DELETED_EVENT -> service.onDeleted(
                objectMapper.readValue(
                    decode(event.payload),
                    EventPayload::class.java,
                ),
            )

            else -> {}
        }
    }

    private fun decode(json: String): String =
        StringEscapeUtils.unescapeJson(json)
            .replace("\"{", "{")
            .replace("}\"", "}")
}
