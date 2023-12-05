package com.wutsi.blog.product.service

import com.wutsi.blog.error.ErrorCode.PRODUCT_NOT_FOUND
import com.wutsi.blog.product.dao.ProductRepository
import com.wutsi.blog.product.dao.SearchProductQueryBuilder
import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.product.domain.StoreEntity
import com.wutsi.blog.product.dto.ProductStatus
import com.wutsi.blog.product.dto.SearchProductRequest
import com.wutsi.blog.util.Predicates
import com.wutsi.event.store.EventStore
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.exception.NotFoundException
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.EventStream
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date
import java.util.Optional

@Service
class ProductService(
    private val dao: ProductRepository,
    private val eventStore: EventStore,
    private val eventStream: EventStream,
    private val logger: KVLogger,
    private val em: EntityManager,
) {
    fun findById(id: Long): ProductEntity =
        dao.findById(id)
            .orElseThrow {
                NotFoundException(
                    error = Error(
                        code = PRODUCT_NOT_FOUND,
                        parameter = Parameter(
                            name = "id",
                            value = id,
                        )
                    )
                )
            }

    fun findByExternalIdAndStore(externalId: String, store: StoreEntity): Optional<ProductEntity> =
        dao.findByExternalIdAndStore(externalId, store)

    @Transactional
    fun save(product: ProductEntity): ProductEntity {
        product.modificationDateTime = Date()
        return dao.save(product)
    }

    @Transactional
    fun unpublishOthers(store: StoreEntity, externalIds: List<String>): List<ProductEntity> {
        val products = dao.findByStoreAndStatusAndExternalIdNotIn(store, ProductStatus.PUBLISHED, externalIds)
        if (products.isNotEmpty()) {
            val now = Date()
            products.forEach { product ->
                product.status = ProductStatus.DRAFT
                product.publishedDateTime = null
                product.modificationDateTime = now
            }
            return dao.saveAll(products).toList()
        }
        return emptyList()
    }

    fun search(request: SearchProductRequest, deviceId: String? = null): List<ProductEntity> {
        logger.add("request_product_ids", request.productIds)
        logger.add("request_external_ids", request.externalIds)
        logger.add("request_store_ids", request.storeIds)
        logger.add("request_status", request.status)
        logger.add("request_sort_by", request.sortBy)
        logger.add("request_sort_order", request.sortOrder)
        logger.add("request_limit", request.limit)
        logger.add("request_offset", request.offset)

        val products = searchStories(request)
        logger.add("count", products.size)

        return products
    }

    private fun searchStories(request: SearchProductRequest): List<ProductEntity> {
        val builder = SearchProductQueryBuilder()
        val sql = builder.query(request)
        val params = builder.parameters(request)
        val query = em.createNativeQuery(sql, ProductEntity::class.java)
        Predicates.setParameters(query, params)
        return query.resultList as List<ProductEntity>
    }
}
