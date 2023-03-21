package com.wutsi.marketplace.access.delegate

import com.wutsi.marketplace.access.dto.CreateStoreRequest
import com.wutsi.marketplace.access.dto.CreateStoreResponse
import com.wutsi.marketplace.access.service.StoreService
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class CreateStoreDelegate(
    private val service: StoreService,
    private val logger: KVLogger,
) {
    @Transactional
    fun invoke(request: CreateStoreRequest): CreateStoreResponse {
        logger.add("request_account_id", request.accountId)
        logger.add("request_currency", request.currency)

        val store = service.create(request)
        logger.add("store_id", store.id)
        return CreateStoreResponse(
            storeId = store.id ?: -1,
        )
    }
}
