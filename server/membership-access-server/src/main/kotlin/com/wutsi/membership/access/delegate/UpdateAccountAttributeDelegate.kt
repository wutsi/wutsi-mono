package com.wutsi.membership.access.delegate

import com.wutsi.membership.access.dto.UpdateAccountAttributeRequest
import com.wutsi.membership.access.service.AccountService
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Service

@Service
class UpdateAccountAttributeDelegate(
    private val service: AccountService,
    private val logger: KVLogger,
) {
    fun invoke(id: Long, request: UpdateAccountAttributeRequest) {
        logger.add("request_value", request.value)
        logger.add("request_name", request.name)

        service.update(id, request)
    }
}
