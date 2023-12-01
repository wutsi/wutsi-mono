package com.wutsi.blog.product.service

import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.product.dao.ProductRepository
import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.product.dto.ImportProductCommand
import com.wutsi.blog.product.dto.ProductImportedEventPayload
import com.wutsi.event.store.Event
import com.wutsi.event.store.EventStore
import com.wutsi.platform.core.stream.EventStream
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date
import java.util.Optional

@Service
class ProductService(
    private val dao: ProductRepository,
    private val eventStore: EventStore,
    private val eventStream: EventStream
) {
    fun findByExternalIdAndUserId(externalId: String, userId: Long): Optional<ProductEntity> =
        dao.findByExternalIdAndUserId(externalId, userId)

    @Transactional
    fun save(product: ProductEntity) {
        dao.save(product)
    }

    @Transactional
    fun notifyImport(command: ImportProductCommand) {
        val event = Event(
            streamId = StreamId.PRODUCT,
            type = EventType.PRODUCT_IMPORTED_EVENT,
            entityId = command.userId.toString(),
            timestamp = Date(command.timestamp),
            payload = ProductImportedEventPayload(command.url),
        )
        val eventId = eventStore.store(event)

        eventStream.enqueue(event.type, EventPayload(eventId = eventId))
    }
}
