package com.wutsi.blog.mail.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.event.EventHandler
import com.wutsi.blog.event.EventType.EMAIL_BOUNCED_EVENT
import com.wutsi.blog.event.EventType.EMAIL_COMPLAINED_EVENT
import com.wutsi.blog.event.EventType.EMAIL_DELIVERED_EVENT
import com.wutsi.blog.event.EventType.EMAIL_OPENED_EVENT
import com.wutsi.blog.event.RootEventHandler
import com.wutsi.blog.mail.dto.EmailBouncedEvent
import com.wutsi.blog.mail.dto.EmailComplainedEvent
import com.wutsi.blog.mail.dto.EmailOpenedEvent
import com.wutsi.blog.mail.dto.EmailType
import com.wutsi.blog.story.service.StoryService
import com.wutsi.blog.subscription.service.SubscriptionService
import com.wutsi.blog.user.service.UserService
import com.wutsi.platform.core.stream.Event
import org.apache.commons.text.StringEscapeUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class MailEventHandler(
    private val root: RootEventHandler,
    private val objectMapper: ObjectMapper,
    private val xmailService: XEmailService,
    private val userService: UserService,
    private val storyService: StoryService,
    private val subscriptionService: SubscriptionService,
) : EventHandler {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(MailEventHandler::class.java)
    }

    @PostConstruct
    fun init() {
        root.register(EMAIL_BOUNCED_EVENT, this)
        root.register(EMAIL_COMPLAINED_EVENT, this)
        root.register(EMAIL_DELIVERED_EVENT, this)
        root.register(EMAIL_OPENED_EVENT, this)
    }

    override fun handle(event: Event) {
        when (event.type) {
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

            EMAIL_OPENED_EVENT -> {
                try {
                    val event = objectMapper.readValue(
                        decode(event.payload),
                        EmailOpenedEvent::class.java,
                    )
                    when (event.type) {
                        EmailType.WEEKLY_DIGEST -> event.userId?.let { userId ->
                            userService.onWeeklyEmailOpened(userId, event.timestamp)
                        }

                        EmailType.DAILY_EMAIL -> event.userId?.let { userId ->
                            event.storyId?.let { storyId ->
                                val story = storyService.findById(storyId)
                                subscriptionService.onEmailOpened(story.userId, userId, event.timestamp)
                            }
                        }

                        else -> {}
                    }
                } catch (ex: Exception) {
                    LOGGER.warn("Unexpected error", ex)
                }
            }

            else -> {}
        }
    }

    private fun decode(json: String): String =
        StringEscapeUtils
            .unescapeJson(json)
            .replace("\"{", "{")
            .replace("}\"", "}")
}
