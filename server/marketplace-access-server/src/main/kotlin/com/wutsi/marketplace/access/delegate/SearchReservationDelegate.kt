package com.wutsi.marketplace.access.delegate

import com.wutsi.marketplace.access.dto.ReservationSummary
import com.wutsi.marketplace.access.dto.SearchReservationRequest
import com.wutsi.marketplace.access.dto.SearchReservationResponse
import com.wutsi.marketplace.access.service.ReservationService
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Service

@Service
public class SearchReservationDelegate(
    private val logger: KVLogger,
    private val service: ReservationService,
) {
    public fun invoke(request: SearchReservationRequest): SearchReservationResponse {
        logger.add("request_order_id", request.orderId)
        logger.add("request_limit", request.limit)
        logger.add("request_offset", request.offset)

        val reservations = service.search(request)
        logger.add("response_count", reservations.size)
        return SearchReservationResponse(
            reservations = reservations.map {
                ReservationSummary(
                    id = it.id ?: -1,
                    orderId = it.orderId,
                )
            },
        )
    }
}
