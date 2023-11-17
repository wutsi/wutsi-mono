package com.wutsi.blog.mail.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.event.EventHandler
import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType.EMAIL_BOUNCED_EVENT
import com.wutsi.blog.event.EventType.EMAIL_COMPLAINED_EVENT
import com.wutsi.blog.event.EventType.EMAIL_DELIVERED_EVENT
import com.wutsi.blog.event.EventType.LOGIN_LINK_CREATED_EVENT
import com.wutsi.blog.event.EventType.SEND_STORY_DAILY_EMAIL_COMMAND
import com.wutsi.blog.event.EventType.SEND_STORY_WEEKLY_EMAIL_COMMAND
import com.wutsi.blog.event.RootEventHandler
import com.wutsi.blog.mail.dto.EmailBouncedEvent
import com.wutsi.blog.mail.dto.EmailComplainedEvent
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
    private val xmailService: XEmailService,
) : EventHandler {
    @PostConstruct
    fun init() {
        root.register(SEND_STORY_DAILY_EMAIL_COMMAND, this)
        root.register(SEND_STORY_WEEKLY_EMAIL_COMMAND, this)
        root.register(EMAIL_BOUNCED_EVENT, this)
        root.register(EMAIL_COMPLAINED_EVENT, this)
        root.register(EMAIL_DELIVERED_EVENT, this)
        root.register(LOGIN_LINK_CREATED_EVENT, this)
    }

    override fun handle(event: Event) {
        when (event.type) {
            SEND_STORY_DAILY_EMAIL_COMMAND -> mailService.sendDaily(
                objectMapper.readValue(
                    decode(event.payload),
                    SendStoryDailyEmailCommand::class.java,
                ),
            )

            SEND_STORY_WEEKLY_EMAIL_COMMAND -> mailService.sendWeekly()

            EMAIL_BOUNCED_EVENT -> xmailService.onBounced(
                objectMapper.readValue(
                    decode(event.payload),
                    EmailBouncedEvent::class.java,
                ),
            )

            EMAIL_COMPLAINED_EVENT -> xmailService.onComplained(
                objectMapper.readValue(
                    decode(event.payload),
                    EmailComplainedEvent::class.java,
                ),
            )

            LOGIN_LINK_CREATED_EVENT -> mailService.sendLoginLink(
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
