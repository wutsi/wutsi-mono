package com.wutsi.blog.ads.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.ads.dto.AdsAttributeUpdatedEventPayload
import com.wutsi.blog.event.EventType.ADS_ATTRIBUTE_UPDATED_EVENT
import com.wutsi.blog.event.RootPayloadDeserializer
import com.wutsi.event.store.PayloadDeserializer
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class AdsPayloadDeserializer(
    private val root: RootPayloadDeserializer,
    private val objectMapper: ObjectMapper,
) : PayloadDeserializer {
    @PostConstruct
    fun init() {
        root.register(ADS_ATTRIBUTE_UPDATED_EVENT, this)
    }

    override fun deserialize(type: String, payload: String): Any? =
        when (type) {
            ADS_ATTRIBUTE_UPDATED_EVENT -> objectMapper.readValue(
                payload,
                AdsAttributeUpdatedEventPayload::class.java
            )

            else -> null
        }
}
