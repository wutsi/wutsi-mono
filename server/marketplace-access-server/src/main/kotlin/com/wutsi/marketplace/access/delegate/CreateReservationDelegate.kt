package com.wutsi.marketplace.access.delegate

import com.wutsi.marketplace.access.dto.CreateReservationRequest
import com.wutsi.marketplace.access.dto.CreateReservationResponse
import com.wutsi.marketplace.access.service.ProductService
import com.wutsi.marketplace.access.service.ReservationService
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class CreateReservationDelegate(
    private val service: ReservationService,
    private val productService: ProductService,
    private val logger: KVLogger,
) {
    @Transactional
    fun invoke(request: CreateReservationRequest): CreateReservationResponse {
        log(request)

        val reservation = service.create(request)
        productService.decrementStock(reservation)

        logger.add("reservation_id", reservation.id)
        return CreateReservationResponse(
            reservationId = reservation.id!!,
        )
    }

    private fun log(request: CreateReservationRequest) {
        logger.add("request_order_id", request.orderId)
        var i = 1
        request.items.forEach {
            logger.add("request_product_${i}_id", it.productId)
            logger.add("request_product_${i}_quantity", it.quantity)
            i++
        }
    }
}
