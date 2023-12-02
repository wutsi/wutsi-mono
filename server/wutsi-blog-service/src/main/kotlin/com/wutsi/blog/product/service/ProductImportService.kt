package com.wutsi.blog.product.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.product.dao.ProductImportRepository
import com.wutsi.blog.product.domain.ProductImportEntity
import com.wutsi.blog.product.domain.StoreEntity
import com.wutsi.blog.product.dto.ImportResult
import com.wutsi.event.store.Event
import com.wutsi.event.store.EventStore
import com.wutsi.platform.core.storage.StorageService
import com.wutsi.platform.core.stream.EventStream
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.ByteArrayInputStream

@Service
class ProductImportService(
    private val dao: ProductImportRepository,
    private val storage: StorageService,
    private val objectMapper: ObjectMapper,
    private val eventStore: EventStore,
    private val eventStream: EventStream
) {
    @Transactional
    fun onImported(path: String, store: StoreEntity, result: ImportResult): ProductImportEntity {
        val errorUrl = if (result.errors.isNotEmpty()) {
            val txt = objectMapper.writeValueAsString(result)
            storage.store("$path/report.json", ByteArrayInputStream(txt.toByteArray()))
        } else {
            null
        }

        return dao.save(
            ProductImportEntity(
                store = store,
                errorCount = result.errors.size,
                importedCount = result.importedCount,
                unpublishedCount = result.unpublishedCount,
                url = result.url,
                errorReportUrl = errorUrl?.toString()
            )
        )
    }

    @Transactional
    fun notify(productImport: ProductImportEntity) {
        val event = Event(
            streamId = StreamId.PRODUCT_IMPORT,
            type = EventType.PRODUCT_IMPORTED_EVENT,
            entityId = productImport.id.toString(),
            timestamp = productImport.creationDateTime,
        )
        val eventId = eventStore.store(event)

        eventStream.enqueue(EventType.PRODUCT_IMPORTED_EVENT, EventPayload(eventId))
    }
}
