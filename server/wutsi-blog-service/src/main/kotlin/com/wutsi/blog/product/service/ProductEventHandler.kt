package com.wutsi.blog.product.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.event.EventHandler
import com.wutsi.blog.event.EventType.IMPORT_PRODUCT_COMMAND
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
) : EventHandler {
    @PostConstruct
    fun init() {
        root.register(IMPORT_PRODUCT_COMMAND, this)
        root.register(PRODUCT_IMPORTED_EVENT, this)
    }

    override fun handle(event: Event) {
        when (event.type) {
            IMPORT_PRODUCT_COMMAND -> importer.import(
                objectMapper.readValue(
                    decode(event.payload),
                    ImportProductCommand::class.java,
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
