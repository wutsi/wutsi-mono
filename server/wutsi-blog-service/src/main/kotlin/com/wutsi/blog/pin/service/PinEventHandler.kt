package com.wutsi.blog.pin.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.event.EventHandler
import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType.PIN_STORY_COMMAND
import com.wutsi.blog.event.EventType.STORY_PINED_EVENT
import com.wutsi.blog.event.EventType.STORY_UNPINED_EVENT
import com.wutsi.blog.event.EventType.UNPIN_STORY_COMMAND
import com.wutsi.blog.event.RootEventHandler
import com.wutsi.blog.pin.dto.PinStoryCommand
import com.wutsi.blog.pin.dto.UnpinStoryCommand
import com.wutsi.platform.core.stream.Event
import org.apache.commons.text.StringEscapeUtils
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class PinEventHandler(
    private val root: RootEventHandler,
    private val objectMapper: ObjectMapper,
    private val service: PinService,
) : EventHandler {
    @PostConstruct
    fun init() {
        root.register(STORY_PINED_EVENT, this)
        root.register(STORY_UNPINED_EVENT, this)
        root.register(PIN_STORY_COMMAND, this)
        root.register(UNPIN_STORY_COMMAND, this)
    }

    override fun handle(event: Event) {
        when (event.type) {
            STORY_PINED_EVENT -> service.onPinned(
                objectMapper.readValue(
                    decode(event.payload),
                    EventPayload::class.java,
                ),
            )

            STORY_UNPINED_EVENT -> service.onUnpined(
                objectMapper.readValue(
                    decode(event.payload),
                    EventPayload::class.java,
                ),
            )

            PIN_STORY_COMMAND -> service.pin(
                objectMapper.readValue(
                    decode(event.payload),
                    PinStoryCommand::class.java,
                ),
            )

            UNPIN_STORY_COMMAND -> service.unpin(
                objectMapper.readValue(
                    decode(event.payload),
                    UnpinStoryCommand::class.java,
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
