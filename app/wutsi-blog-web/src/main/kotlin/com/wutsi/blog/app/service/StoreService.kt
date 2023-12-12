package com.wutsi.blog.app.service

import com.wutsi.blog.app.backend.StoreBackend
import com.wutsi.blog.app.mapper.StoreMapper
import com.wutsi.blog.app.model.StoreModel
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.product.dto.CreateStoreCommand
import org.springframework.stereotype.Service

@Service
class StoreService(
    private val requestContext: RequestContext,
    private val storeBackend: StoreBackend,
    private val mapper: StoreMapper,
) {
    fun get(id: String): StoreModel =
        mapper.toStoreMapper(storeBackend.get(id).store)

    fun get(user: UserModel): StoreModel? =
        user.storeId?.let { get(it) }

    fun create() {
        storeBackend.create(CreateStoreCommand(requestContext.currentUser()?.id ?: -1))
    }
}
