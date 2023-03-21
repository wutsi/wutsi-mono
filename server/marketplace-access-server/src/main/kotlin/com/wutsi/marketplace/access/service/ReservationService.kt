package com.wutsi.marketplace.access.service

import com.wutsi.enums.ReservationStatus
import com.wutsi.marketplace.access.dao.ReservationItemRepository
import com.wutsi.marketplace.access.dao.ReservationRepository
import com.wutsi.marketplace.access.dto.CreateReservationRequest
import com.wutsi.marketplace.access.dto.SearchProductRequest
import com.wutsi.marketplace.access.dto.SearchReservationRequest
import com.wutsi.marketplace.access.dto.UpdateReservationStatusRequest
import com.wutsi.marketplace.access.entity.ReservationEntity
import com.wutsi.marketplace.access.entity.ReservationItemEntity
import com.wutsi.marketplace.access.error.ErrorURN
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.ParameterType
import com.wutsi.platform.core.error.exception.BadRequestException
import com.wutsi.platform.core.error.exception.ConflictException
import com.wutsi.platform.core.error.exception.NotFoundException
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Service
import java.util.Date
import javax.persistence.EntityManager
import javax.persistence.Query

@Service
class ReservationService(
    private val dao: ReservationRepository,
    private val itemDao: ReservationItemRepository,
    private val productService: ProductService,
    private val em: EntityManager,
    private val logger: KVLogger,
) {
    fun create(request: CreateReservationRequest): ReservationEntity {
        // Reservation
        val reservation = dao.save(
            ReservationEntity(
                orderId = request.orderId,
                status = ReservationStatus.ACTIVE,
            ),
        )

        // Items
        val productMap = productService.search(
            request = SearchProductRequest(
                productIds = request.items.map { it.productId },
                limit = request.items.size,
            ),
        ).associateBy { it.id }
        reservation.items = request.items.map {
            ReservationItemEntity(
                reservation = reservation,
                quantity = it.quantity,
                product = productMap[it.productId]
                    ?: throw ConflictException(
                        error = Error(
                            code = ErrorURN.PRODUCT_NOT_AVAILABLE.urn,
                            data = mapOf(
                                "product-id" to it.productId,
                            ),
                        ),
                    ),
            )
        }
        itemDao.saveAll(reservation.items)

        return reservation
    }

    fun updateStatus(id: Long, request: UpdateReservationStatusRequest) {
        val reservation = findById(id)
        val status = ReservationStatus.valueOf(request.status.uppercase())
        if (status == reservation.status) {
            return
        }

        reservation.status = status
        when (status) {
            ReservationStatus.CANCELLED -> {
                reservation.cancelled = Date()
                productService.incrementStock(reservation)
            }
            ReservationStatus.ACTIVE -> {}
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
        dao.save(reservation)
    }

    fun findById(id: Long): ReservationEntity =
        dao.findById(id)
            .orElseThrow {
                NotFoundException(
                    error = Error(
                        code = ErrorURN.RESERVATION_NOT_FOUND.urn,
                        parameter = Parameter(
                            name = "id",
                            value = id,
                            type = ParameterType.PARAMETER_TYPE_PATH,
                        ),
                    ),
                )
            }

    fun search(request: SearchReservationRequest): List<ReservationEntity> {
        val sql = sql(request)
        val query = em.createQuery(sql)
        parameters(request, query)
        return query
            .setFirstResult(request.offset)
            .setMaxResults(request.limit)
            .resultList as List<ReservationEntity>
    }

    private fun sql(request: SearchReservationRequest): String {
        val select = select()
        val where = where(request)
        return if (where.isNullOrEmpty()) {
            select
        } else {
            "$select WHERE $where"
        }
    }

    private fun select(): String =
        "SELECT P FROM ReservationEntity P"

    private fun where(request: SearchReservationRequest): String {
        val criteria = mutableListOf<String>()
        if (!request.orderId.isNullOrEmpty()) {
            criteria.add("P.orderId = :order_id")
        }

        return criteria.joinToString(separator = " AND ")
    }

    private fun parameters(request: SearchReservationRequest, query: Query) {
        if (!request.orderId.isNullOrEmpty()) {
            query.setParameter("order_id", request.orderId)
        }
    }
}
