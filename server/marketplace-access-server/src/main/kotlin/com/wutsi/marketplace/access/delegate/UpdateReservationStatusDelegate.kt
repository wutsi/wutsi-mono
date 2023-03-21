package com.wutsi.marketplace.access.delegate

import com.wutsi.marketplace.access.dto.UpdateReservationStatusRequest
import com.wutsi.marketplace.access.service.ReservationService
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class UpdateReservationStatusDelegate(
    private val service: ReservationService,
    private val logger: KVLogger,
) {
    @Transactional
    fun invoke(id: Long, request: UpdateReservationStatusRequest) {
        logger.add("request_status", request.status)
        service.updateStatus(id, request)
    }
}
