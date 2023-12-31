package com.wutsi.blog.product.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.event.EventType.PRODUCT_ATTRIBUTE_UPDATED_EVENT
import com.wutsi.blog.event.RootPayloadDeserializer
import com.wutsi.blog.product.dto.ProductAttributeUpdatedEventPayload
import com.wutsi.event.store.PayloadDeserializer
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class ProductPayloadDeserializer(
    private val root: RootPayloadDeserializer,
    private val objectMapper: ObjectMapper,
) : PayloadDeserializer {
    @PostConstruct
    fun init() {
        root.register(PRODUCT_ATTRIBUTE_UPDATED_EVENT, this)
    }

    override fun deserialize(type: String, payload: String): Any? =
        when (type) {
            PRODUCT_ATTRIBUTE_UPDATED_EVENT -> objectMapper.readValue(
                payload,
                ProductAttributeUpdatedEventPayload::class.java
            )

            else -> null
        }
}
