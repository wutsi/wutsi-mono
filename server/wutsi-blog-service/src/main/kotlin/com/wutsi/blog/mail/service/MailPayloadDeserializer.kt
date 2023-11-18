package com.wutsi.blog.mail.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.event.EventType.STORY_DAILY_EMAIL_SENT_EVENT
import com.wutsi.blog.event.RootPayloadDeserializer
import com.wutsi.blog.mail.dto.StoryDailyEmailSentPayload
import com.wutsi.event.store.PayloadDeserializer
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class MailPayloadDeserializer(
    private val root: RootPayloadDeserializer,
    private val objectMapper: ObjectMapper,
) : PayloadDeserializer {
    @PostConstruct
    fun init() {
        root.register(STORY_DAILY_EMAIL_SENT_EVENT, this)
    }

    override fun deserialize(type: String, payload: String): Any? =
        when (type) {
            STORY_DAILY_EMAIL_SENT_EVENT -> objectMapper.readValue(
                payload,
                StoryDailyEmailSentPayload::class.java,
            )

            else -> null
        }
}
