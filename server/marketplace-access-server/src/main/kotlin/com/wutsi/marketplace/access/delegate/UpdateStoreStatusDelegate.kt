package com.wutsi.marketplace.access.delegate

import com.wutsi.marketplace.access.dto.UpdateStoreStatusRequest
import com.wutsi.marketplace.access.service.StoreService
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
public class UpdateStoreStatusDelegate(private val service: StoreService, private val logger: KVLogger) {
    @Transactional
    public fun invoke(id: Long, request: UpdateStoreStatusRequest) {
        logger.add("status", request.status)
        service.updateStatus(id, request)
    }
}
