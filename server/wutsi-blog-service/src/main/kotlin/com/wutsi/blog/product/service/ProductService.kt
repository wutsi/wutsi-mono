package com.wutsi.blog.product.service

import com.wutsi.blog.error.ErrorCode.PRODUCT_NOT_FOUND
import com.wutsi.blog.kpi.dao.ProductKpiRepository
import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.dto.TrafficSource
import com.wutsi.blog.product.dao.ProductRepository
import com.wutsi.blog.product.dao.SearchProductQueryBuilder
import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.product.domain.StoreEntity
import com.wutsi.blog.product.dto.ProductStatus
import com.wutsi.blog.product.dto.SearchProductRequest
import com.wutsi.blog.product.mapper.ProductMapper
import com.wutsi.blog.story.dao.StoryRepository
import com.wutsi.blog.transaction.dao.TransactionRepository
import com.wutsi.blog.transaction.dto.TransactionType
import com.wutsi.blog.util.Predicates
import com.wutsi.blog.util.SlugGenerator
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.exception.NotFoundException
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.payment.core.Status
import jakarta.persistence.EntityManager
import org.apache.commons.text.similarity.LevenshteinDistance
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date
import java.util.Optional
import kotlin.jvm.optionals.getOrNull

@Service
class ProductService(
    private val dao: ProductRepository,
    private val storyDao: StoryRepository,
    private val logger: KVLogger,
    private val em: EntityManager,
    private val mapper: ProductMapper,
    private val transactionDao: TransactionRepository,
    private val productKpiDao: ProductKpiRepository,
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

        val products = searchProducts(request)
        logger.add("count", products.size)

        return products
    }

    fun searchProducts(request: SearchProductRequest): List<ProductEntity> {
        val builder = SearchProductQueryBuilder()
        val sql = builder.query(request)
        val params = builder.parameters(request)
        val query = em.createNativeQuery(sql, ProductEntity::class.java)
        Predicates.setParameters(query, params)
        val products = query.resultList as List<ProductEntity>

        return bubbleUpTaggedProduct(request, products)
    }

    private fun bubbleUpTaggedProduct(
        request: SearchProductRequest,
        products: List<ProductEntity>
    ): List<ProductEntity> {
        if (request.storyId == null || products.isEmpty()) {
            return products
        }

        // Story
        val story = storyDao.findById(request.storyId!!).getOrNull() ?: return products
        val storyTitle = SlugGenerator.generate("", story.title ?: "")

        // Product titles
        val productTitles = products.associate { it.id to mapper.toSlug(it) }

        // Sort product vs story distance
        val algo = LevenshteinDistance.getDefaultInstance()
        val sorted = products.map { it }.sortedWith { a, b ->
            algo.apply(storyTitle, productTitles[a.id]) - algo.apply(storyTitle, productTitles[b.id])
        }

        // Pick the 1st product as tagged product
        val tagged = sorted.first()

        // Result
        val result = mutableListOf<ProductEntity>()
        result.add(tagged)
        result.addAll(products.filter { it.id != tagged.id })
        return result
    }

    @Transactional
    fun onTransactionSuccessful(product: ProductEntity) {
        product.orderCount =
            transactionDao.countByProductAndTypeAndStatus(product, TransactionType.CHARGE, Status.SUCCESSFUL) ?: 0

        product.totalSales =
            transactionDao.sumNetByProductAndTypeAndStatus(product, TransactionType.CHARGE, Status.SUCCESSFUL) ?: 0

        product.modificationDateTime = Date()
        dao.save(product)
    }

    @Transactional
    fun onKpiImported(product: ProductEntity) {
        product.viewCount =
            productKpiDao.sumValueByProductIdAndTypeAndSource(product.id ?: -1, KpiType.VIEW, TrafficSource.ALL) ?: 0

        product.orderCount =
            transactionDao.countByProductAndTypeAndStatus(product, TransactionType.CHARGE, Status.SUCCESSFUL) ?: 0

        product.totalSales =
            transactionDao.sumNetByProductAndTypeAndStatus(product, TransactionType.CHARGE, Status.SUCCESSFUL) ?: 0

        product.modificationDateTime = Date()
        dao.save(product)
    }
}
