package com.wutsi.marketplace.access.service

import com.wutsi.enums.DiscountType
import com.wutsi.marketplace.access.dao.DiscountRepository
import com.wutsi.marketplace.access.dto.CreateDiscountRequest
import com.wutsi.marketplace.access.dto.Discount
import com.wutsi.marketplace.access.dto.DiscountSummary
import com.wutsi.marketplace.access.dto.SearchDiscountRequest
import com.wutsi.marketplace.access.dto.UpdateDiscountAttributeRequest
import com.wutsi.marketplace.access.entity.DiscountEntity
import com.wutsi.marketplace.access.error.ErrorURN
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.ParameterType
import com.wutsi.platform.core.error.exception.BadRequestException
import com.wutsi.platform.core.error.exception.NotFoundException
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.Date
import javax.persistence.EntityManager
import javax.persistence.Query

@Service
class DiscountService(
    private val dao: DiscountRepository,
    private val storeService: StoreService,
    private val productService: ProductService,
    private val em: EntityManager,
) {
    fun create(request: CreateDiscountRequest): DiscountEntity =
        dao.save(
            DiscountEntity(
                store = storeService.findById(request.storeId),
                starts = request.starts?.let { Date(it.toInstant().toEpochMilli()) },
                ends = request.ends?.let { Date(it.toInstant().toEpochMilli()) },
                name = request.name,
                rate = request.rate,
                type = DiscountType.valueOf(request.type.uppercase()),
                allProducts = request.allProducts,
                created = Date(),
                updated = Date(),
            ),
        )

    fun updateAttribute(id: Long, request: UpdateDiscountAttributeRequest) {
        val discount = findById(id)
        when (request.name.lowercase()) {
            "starts" -> discount.starts = toDate(request.value)
            "ends" -> discount.ends = toDate(request.value)
            "rate" -> discount.rate = toInt(request.value) ?: 0
            "name" -> discount.name = request.value ?: ""
            "all-products" -> discount.allProducts = toBoolean(request.value)
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
        discount.updated = Date()
        dao.save(discount)
    }

    fun delete(id: Long) {
        val opt = dao.findById(id)
        if (opt.isPresent) {
            val discount = opt.get()
            if (!discount.isDeleted) {
                discount.isDeleted = true
                discount.deleted = Date()
                discount.updated = Date()
                dao.save(discount)
            }
        }
    }

    fun findById(id: Long): DiscountEntity {
        val discount = dao.findById(id)
            .orElseThrow {
                NotFoundException(
                    error = Error(
                        code = ErrorURN.DISCOUNT_NOT_FOUND.urn,
                        parameter = Parameter(
                            name = "id",
                            value = id,
                            type = ParameterType.PARAMETER_TYPE_PATH,
                        ),
                    ),
                )
            }
        if (discount.isDeleted) {
            throw NotFoundException(
                error = Error(
                    code = ErrorURN.DISCOUNT_DELETED.urn,
                    parameter = Parameter(
                        name = "id",
                        value = id,
                        type = ParameterType.PARAMETER_TYPE_PATH,
                    ),
                ),
            )
        }
        return discount
    }

    fun toDiscount(discount: DiscountEntity) = Discount(
        id = discount.id,
        name = discount.name,
        storeId = discount.store.id ?: -1,
        starts = discount.starts?.toInstant()?.atOffset(ZoneOffset.UTC),
        ends = discount.ends?.toInstant()?.atOffset(ZoneOffset.UTC),
        rate = discount.rate,
        allProducts = discount.allProducts,
        type = discount.type.name,
        productIds = discount.products.filter { !it.isDeleted }.mapNotNull { it.id },
        created = OffsetDateTime.ofInstant(discount.created.toInstant(), ZoneId.of("UTC")),
        updated = OffsetDateTime.ofInstant(discount.updated.toInstant(), ZoneId.of("UTC")),
    )

    fun toDiscountSummary(discount: DiscountEntity) = DiscountSummary(
        id = discount.id,
        name = discount.name,
        storeId = discount.store.id ?: -1,
        starts = discount.starts?.toInstant()?.atOffset(ZoneOffset.UTC),
        ends = discount.ends?.toInstant()?.atOffset(ZoneOffset.UTC),
        rate = discount.rate,
        created = OffsetDateTime.ofInstant(discount.created.toInstant(), ZoneId.of("UTC")),
        type = discount.type.name,
    )

    fun addProduct(discountId: Long, productId: Long) {
        val discount = findById(discountId)
        val product = productService.findById(productId)
        if (!discount.products.contains(product)) {
            discount.products.add(product)
            dao.save(discount)
        }
    }

    fun removeProduct(discountId: Long, productId: Long) {
        val discount = findById(discountId)
        val product = productService.findById(productId)
        if (discount.products.remove(product)) {
            dao.save(discount)
        }
    }

    fun search(request: SearchDiscountRequest): List<DiscountEntity> {
        val sql = sql(request)
        val query = em.createQuery(sql)
        parameters(request, query)
        return query
            .setFirstResult(request.offset)
            .setMaxResults(request.limit)
            .resultList as List<DiscountEntity>
    }

    private fun sql(request: SearchDiscountRequest): String {
        val select = select(request)
        val where = where(request)
        return if (where.isEmpty()) {
            select
        } else {
            "$select WHERE $where"
        }
    }

    private fun select(request: SearchDiscountRequest): String =
        if (request.productIds.isEmpty()) {
            "SELECT D FROM DiscountEntity D"
        } else {
            "SELECT D FROM DiscountEntity D LEFT JOIN D.products P"
        }

    private fun where(request: SearchDiscountRequest): String {
        val criteria = mutableListOf("D.isDeleted=false") // Discount not deleted

        if (request.storeId != null) {
            criteria.add("D.store.id = :store_id")
        }

        if (request.productIds.isNotEmpty()) {
            criteria.add("(D.allProducts=true OR P.id IN :product_ids)")
        }

        if (request.discountIds.isNotEmpty()) {
            criteria.add("(D.id IN :discount_ids)")
        }

        if (request.date != null) {
            criteria.add("D.starts <= :date AND (D.ends > :date OR D.ends IS NULL)")
        }

        if (request.type != null) {
            criteria.add("D.type = :type")
        }

        return criteria.joinToString(separator = " AND ")
    }

    private fun parameters(request: SearchDiscountRequest, query: Query) {
        if (request.storeId != null) {
            query.setParameter("store_id", request.storeId)
        }

        if (request.productIds.isNotEmpty()) {
            query.setParameter("product_ids", request.productIds)
        }

        if (request.discountIds.isNotEmpty()) {
            query.setParameter("discount_ids", request.discountIds)
        }

        if (request.date != null) {
            query.setParameter("date", Date.from(request.date.atStartOfDay(ZoneId.of("UTC")).toInstant()))
        }

        if (request.type != null) {
            query.setParameter("type", DiscountType.valueOf(request.type.uppercase()))
        }
    }

    private fun toInt(value: String?): Int? =
        if (value.isNullOrEmpty()) {
            null
        } else {
            value.toInt()
        }

    private fun toDate(value: String?): Date? {
        if (value.isNullOrEmpty()) {
            return null
        }
        val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm:00")
        return fmt.parse(value)
    }

    private fun toBoolean(value: String?): Boolean =
        if (value.isNullOrEmpty()) {
            false
        } else {
            value.toBoolean()
        }
}
