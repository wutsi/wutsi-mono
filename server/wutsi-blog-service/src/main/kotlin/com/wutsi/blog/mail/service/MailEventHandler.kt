package com.wutsi.blog.mail.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.event.EventHandler
import com.wutsi.blog.event.EventType.SEND_STORY_DAILY_EMAIL_COMMAND
import com.wutsi.blog.event.RootEventHandler
import com.wutsi.blog.mail.dto.SendStoryDailyEmailCommand
import com.wutsi.platform.core.stream.Event
import org.apache.commons.text.StringEscapeUtils
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class MailEventHandler(
    private val root: RootEventHandler,
    private val objectMapper: ObjectMapper,
    private val mailService: MailService,
) : EventHandler {
    @PostConstruct
    fun init() {
        root.register(SEND_STORY_DAILY_EMAIL_COMMAND, this)
    }

    override fun handle(event: Event) {
        when (event.type) {
            SEND_STORY_DAILY_EMAIL_COMMAND -> mailService.send(
                objectMapper.readValue(
                    decode(event.payload),
                    SendStoryDailyEmailCommand::class.java,
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
