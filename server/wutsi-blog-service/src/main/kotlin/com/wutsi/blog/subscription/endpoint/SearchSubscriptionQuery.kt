package com.wutsi.blog.subscription.endpoint

import com.wutsi.blog.subscription.dto.SearchSubscriptionRequest
import com.wutsi.blog.subscription.dto.SearchSubscriptionResponse
import com.wutsi.blog.subscription.dto.Subscription
import com.wutsi.blog.subscription.service.SubscriptionService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/v1/subscriptions/queries/search")
class SearchSubscriptionQuery(
    private val service: SubscriptionService,
) {
    @PostMapping
    fun search(@Valid @RequestBody request: SearchSubscriptionRequest): SearchSubscriptionResponse =
        SearchSubscriptionResponse(
            subscriptions = service.search(request).map {
                Subscription(it.userId, it.subscriberId, it.timestamp)
            },
        )
}
