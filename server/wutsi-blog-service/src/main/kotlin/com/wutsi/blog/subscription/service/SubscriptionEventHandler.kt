package com.wutsi.blog.subscription.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.event.EventHandler
import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType.IMPORT_SUBSCRIBER_COMMAND
import com.wutsi.blog.event.EventType.SUBSCRIBED_EVENT
import com.wutsi.blog.event.EventType.SUBSCRIBER_IMPORTED_EVENT
import com.wutsi.blog.event.EventType.SUBSCRIBE_COMMAND
import com.wutsi.blog.event.EventType.UNSUBSCRIBED_EVENT
import com.wutsi.blog.event.EventType.UNSUBSCRIBE_COMMAND
import com.wutsi.blog.event.RootEventHandler
import com.wutsi.blog.subscription.dto.ImportSubscriberCommand
import com.wutsi.blog.subscription.dto.SubscribeCommand
import com.wutsi.blog.subscription.dto.UnsubscribeCommand
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.Event
import org.apache.commons.text.StringEscapeUtils
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class SubscriptionEventHandler(
    private val root: RootEventHandler,
    private val objectMapper: ObjectMapper,
    private val service: SubscriptionService,
    private val importer: SubscriptionImporterService,
    private val logger: KVLogger,
) : EventHandler {
    @PostConstruct
    fun init() {
        root.register(SUBSCRIBE_COMMAND, this)
        root.register(UNSUBSCRIBE_COMMAND, this)
        root.register(IMPORT_SUBSCRIBER_COMMAND, this)

        root.register(SUBSCRIBED_EVENT, this)
        root.register(UNSUBSCRIBED_EVENT, this)
        root.register(SUBSCRIBER_IMPORTED_EVENT, this)
    }

    override fun handle(event: Event) {
        when (event.type) {
            SUBSCRIBED_EVENT -> service.onSubscribed(
                objectMapper.readValue(
                    decode(event.payload),
                    EventPayload::class.java,
                ),
            )

            UNSUBSCRIBED_EVENT -> service.onUnsubscribed(
                objectMapper.readValue(
                    decode(event.payload),
                    EventPayload::class.java,
                ),
            )

            SUBSCRIBER_IMPORTED_EVENT -> importer.onImported(
                objectMapper.readValue(
                    decode(event.payload),
                    EventPayload::class.java,
                ),
            )

            SUBSCRIBE_COMMAND -> try {
                service.subscribe(
                    objectMapper.readValue(
                        decode(event.payload),
                        SubscribeCommand::class.java,
                    ),
                )
            } catch (e: DataIntegrityViolationException) {
                // Ignore - duplicate like
                logger.add("duplicate_subscription", true)
            }

            UNSUBSCRIBE_COMMAND -> service.unsubscribe(
                objectMapper.readValue(
                    decode(event.payload),
                    UnsubscribeCommand::class.java,
                ),
            )

            IMPORT_SUBSCRIBER_COMMAND -> importer.import(
                objectMapper.readValue(
                    decode(event.payload),
                    ImportSubscriberCommand::class.java,
                ),
            )

            else -> {}
        }
    }

    private fun decode(json: String): String =
        StringEscapeUtils
            .unescapeJson(json)
            .replace("\"{", "{")
            .replace("}\"", "}")
}
