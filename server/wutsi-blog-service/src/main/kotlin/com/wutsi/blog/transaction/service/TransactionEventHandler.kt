package com.wutsi.blog.transaction.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.event.EventHandler
import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType.SUBMIT_TRANSACTION_NOTIFICATION_COMMAND
import com.wutsi.blog.event.EventType.TRANSACTION_NOTIFICATION_SUBMITTED_EVENT
import com.wutsi.blog.event.EventType.TRANSACTION_SUCCEEDED_EVENT
import com.wutsi.blog.event.RootEventHandler
import com.wutsi.blog.transaction.dto.SubmitTransactionNotificationCommand
import com.wutsi.platform.core.stream.Event
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class TransactionEventHandler(
    private val root: RootEventHandler,
    private val objectMapper: ObjectMapper,
    private val service: TransactionService,
) : EventHandler {
    @PostConstruct
    fun init() {
        root.register(SUBMIT_TRANSACTION_NOTIFICATION_COMMAND, this)
        root.register(TRANSACTION_NOTIFICATION_SUBMITTED_EVENT, this)
        root.register(TRANSACTION_SUCCEEDED_EVENT, this)
    }

    override fun handle(event: Event) {
        when (event.type) {
            SUBMIT_TRANSACTION_NOTIFICATION_COMMAND -> service.notify(
                objectMapper.readValue(
                    event.payload,
                    SubmitTransactionNotificationCommand::class.java,
                ),
            )
            TRANSACTION_NOTIFICATION_SUBMITTED_EVENT -> service.onNotification(
                objectMapper.readValue(
                    event.payload,
                    EventPayload::class.java,
                ),
            )
            TRANSACTION_SUCCEEDED_EVENT -> service.onTransactionSuccessful(
                objectMapper.readValue(
                    event.payload,
                    EventPayload::class.java,
                ),
            )
            else -> {}
        }
    }

//    private fun decode(json: String): String =
//        StringEscapeUtils.unescapeJson(json)
//            .replace("\"{", "{")
//            .replace("}\"", "}")
}
