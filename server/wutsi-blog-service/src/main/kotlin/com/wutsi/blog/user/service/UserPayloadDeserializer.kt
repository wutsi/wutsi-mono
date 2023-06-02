package com.wutsi.blog.user.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.event.EventType.USER_ATTRIBUTE_UPDATED_EVENT
import com.wutsi.blog.event.RootPayloadDeserializer
import com.wutsi.blog.user.dto.UserAttributeUpdatedEvent
import com.wutsi.event.store.PayloadDeserializer
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class UserPayloadDeserializer(
    private val root: RootPayloadDeserializer,
    private val objectMapper: ObjectMapper,
) : PayloadDeserializer {
    @PostConstruct
    fun init() {
        root.register(USER_ATTRIBUTE_UPDATED_EVENT, this)
    }

    override fun deserialize(type: String, payload: String): Any? =
        when (type) {
            USER_ATTRIBUTE_UPDATED_EVENT -> objectMapper.readValue(payload, UserAttributeUpdatedEvent::class.java)
            else -> null
        }
}
