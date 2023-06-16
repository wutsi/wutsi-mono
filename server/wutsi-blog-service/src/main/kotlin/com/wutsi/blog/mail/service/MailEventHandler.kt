package com.wutsi.blog.story.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.event.EventHandler
import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType.SEND_STORY_EMAIL_NOTIFICATION_COMMAND
import com.wutsi.blog.event.EventType.STORY_CREATED_EVENT
import com.wutsi.blog.event.EventType.STORY_DELETED_EVENT
import com.wutsi.blog.event.EventType.STORY_EMAIL_NOTIFICATION_SENT_EVENT
import com.wutsi.blog.event.EventType.STORY_PUBLISHED_EVENT
import com.wutsi.blog.event.EventType.STORY_UNPUBLISHED_EVENT
import com.wutsi.blog.event.EventType.STORY_UPDATED_EVENT
import com.wutsi.blog.event.RootEventHandler
import com.wutsi.blog.mail.service.DailyEmailSender
import com.wutsi.blog.story.dto.SendStoryEmailNotificationCommand
import com.wutsi.platform.core.stream.Event
import org.apache.commons.text.StringEscapeUtils
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class StoryEventHandler(
    private val root: RootEventHandler,
    private val objectMapper: ObjectMapper,
    private val service: StoryService,
    private val notifier: DailyEmailSender,
) : EventHandler {
    @PostConstruct
    fun init() {
        root.register(SEND_STORY_EMAIL_NOTIFICATION_COMMAND, this)

        root.register(STORY_CREATED_EVENT, this)
        root.register(STORY_PUBLISHED_EVENT, this)
        root.register(STORY_DELETED_EVENT, this)
        root.register(STORY_UNPUBLISHED_EVENT, this)
        root.register(STORY_UPDATED_EVENT, this)
        root.register(STORY_EMAIL_NOTIFICATION_SENT_EVENT, this)
    }

    override fun handle(event: Event) {
        when (event.type) {
            SEND_STORY_EMAIL_NOTIFICATION_COMMAND -> notifier.send(
                objectMapper.readValue(
                    decode(event.payload),
                    SendStoryEmailNotificationCommand::class.java,
                ),
            )

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

            STORY_UNPUBLISHED_EVENT -> service.onUnpublished(
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

            STORY_UPDATED_EVENT -> service.onUpdated(
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
