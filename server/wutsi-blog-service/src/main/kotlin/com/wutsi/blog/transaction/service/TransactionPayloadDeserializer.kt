package com.wutsi.blog.transaction.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.event.EventType.TRANSACTION_NOTIFICATION_SUBMITTED_EVENT
import com.wutsi.blog.event.RootPayloadDeserializer
import com.wutsi.blog.transaction.dto.TransactionNotificationSubmittedEventPayload
import com.wutsi.event.store.PayloadDeserializer
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class TransactionPayloadDeserializer(
    private val root: RootPayloadDeserializer,
    private val objectMapper: ObjectMapper,
) : PayloadDeserializer {
    @PostConstruct
    fun init() {
        root.register(TRANSACTION_NOTIFICATION_SUBMITTED_EVENT, this)
    }

    override fun deserialize(type: String, payload: String): Any? =
        when (type) {
            TRANSACTION_NOTIFICATION_SUBMITTED_EVENT -> {
                objectMapper.readValue(payload, TransactionNotificationSubmittedEventPayload::class.java)
            }

            else -> null
        }
}
