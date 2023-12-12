package com.wutsi.blog.app.service

import com.wutsi.blog.app.model.StoreModel
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Component

@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
class CurrentStoreHolder(
    private val storeService: StoreService,
    private val userHolder: CurrentUserHolder,
) {
    private var store: StoreModel? = null

    fun store(): StoreModel? {
        if (store == null) {
            val user = userHolder.user()
            if (user != null) {
                store = storeService.get(user)
            }
        }
        return store
    }
}
