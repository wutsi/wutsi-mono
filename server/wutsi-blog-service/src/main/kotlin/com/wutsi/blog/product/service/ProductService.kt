package com.wutsi.blog.product.service

import com.wutsi.blog.error.ErrorCode
import com.wutsi.blog.error.ErrorCode.PRODUCT_NOT_FOUND
import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.kpi.dao.ProductKpiRepository
import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.dto.TrafficSource
import com.wutsi.blog.product.dao.ProductRepository
import com.wutsi.blog.product.dao.SearchProductQueryBuilder
import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.product.domain.StoreEntity
import com.wutsi.blog.product.dto.CreateProductCommand
import com.wutsi.blog.product.dto.ProductAttributeUpdatedEventPayload
import com.wutsi.blog.product.dto.ProductStatus
import com.wutsi.blog.product.dto.ProductType
import com.wutsi.blog.product.dto.PublishProductCommand
import com.wutsi.blog.product.dto.SearchProductRequest
import com.wutsi.blog.product.dto.UpdateProductAttributeCommand
import com.wutsi.blog.product.mapper.ProductMapper
import com.wutsi.blog.story.dao.StoryRepository
import com.wutsi.blog.transaction.dao.TransactionRepository
import com.wutsi.blog.transaction.dto.TransactionType
import com.wutsi.blog.user.dto.UserAttributeUpdatedEvent
import com.wutsi.blog.util.Predicates
import com.wutsi.blog.util.StringUtils
import com.wutsi.event.store.Event
import com.wutsi.event.store.EventStore
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.exception.ConflictException
import com.wutsi.platform.core.error.exception.NotFoundException
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.storage.StorageService
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.platform.payment.core.Status
import jakarta.persistence.EntityManager
import org.apache.commons.io.IOUtils
import org.apache.commons.text.similarity.LevenshteinDistance
import org.apache.tika.Tika
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Optional
import java.util.UUID
import javax.imageio.ImageIO
import kotlin.jvm.optionals.getOrNull

