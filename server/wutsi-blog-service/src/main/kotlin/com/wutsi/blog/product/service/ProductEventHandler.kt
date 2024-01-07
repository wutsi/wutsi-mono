package com.wutsi.blog.product.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.event.EventHandler
import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType.IMPORT_PRODUCT_COMMAND
import com.wutsi.blog.event.EventType.PRODUCT_ATTRIBUTE_UPDATED_EVENT
import com.wutsi.blog.event.EventType.PRODUCT_CREATED_EVENT
import com.wutsi.blog.event.EventType.PRODUCT_IMPORTED_EVENT
import com.wutsi.blog.event.RootEventHandler
import com.wutsi.blog.product.dto.ImportProductCommand
import com.wutsi.platform.core.stream.Event
import org.apache.commons.text.StringEscapeUtils
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class ProductEventHandler(
    private val root: RootEventHandler,
    private val objectMapper: ObjectMapper,
    private val importer: ProductImporter,
    private val service: ProductService
) : EventHandler {
    @PostConstruct
    fun init() {
        root.register(IMPORT_PRODUCT_COMMAND, this)
        root.register(PRODUCT_IMPORTED_EVENT, this)
        root.register(PRODUCT_ATTRIBUTE_UPDATED_EVENT, this)
        root.register(PRODUCT_CREATED_EVENT, this)
    }

    override fun handle(event: Event) {
        when (event.type) {
            IMPORT_PRODUCT_COMMAND -> importer.import(
                objectMapper.readValue(
                    decode(event.payload),
                    ImportProductCommand::class.java,
                ),
            )

            PRODUCT_CREATED_EVENT -> service.onProductCreated(
                objectMapper.readValue(
                    decode(event.payload),
                    EventPayload::class.java,
                ),
            )

            PRODUCT_ATTRIBUTE_UPDATED_EVENT -> service.onAttributeUpdated(
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
