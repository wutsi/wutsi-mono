package com.wutsi.blog.account.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.account.dto.UserLoggedInAsEventPayload
import com.wutsi.blog.event.EventType.USER_LOGGED_IN_AS_EVENT
import com.wutsi.blog.event.RootPayloadDeserializer
import com.wutsi.event.store.PayloadDeserializer
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class AccountPayloadDeserializer(
    private val root: RootPayloadDeserializer,
    private val objectMapper: ObjectMapper,
) : PayloadDeserializer {
    @PostConstruct
    fun init() {
        root.register(USER_LOGGED_IN_AS_EVENT, this)
    }

    override fun deserialize(type: String, payload: String): Any? =
        when (type) {
            USER_LOGGED_IN_AS_EVENT -> objectMapper.readValue(payload, UserLoggedInAsEventPayload::class.java)
            else -> null
        }
}
