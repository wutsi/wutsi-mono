package com.wutsi.blog.pin.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType.PIN_STORY_COMMAND
import com.wutsi.blog.event.EventType.STORY_PINED_EVENT
import com.wutsi.blog.event.EventType.STORY_UNPINED_EVENT
import com.wutsi.blog.event.EventType.UNPIN_STORY_COMMAND
import com.wutsi.blog.event.RootPayloadDeserializer
import com.wutsi.blog.pin.dto.PinStoryCommand
import com.wutsi.blog.pin.dto.UnpinStoryCommand
import com.wutsi.event.store.PayloadDeserializer
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class PinPayloadDeserializer(
    private val root: RootPayloadDeserializer,
    private val objectMapper: ObjectMapper,
) : PayloadDeserializer {
    @PostConstruct
    fun init() {
        root.register(STORY_PINED_EVENT, this)
        root.register(STORY_UNPINED_EVENT, this)

        root.register(PIN_STORY_COMMAND, this)
        root.register(UNPIN_STORY_COMMAND, this)
    }

    override fun deserialize(type: String, payload: String): Any? =
        when (type) {
            STORY_PINED_EVENT -> objectMapper.readValue(payload, EventPayload::class.java)
            STORY_UNPINED_EVENT -> objectMapper.readValue(payload, EventPayload::class.java)

            PIN_STORY_COMMAND -> objectMapper.readValue(payload, PinStoryCommand::class.java)
            UNPIN_STORY_COMMAND -> objectMapper.readValue(payload, UnpinStoryCommand::class.java)
            else -> null
        }
}
