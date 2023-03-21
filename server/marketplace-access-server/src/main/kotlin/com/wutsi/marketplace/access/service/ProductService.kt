package com.wutsi.marketplace.access.service

import com.wutsi.enums.ProductSort
import com.wutsi.enums.ProductStatus
import com.wutsi.enums.ProductType
import com.wutsi.enums.StoreStatus
import com.wutsi.marketplace.access.dao.ProductRepository
import com.wutsi.marketplace.access.dto.CheckProductAvailabilityRequest
import com.wutsi.marketplace.access.dto.CreateProductRequest
import com.wutsi.marketplace.access.dto.Event
import com.wutsi.marketplace.access.dto.Product
import com.wutsi.marketplace.access.dto.ProductSummary
import com.wutsi.marketplace.access.dto.SearchProductRequest
import com.wutsi.marketplace.access.dto.UpdateProductAttributeRequest
import com.wutsi.marketplace.access.dto.UpdateProductEventRequest
import com.wutsi.marketplace.access.dto.UpdateProductStatusRequest
import com.wutsi.marketplace.access.entity.PictureEntity
import com.wutsi.marketplace.access.entity.ProductEntity
import com.wutsi.marketplace.access.entity.ReservationEntity
import com.wutsi.marketplace.access.error.ErrorURN
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.ParameterType
import com.wutsi.platform.core.error.exception.BadRequestException
import com.wutsi.platform.core.error.exception.ConflictException
import com.wutsi.platform.core.error.exception.NotFoundException
import com.wutsi.platform.core.storage.StorageService
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.UUID
import javax.persistence.EntityManager
import javax.persistence.Query

