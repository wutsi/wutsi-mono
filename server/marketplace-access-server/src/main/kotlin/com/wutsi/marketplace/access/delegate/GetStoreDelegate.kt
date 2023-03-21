package com.wutsi.marketplace.access.delegate

import com.wutsi.marketplace.access.dto.GetStoreResponse
import com.wutsi.marketplace.access.service.StoreService
import org.springframework.stereotype.Service

@Service
class GetStoreDelegate(private val service: StoreService) {
    fun invoke(id: Long): GetStoreResponse {
        val store = service.findById(id)
        return GetStoreResponse(
            store = service.toStore(store),
        )
    }
}
