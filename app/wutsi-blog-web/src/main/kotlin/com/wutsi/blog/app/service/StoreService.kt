package com.wutsi.blog.app.service

import com.wutsi.blog.app.backend.StoreBackend
import com.wutsi.blog.product.dto.CreateStoreCommand
import org.springframework.stereotype.Service

@Service
class StoreService(
    private val requestContext: RequestContext,
    private val storeBackend: StoreBackend
) {
    fun create() {
        storeBackend.create(CreateStoreCommand(requestContext.currentUser()?.id ?: -1))
    }
}