@Service
class ProductService(
    private val dao: ProductRepository,
    private val categoryService: CategoryService,
    private val pictureService: PictureService,
    private val storeService: StoreService,
    private val meetingProviderService: MeetingProviderService,
    private val fileService: FileService,
    private val em: EntityManager,
    private val storage: StorageService,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ProductService::class.java)
    }

    fun create(request: CreateProductRequest): ProductEntity {
        val store = storeService.findById(request.storeId)
        val product = dao.save(
            ProductEntity(
                title = request.title,
                summary = request.summary,
                status = ProductStatus.DRAFT,
                price = request.price,
                category = request.categoryId?.let { categoryService.findById(it) },
                store = store,
                currency = store.currency,
                quantity = request.quantity,
                type = ProductType.valueOf(request.type),
            ),
        )

        if (!request.pictureUrl.isNullOrEmpty()) {
            product.thumbnail = pictureService.create(product, request.pictureUrl)
            product.updated = Date()
            dao.save(product)
        }

        storeService.updateProductCount(store)
        return product
    }

    fun delete(id: Long) {
        val product = findById(id)
        product.isDeleted = true
        product.deleted = Date()
        product.updated = Date()
        dao.save(product)

        storeService.updateProductCount(product.store)
    }

    fun findById(id: Long): ProductEntity {
        val product = dao.findById(id)
            .orElseThrow {
                NotFoundException(
                    error = Error(
                        code = ErrorURN.PRODUCT_NOT_FOUND.urn,
                        parameter = Parameter(
                            name = "id",
                            value = id,
                            type = ParameterType.PARAMETER_TYPE_PATH,
                        ),
                    ),
                )
            }

        if (product.isDeleted) {
            throw NotFoundException(
                error = Error(
                    code = ErrorURN.PRODUCT_DELETED.urn,
                    parameter = Parameter(
                        name = "id",
                        value = id,
                        type = ParameterType.PARAMETER_TYPE_PATH,
                    ),
                ),
            )
        }

        return product
    }

    fun toProduct(product: ProductEntity, language: String?) = Product(
        id = product.id ?: -1,
        title = product.title ?: "",
        summary = product.summary,
        price = product.price,
        currency = product.currency,
        status = product.status.name,
        store = storeService.toStoreSummary(product.store),
        category = product.category?.let { categoryService.toCategorySummary(it, language) },
        created = product.created.toInstant().atOffset(ZoneOffset.UTC),
        updated = product.updated.toInstant().atOffset(ZoneOffset.UTC),
        published = product.published?.toInstant()?.atOffset(ZoneOffset.UTC),
        description = product.description,
        quantity = product.quantity,
        type = product.type.name,
        event = toEvent(product),
        thumbnail = product.thumbnail?.let { pictureService.toPictureSummary(it) },
        pictures = product.pictures
            .filter { !it.isDeleted }
            .map { pictureService.toPictureSummary(it) },
        files = if (product.type == ProductType.DIGITAL_DOWNLOAD) {
            product.files
                .filter { !it.isDeleted }
                .map { fileService.toFileSummary(it) }
        } else {
            emptyList()
        },
        totalSales = product.totalSales,
        totalOrders = product.totalOrders,
        totalUnits = product.totalUnits,
        totalViews = product.totalViews,
        outOfStock = product.outOfScope,
        url = "/p/${product.id}/" + (product.title?.let { HandleGenerator.generate(it) } ?: ""),
    )

    fun toProductSummary(product: ProductEntity, language: String?) = ProductSummary(
        id = product.id ?: -1,
        title = product.title ?: "",
        summary = product.summary,
        price = product.price,
        currency = product.currency,
        status = product.status.name,
        storeId = product.store.id ?: -1,
        categoryId = product.category?.id,
        quantity = product.quantity,
        thumbnailUrl = product.thumbnail?.url,
        type = product.type.name,
        event = toEvent(product),
        outOfStock = product.outOfScope,
        url = "/p/${product.id}/" + (product.title?.let { HandleGenerator.generate(it) } ?: ""),
    )

    private fun toEvent(product: ProductEntity): Event? {
        if (product.type != ProductType.EVENT) {
            return null
        }

        return Event(
            online = product.eventOnline,
            meetingId = product.eventMeetingId ?: "",
            meetingPassword = product.eventMeetingPassword,
            ends = product.eventEnds?.toInstant()?.atOffset(ZoneOffset.UTC),
            starts = product.eventStarts?.toInstant()?.atOffset(ZoneOffset.UTC),
            meetingJoinUrl = meetingProviderService.toJoinUrl(product),
            meetingProvider = product.eventMeetingProvider?.let {
                meetingProviderService.toMeetingProviderSummary(it)
            },
        )
    }

    fun updateAttribute(id: Long, request: UpdateProductAttributeRequest) {
        val product = findById(id)
        updateAttribute(product, request)
    }

    fun updateEvent(product: ProductEntity, request: UpdateProductEventRequest) {
        product.eventOnline = request.online
        product.eventStarts = request.starts?.let { Date(it.toInstant().toEpochMilli()) }
        product.eventEnds = request.ends?.let { Date(it.toInstant().toEpochMilli()) }
        product.eventMeetingId = request.meetingId
        product.eventMeetingPassword = request.meetingPassword
        product.eventMeetingProvider = request.meetingProviderId?.let {
            meetingProviderService.findById(it)
        }
        product.updated = Date()
        dao.save(product)
    }

    fun updateAttribute(product: ProductEntity, request: UpdateProductAttributeRequest) {
        when (request.name.lowercase()) {
            "title" -> product.title = toString(request.value) ?: "NO TITLE"
            "summary" -> product.summary = toString(request.value)
            "description" -> product.description = toString(request.value)
            "price" -> product.price = toLong(request.value)
            "thumbnail-id" -> product.thumbnail = toLong(request.value)?.let { pictureService.findById(it) }
            "category-id" -> product.category = toLong(request.value)?.let { categoryService.findById(it) }
            "quantity" -> product.quantity = toInt(request.value) ?: 0
            "type" -> product.type = toString(request.value)
                ?.let { ProductType.valueOf(it.uppercase()) }
                ?: ProductType.PHYSICAL_PRODUCT
            else -> throw BadRequestException(
                error = Error(
                    code = ErrorURN.ATTRIBUTE_NOT_VALID.urn,
                    parameter = Parameter(
                        name = "name",
                        value = request.name,
                        type = ParameterType.PARAMETER_TYPE_PAYLOAD,
                    ),
                ),
            )
        }
        product.updated = Date()
        dao.save(product)
    }

    fun setThumbnail(product: ProductEntity, picture: PictureEntity?) {
        product.thumbnail = picture
        product.updated = Date()
        dao.save(product)
    }

    fun updateStatus(id: Long, request: UpdateProductStatusRequest) {
        val product = findById(id)
        val status = ProductStatus.valueOf(request.status.uppercase())
        if (status == product.status) {
            return
        }

        when (status) {
            ProductStatus.DRAFT -> product.published = null
            ProductStatus.PUBLISHED -> product.published = Date()

            else -> throw BadRequestException(
                error = Error(
                    code = ErrorURN.STATUS_NOT_VALID.urn,
                    parameter = Parameter(
                        name = "status",
                        value = request.status,
                        type = ParameterType.PARAMETER_TYPE_PAYLOAD,
                    ),
                ),
            )
        }
        product.status = status
        product.updated = Date()
        dao.save(product)
        storeService.updateProductCount(product.store)
    }

    fun checkAvailability(request: CheckProductAvailabilityRequest) {
        val productMap = search(
            request = SearchProductRequest(
                productIds = request.items.map { it.productId },
                limit = request.items.size,
            ),
        ).associateBy { it.id }

        request.items.forEach {
            val product = productMap[it.productId]
                ?: throw availabilityException(it.productId)

            if (product.quantity != null && product.quantity!! < it.quantity) {
                throw availabilityException(it.productId, product.quantity)
            }
        }
    }

    fun decrementStock(reservation: ReservationEntity) {
        val products = mutableListOf<ProductEntity>()
        reservation.items.forEach {
            val product = it.product
            if (product.quantity != null) {
                product.quantity = product.quantity!! - it.quantity
                product.updated = Date()
                if (product.quantity!! < 0) {
                    throw availabilityException(product.id!!, product.quantity)
                } else {
                    products.add(product)
                }
            }
        }

        if (products.isNotEmpty()) {
            dao.saveAll(products)
        }
    }

    fun incrementStock(reservation: ReservationEntity) {
        val products = mutableListOf<ProductEntity>()
        reservation.items.forEach {
            val product = it.product
            if (product.quantity != null) {
                product.quantity = product.quantity!! + it.quantity
                product.updated = Date()
                products.add(product)
            }
        }

        if (products.isNotEmpty()) {
            dao.saveAll(products)
        }
    }

    fun search(request: SearchProductRequest): List<ProductEntity> {
        val sql = sql(request)
        val query = em.createQuery(sql)
        parameters(request, query)
        return query
            .setFirstResult(request.offset)
            .setMaxResults(request.limit)
            .resultList as List<ProductEntity>
    }

    fun importSalesKpi(date: LocalDate): Long {
        val path = "kpi/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "/sales.csv"
        try {
            val file = downloadFromStorage(path)
            try {
                val result = importSalesKpi(file)
                LOGGER.info("$result KPIs loaded for $date from $path")

                return result
            } finally {
                file.delete()
            }
        } catch (ex: Exception) {
            LOGGER.warn("Unable to log KPIs for $date from $path", ex)
        }
        return 0L
    }

    private fun downloadFromStorage(path: String): File {
        val file = File.createTempFile(UUID.randomUUID().toString(), "csv")
        val out = FileOutputStream(file)
        out.use {
            val url = storage.toURL(path)
            LOGGER.info("Downloading $url to $file")
            storage.get(url, out)
        }
        return file
    }

    private fun importSalesKpi(file: File): Long {
        var result = 0L
        val parser = CSVParser.parse(
            file.toPath(),
            Charsets.UTF_8,
            CSVFormat.Builder.create()
                .setSkipHeaderRecord(true)
                .setDelimiter(",")
                .setHeader("business_id", "product_id", "total_orders", "total_units", "total_sale", "total_views")
                .build(),
        )
        parser.use {
            for (record in parser) {
                try {
                    updateSales(record)
                    result++
                } catch (ex: Exception) {
                    LOGGER.warn("Unable to line $record", ex)
                }
            }
            return result
        }
    }

    private fun updateSales(record: CSVRecord) {
        val productId = record.get(1).toLong()
        val product = findById(productId)
        product.totalOrders = record.get(2).toLong()
        product.totalUnits = record.get(3).toLong()
        product.totalSales = record.get(4).toLong()
        product.totalViews = record.get(5).toLong()
        product.updated = Date()
        dao.save(product)
    }

    private fun sql(request: SearchProductRequest): String {
        val select = select()
        val where = where(request)
        val orderBy = orderBy(request)
        return if (where.isEmpty()) {
            select
        } else {
            "$select WHERE $where ORDER BY $orderBy"
        }
    }

    private fun select(): String =
        "SELECT P FROM ProductEntity P"

    private fun where(request: SearchProductRequest): String {
        val criteria = mutableListOf("P.isDeleted=false") // Product not deleted
        criteria.add("P.store.status=:store_status") // Store status

        if (request.storeId != null) {
            criteria.add("P.store.id = :store_id")
        }

        if (request.productIds.isNotEmpty()) {
            criteria.add("P.id IN :product_ids")
        }

        if (request.categoryIds.isNotEmpty()) {
            criteria.add("P.category.id IN :category_ids")
        }

        if (!request.status.isNullOrEmpty()) {
            criteria.add("P.status=:status")
        }

        if (request.types.isNotEmpty()) {
            criteria.add("P.type IN :types")
        }

        return criteria.joinToString(separator = " AND ")
    }

    private fun orderBy(request: SearchProductRequest): String =
        if (ProductSort.PRICE_DESC.name.equals(request.sortBy, true)) {
            "P.price DESC"
        } else if (ProductSort.PRICE_ASC.name.equals(request.sortBy, true)) {
            "P.price ASC"
        } else {
            "P.title"
        }

    private fun parameters(request: SearchProductRequest, query: Query) {
        query.setParameter("store_status", StoreStatus.ACTIVE)

        if (request.storeId != null) {
            query.setParameter("store_id", request.storeId)
        }

        if (request.productIds.isNotEmpty()) {
            query.setParameter("product_ids", request.productIds)
        }

        if (request.categoryIds.isNotEmpty()) {
            query.setParameter("category_ids", request.categoryIds)
        }

        if (!request.status.isNullOrEmpty()) {
            query.setParameter("status", ProductStatus.valueOf(request.status.uppercase()))
        }

        if (request.types.isNotEmpty()) {
            query.setParameter("types", request.types.map { ProductType.valueOf(it) })
        }
    }

    private fun availabilityException(productId: Long, quantity: Int? = null) = ConflictException(
        error = Error(
            code = ErrorURN.PRODUCT_NOT_AVAILABLE.urn,
            data = mapOf(
                "product-id" to productId,
                "quantity" to (quantity ?: ""),
            ),
        ),
    )

    private fun toString(value: String?): String? =
        if (value.isNullOrEmpty()) {
            null
        } else {
            value
        }

    private fun toLong(value: String?): Long? =
        if (value.isNullOrEmpty()) {
            null
        } else {
            value.toLong()
        }

    private fun toInt(value: String?): Int? =
        if (value.isNullOrEmpty()) {
            null
        } else {
            value.toInt()
        }
}
