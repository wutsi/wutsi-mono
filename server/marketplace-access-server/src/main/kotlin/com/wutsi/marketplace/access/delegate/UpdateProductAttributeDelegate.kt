package com.wutsi.marketplace.access.delegate

import com.wutsi.marketplace.access.dto.UpdateProductAttributeRequest
import com.wutsi.marketplace.access.service.ProductService
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class UpdateProductAttributeDelegate(
    private val service: ProductService,
    private val logger: KVLogger,
) {
    @Transactional
    fun invoke(
        id: Long,
        request: UpdateProductAttributeRequest,
    ) {
        logger.add("request_value", request.name)
        logger.add("request_value", request.value)

        service.updateAttribute(id, request)
    }
}
