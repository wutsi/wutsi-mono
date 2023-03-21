package com.wutsi.marketplace.access.delegate

import com.wutsi.marketplace.access.dto.UpdateProductEventRequest
import com.wutsi.marketplace.access.service.ProductService
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
public class UpdateProductEventDelegate(
    private val service: ProductService,
    private val logger: KVLogger,
) {
    @Transactional
    public fun invoke(id: Long, request: UpdateProductEventRequest) {
        logger.add("request_online", request.online)
        logger.add("request_meeting_id", request.meetingId)
        logger.add("request_meeting_password", request.meetingPassword)
        logger.add("request_meeting_provider_id", request.meetingProviderId)
        logger.add("request_starts", request.starts)
        logger.add("request_end", request.ends)

        val product = service.findById(id)
        service.updateEvent(product, request)
    }
}
