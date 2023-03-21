package com.wutsi.marketplace.access.delegate

import com.wutsi.marketplace.access.dto.UpdateProductStatusRequest
import com.wutsi.marketplace.access.service.ProductService
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class UpdateProductStatusDelegate(
    private val service: ProductService,
    private val logger: KVLogger,
) {
    @Transactional
    fun invoke(id: Long, request: UpdateProductStatusRequest) {
        logger.add("request_status", request.status)
        service.updateStatus(id, request)
    }
}
