package com.wutsi.marketplace.access.service

import com.wutsi.enums.ProductStatus
import com.wutsi.enums.StoreStatus
import com.wutsi.marketplace.access.dao.ProductRepository
import com.wutsi.marketplace.access.dao.StoreRepository
import com.wutsi.marketplace.access.dto.CancellationPolicy
import com.wutsi.marketplace.access.dto.CreateStoreRequest
import com.wutsi.marketplace.access.dto.ReturnPolicy
import com.wutsi.marketplace.access.dto.SearchStoreRequest
import com.wutsi.marketplace.access.dto.Store
import com.wutsi.marketplace.access.dto.StoreSummary
import com.wutsi.marketplace.access.dto.UpdateStorePolicyAttributeRequest
import com.wutsi.marketplace.access.dto.UpdateStoreStatusRequest
import com.wutsi.marketplace.access.entity.StoreEntity
import com.wutsi.marketplace.access.error.ErrorURN
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.ParameterType
import com.wutsi.platform.core.error.exception.BadRequestException
import com.wutsi.platform.core.error.exception.NotFoundException
import org.springframework.stereotype.Service
import java.time.ZoneOffset
import java.util.Date
import javax.persistence.EntityManager
import javax.persistence.Query

@Service
class StoreService(
    private val dao: StoreRepository,
    private val productDao: ProductRepository,
    private val em: EntityManager,
) {
    fun findById(id: Long): StoreEntity =
        dao.findById(id)
            .orElseThrow {
                NotFoundException(
                    error = Error(
                        code = ErrorURN.STORE_NOT_FOUND.urn,
                        parameter = Parameter(
                            name = "id",
                            value = id,
                            type = ParameterType.PARAMETER_TYPE_PATH,
                        ),
                    ),
                )
            }

    fun create(request: CreateStoreRequest): StoreEntity {
        val stores =
            dao.findByAccountIdAndStatusNotIn(
                request.accountId,
                listOf(StoreStatus.UNKNOWN, StoreStatus.INACTIVE),
            )
        return if (stores.isEmpty()) {
            dao.save(
                StoreEntity(
                    accountId = request.accountId,
                    businessId = request.businessId,
                    currency = request.currency,
                    status = StoreStatus.ACTIVE,
                ),
            )
        } else {
            stores[0]
        }
    }

    fun updateStatus(id: Long, request: UpdateStoreStatusRequest) {
        val store = findById(id)
        val status = StoreStatus.valueOf(request.status)
        if (store.status == status) {
            return
        }

        store.status = status
        store.updated = Date()
        when (status) {
            StoreStatus.INACTIVE -> store.deactivated = Date()
            StoreStatus.UNDER_REVIEW -> store.deactivated = null
            StoreStatus.ACTIVE -> store.deactivated = null
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
        dao.save(store)
    }

    fun search(request: SearchStoreRequest): List<StoreEntity> {
        val sql = sql(request)
        val query = em.createQuery(sql)
        parameters(request, query)
        return query
            .setFirstResult(request.offset)
            .setMaxResults(request.limit)
            .resultList as List<StoreEntity>
    }

    private fun sql(request: SearchStoreRequest): String {
        val select = select()
        val where = where(request)
        return if (where.isNullOrEmpty()) {
            select
        } else {
            "$select WHERE $where"
        }
    }

    private fun select(): String =
        "SELECT P FROM StoreEntity P"

    private fun where(request: SearchStoreRequest): String {
        val criteria = mutableListOf<String>()

        if (!request.status.isNullOrEmpty()) {
            criteria.add("P.status = :status")
        }
        if (request.storeIds.isNotEmpty()) {
            criteria.add("P.id IN :store_ids")
        }
        if (request.businessId != null) {
            criteria.add("P.businessId = :business_id")
        }
        return criteria.joinToString(separator = " AND ")
    }

    private fun parameters(request: SearchStoreRequest, query: Query) {
        if (!request.status.isNullOrEmpty()) {
            query.setParameter("status", StoreStatus.valueOf(request.status.uppercase()))
        }
        if (request.storeIds.isNotEmpty()) {
            query.setParameter("store_ids", request.storeIds)
        }
        if (request.businessId != null) {
            query.setParameter("business_id", request.businessId)
        }
    }

    fun toStore(store: StoreEntity) = Store(
        id = store.id ?: -1,
        accountId = store.accountId,
        businessId = store.businessId,
        productCount = store.productCount,
        publishedProductCount = store.publishedProductCount,
        created = store.created.toInstant().atOffset(ZoneOffset.UTC),
        updated = store.updated.toInstant().atOffset(ZoneOffset.UTC),
        deactivated = store.deactivated?.toInstant()?.atOffset(ZoneOffset.UTC),
        currency = store.currency,
        status = store.status.name,
        cancellationPolicy = CancellationPolicy(
            accepted = store.cancellationAccepted,
            window = store.cancellationWindow,
            message = store.cancellationMessage,
        ),
        returnPolicy = ReturnPolicy(
            accepted = store.returnAccepted,
            contactWindow = store.returnContactWindow,
            shipBackWindow = store.returnShipBackWindow,
            message = store.returnMessage,
        ),
    )

    fun toStoreSummary(store: StoreEntity) = StoreSummary(
        id = store.id ?: -1,
        accountId = store.accountId,
        businessId = store.businessId,
        currency = store.currency,
        status = store.status.name,
        created = store.created.toInstant().atOffset(ZoneOffset.UTC),
    )

    fun updateProductCount(store: StoreEntity) {
        store.productCount = productDao.countByStoreAndIsDeleted(store, false)
        store.publishedProductCount =
            productDao.countByStoreAndIsDeletedAndStatus(store, false, ProductStatus.PUBLISHED)
        dao.save(store)
    }

    fun updatePolicyAttribute(id: Long, request: UpdateStorePolicyAttributeRequest) {
        val store = findById(id)
        when (request.name.lowercase()) {
            "cancellation-accepted" -> store.cancellationAccepted = toBoolean(request.value)
            "cancellation-window" -> store.cancellationWindow = (toInt(request.value) ?: 12)
            "cancellation-message" -> store.cancellationMessage = toString(request.value)
            "return-accepted" -> store.returnAccepted = toBoolean(request.value)
            "return-contact-window" -> store.returnContactWindow = (toInt(request.value) ?: 24)
            "return-ship-back-window" -> store.returnShipBackWindow = (toInt(request.value) ?: 240)
            "return-message" -> store.returnMessage = toString(request.value)

            else -> BadRequestException(
                error = Error(
                    code = ErrorURN.ATTRIBUTE_NOT_VALID.name,
                    parameter = Parameter(
                        name = "name",
                        value = request.name,
                        type = ParameterType.PARAMETER_TYPE_PAYLOAD,
                    ),
                ),
            )
        }
        store.updated = Date()
        dao.save(store)
    }

    private fun toString(value: String?): String? =
        if (value.isNullOrEmpty()) {
            null
        } else {
            value
        }

    private fun toInt(value: String?): Int? =
        if (value.isNullOrEmpty()) {
            null
        } else {
            value.toInt()
        }

    private fun toBoolean(value: String?): Boolean =
        if (value.isNullOrEmpty()) {
            false
        } else {
            value.toBoolean()
        }
}