@Service
class ProductService(
    private val dao: ProductRepository,
    private val storyDao: StoryRepository,
    private val storeService: StoreService,
    private val categoryService: CategoryService,
    private val logger: KVLogger,
    private val em: EntityManager,
    private val mapper: ProductMapper,
    private val transactionDao: TransactionRepository,
    private val productKpiDao: ProductKpiRepository,
    private val eventStore: EventStore,
    private val eventStream: EventStream,
    private val storage: StorageService,
    private val metadataExtractorProvider: DocumentMetadataExtractorProvider,
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
        Optional.ofNullable(
            dao.findByExternalIdAndStore(externalId, store).firstOrNull()
        )

    @Transactional
    fun create(command: CreateProductCommand): ProductEntity {
        logger.add("command", "CreateProductCommand")
        logger.add("command_title", command.title)
        logger.add("command_description", command.description)
        logger.add("command_price", command.price)
        logger.add("command_store_id", command.storeId)
        logger.add("command_category_id", command.categoryId)
        logger.add("command_type", command.type)
        logger.add("command_available", command.available)
        logger.add("command_external_id", command.externalId)

        val product = dao.save(
            ProductEntity(
                store = storeService.findById(command.storeId),
                category = categoryService.findById(command.categoryId),
                externalId = command.externalId,
                title = command.title,
                description = command.description,
                price = command.price,
                type = command.type,
                available = command.available,
                status = ProductStatus.DRAFT,
            )
        )
        notify(EventType.PRODUCT_CREATED_EVENT, product.id!!, command.timestamp)
        return product
    }

    @Transactional
    fun onProductCreated(payload: EventPayload) {
        val event = eventStore.event(payload.eventId)
        val product = dao.findById(event.entityId.toLong()).getOrNull() ?: return
        storeService.onProductsCreated(product.store)
    }

    @Transactional
    fun save(product: ProductEntity): ProductEntity {
        product.modificationDateTime = Date()
        return dao.save(product)
    }

    @Transactional
    fun publish(command: PublishProductCommand) {
        val product = findById(command.productId)
        if (product.status == ProductStatus.DRAFT) {
            checkCanPublish(product)

            product.status = ProductStatus.PUBLISHED
            product.modificationDateTime = Date()
            dao.save(product)

            notify(EventType.PRODUCT_PUBLISHED_EVENT, command.productId, command.timestamp)
        }
    }

    @Transactional
    fun onProductPublished(payload: EventPayload) {
        val event = eventStore.event(payload.eventId)
        val product = dao.findById(event.entityId.toLong()).getOrNull() ?: return
        storeService.onProductsCreated(product.store)
    }

    private fun checkCanPublish(product: ProductEntity) {
        if (product.title.isNullOrEmpty()) {
            throw conflict(product, ErrorCode.PRODUCT_TITLE_MISSING)
        }
        if (product.fileUrl.isNullOrEmpty()) {
            throw conflict(product, ErrorCode.PRODUCT_FILE_LINK_MISSING)
        }
        if (product.imageUrl.isNullOrEmpty()) {
            throw conflict(product, ErrorCode.PRODUCT_IMAGE_LINK_MISSING)
        }
        if (product.category == null) {
            throw conflict(product, ErrorCode.PRODUCT_CATEGORY_INVALID)
        }
    }

    private fun conflict(product: ProductEntity, errorCode: String) = ConflictException(
        error = Error(
            code = errorCode,
            parameter = Parameter(
                name = "productId",
                value = product.id
            )
        )
    )

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
        products: List<ProductEntity>,
    ): List<ProductEntity> {
        if (request.storyId == null || products.isEmpty()) {
            return products
        }

        // Story
        val story = storyDao.findById(request.storyId!!).getOrNull() ?: return products
        val storyTitle = StringUtils.generate("", story.title ?: "")

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
            transactionDao.sumAmountByProductAndTypeAndStatus(product, TransactionType.CHARGE, Status.SUCCESSFUL) ?: 0

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
            transactionDao.sumAmountByProductAndTypeAndStatus(product, TransactionType.CHARGE, Status.SUCCESSFUL) ?: 0

        product.modificationDateTime = Date()
        dao.save(product)
    }

    @Transactional
    fun updateAttribute(command: UpdateProductAttributeCommand) {
        logger.add("command_product_id", command.productId)
        logger.add("command_name", command.name)
        logger.add("command_value", command.value)

        set(command.productId, command.name, command.value)

        val payload = UserAttributeUpdatedEvent(
            name = command.name,
            value = command.value,
        )
        notify(EventType.PRODUCT_ATTRIBUTE_UPDATED_EVENT, command.productId, command.timestamp, payload)
    }

    private fun set(id: Long, name: String, value: String?): ProductEntity {
        val product = findById(id)
        val lname = name.lowercase()

        if ("title" == lname) {
            product.title = value ?: ""
        } else if ("description" == lname) {
            product.description = value?.ifEmpty { null }
        } else if ("category_id" == lname) {
            product.category = categoryService.findById(value!!.toLong())
        } else if ("available" == lname) {
            product.available = ("true" == value)
        } else if ("external_id" == lname) {
            product.externalId = value ?: ""
        } else if ("file_url" == lname) {
            product.fileUrl = value
            product.fileContentType = value?.ifEmpty { null }.let {
                Tika().detect(URL(value).file)
            }
        } else if ("price" == lname) {
            product.price = value?.toLong() ?: 0
        } else if ("image_url" == lname) {
            product.imageUrl = value
        } else if ("type" == lname) {
            product.type = value?.let { ProductType.valueOf(value.uppercase()) } ?: ProductType.UNKNOWN
        } else {
            throw ConflictException(Error(ErrorCode.PRODUCT_ATTRIBUTE_INVALID))
        }

        product.modificationDateTime = Date()
        return save(product)
    }

    @Transactional
    fun onAttributeUpdated(payload: EventPayload) {
        val event = eventStore.event(payload.eventId)
        val data = event.payload as ProductAttributeUpdatedEventPayload
        if ((data.name == "file_url") || (data.name == "image_url") && data.value.isNullOrEmpty()) {
            val product = dao.findById(event.entityId.toLong()).getOrNull() ?: return
            val path = downloadPath(product.store)
            when (data.name) {
                "file_url" -> downloadFile(data.value!!, path, product)
                "image_url" -> downloadImage(data.value!!, path, product)
                else -> return
            }
            save(product)
        }
    }

    fun downloadPath(store: StoreEntity): String =
        "product/import/" +
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) +
                "/store/${store.id}" +
                "/" + UUID.randomUUID()

    fun downloadImage(link: String, path: String, product: ProductEntity) {
        val url = URL(link)
        if (storage.contains(url)) {
            product.imageUrl = link
        }

        // Download
        val img = ImageIO.read(url)
        val file = File.createTempFile(UUID.randomUUID().toString(), ".png")
        val out = FileOutputStream(file)
        try {
            ImageIO.write(img, "png", out)

            // Store
            val input = FileInputStream(file)
            input.use {
                product.imageUrl = storage.store("$path/${file.name}", input).toString()
            }
        } finally {
            out.close()
        }
    }

    fun downloadFile(link: String, path: String, product: ProductEntity) {
        val url = URL(link)
        val ext = if (link.lastIndexOf(".") > 0) {
            link.substring(link.lastIndexOf("."))
        } else {
            ""
        }

        // Store locally
        val file = File.createTempFile(UUID.randomUUID().toString(), ext)
        val fout = FileOutputStream(file)
        fout.use {
            val cnn = url.openConnection() as HttpURLConnection
            try {
                IOUtils.copy(cnn.inputStream, fout)
                product.fileContentType = extractContentType(cnn.contentType)
                product.fileContentLength = cnn.contentLength.toLong()

                product.fileContentType?.let { contentType ->
                    val meta = metadataExtractorProvider.get(contentType)
                    meta?.extract(file, product)
                }
            } finally {
                cnn.disconnect()
            }
        }

        // Store to the cloud
        val input = FileInputStream(file)
        input.use {
            product.fileUrl = storage.store(
                "$path/${file.name}",
                input,
                contentType = product.fileContentType,
                contentLength = product.fileContentLength
            ).toString()
        }
    }

    private fun extractContentType(contentType: String?): String? {
        val i = contentType?.indexOf(';') ?: return null
        return if (i > 0) {
            contentType.substring(0, i).trim()
        } else {
            contentType
        }
    }

    private fun notify(type: String, productId: Long, timestamp: Long, payload: Any? = null) {
        val eventId = eventStore.store(
            Event(
                streamId = StreamId.PRODUCT,
                type = type,
                entityId = productId.toString(),
                payload = payload,
                timestamp = Date(timestamp)
            ),
        )

        eventStream.enqueue(type, EventPayload(eventId = eventId))
    }
}
