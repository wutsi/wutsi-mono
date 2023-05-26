package com.wutsi.blog.pin.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.event.RootPayloadDeserializer
import com.wutsi.blog.pin.dto.PinEventType
import com.wutsi.blog.pin.dto.PinStoryCommand
import com.wutsi.blog.pin.dto.StoryPinedEvent
import com.wutsi.blog.pin.dto.StoryUnpinedEvent
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
        root.register(PinEventType.STORY_PINED_EVENT, this)
        root.register(PinEventType.STORY_UNPINED_EVENT, this)

        root.register(PinEventType.PIN_STORY_COMMAND, this)
        root.register(PinEventType.UNPIN_STORY_COMMAND, this)
    }

    override fun deserialize(type: String, payload: String): Any? =
        when (type) {
            PinEventType.STORY_PINED_EVENT -> objectMapper.readValue(payload, StoryPinedEvent::class.java)
            PinEventType.STORY_UNPINED_EVENT -> objectMapper.readValue(payload, StoryUnpinedEvent::class.java)

            PinEventType.PIN_STORY_COMMAND -> objectMapper.readValue(payload, PinStoryCommand::class.java)
            PinEventType.UNPIN_STORY_COMMAND -> objectMapper.readValue(payload, UnpinStoryCommand::class.java)
            else -> null
        }
}
