package com.wutsi.blog.share.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.event.EventHandler
import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType.SHARE_STORY_COMMAND
import com.wutsi.blog.event.EventType.STORY_SHARED_EVENT
import com.wutsi.blog.event.RootEventHandler
import com.wutsi.blog.share.dto.ShareStoryCommand
import com.wutsi.platform.core.stream.Event
import org.apache.commons.text.StringEscapeUtils
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class ShareEventHandler(
    private val root: RootEventHandler,
    private val objectMapper: ObjectMapper,
    private val service: ShareService,
) : EventHandler {
    @PostConstruct
    fun init() {
        root.register(STORY_SHARED_EVENT, this)
        root.register(SHARE_STORY_COMMAND, this)
    }

    override fun handle(event: Event) {
        when (event.type) {
            STORY_SHARED_EVENT -> service.onShared(
                objectMapper.readValue(
                    decode(event.payload),
                    EventPayload::class.java,
                ),
            )

            SHARE_STORY_COMMAND -> service.share(
                objectMapper.readValue(
                    decode(event.payload),
                    ShareStoryCommand::class.java,
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
