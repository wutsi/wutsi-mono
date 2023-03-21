package com.wutsi.marketplace.access.delegate

import com.wutsi.marketplace.access.dto.SearchStoreRequest
import com.wutsi.marketplace.access.dto.SearchStoreResponse
import com.wutsi.marketplace.access.service.StoreService
import org.springframework.stereotype.Service

@Service
class SearchStoreDelegate(private val service: StoreService) {
    fun invoke(request: SearchStoreRequest): SearchStoreResponse {
        val stores = service.search(request)
        return SearchStoreResponse(
            stores = stores.map { service.toStoreSummary(it) },
        )
    }
}
