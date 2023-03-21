package com.wutsi.marketplace.access.delegate

import com.wutsi.marketplace.access.dto.UpdateDiscountAttributeRequest
import com.wutsi.marketplace.access.service.DiscountService
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Service

@Service
public class UpdateDiscountAttributeDelegate(
    private val logger: KVLogger,
    private val service: DiscountService,
) {
    public fun invoke(id: Long, request: UpdateDiscountAttributeRequest) {
        logger.add("request_name", request.name)
        logger.add("request_value", request.value)

        service.updateAttribute(id, request)
    }
}
