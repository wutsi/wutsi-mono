package com.wutsi.blog.app.service

import com.wutsi.blog.app.backend.SubscriptionBackend
import com.wutsi.blog.app.mapper.SubscriptionMapper
import com.wutsi.blog.app.model.SubscriptionModel
import com.wutsi.blog.subscription.dto.SearchSubscriptionRequest
import org.springframework.stereotype.Service

@Service
class SubscriptionService(
    private val backend: SubscriptionBackend,
    private val mapper: SubscriptionMapper,
) {
    fun search(request: SearchSubscriptionRequest): List<SubscriptionModel> =
        backend.search(request).subscriptions.map { mapper.toSubscriptionModel(it) }
}
