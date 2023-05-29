package com.wutsi.blog.subscription.endpoint

import com.wutsi.blog.subscription.dao.SubscriptionRepository
import com.wutsi.blog.subscription.dao.SubscriptionUserRepository
import com.wutsi.blog.subscription.dto.CountSubscriptionRequest
import com.wutsi.blog.subscription.dto.CountSubscriptionResponse
import com.wutsi.blog.subscription.dto.SubscriptionCounter
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/v1/subscriptions/queries/count")
class CountSubscriptionQuery(
    private val userDao: SubscriptionUserRepository,
    private val subscriptionDao: SubscriptionRepository,
) {
    @PostMapping
    fun search(
        @Valid @RequestBody request: CountSubscriptionRequest,
    ): CountSubscriptionResponse {
        // Stories
        val users = userDao.findAllById(request.userIds.toSet()).toList()
        if (users.isEmpty()) {
            return CountSubscriptionResponse()
        }

        // Liked stories
        val subscribed: List<Long> = if (request.subscriberId != null) {
            subscriptionDao.findByUserIdInAndSubscriberId(users.map { it.userId }, request.subscriberId!!)
                .map { it.userId }
        } else {
            emptyList()
        }

        // Result
        return CountSubscriptionResponse(
            counters = users.map {
                SubscriptionCounter(
                    userId = it.userId,
                    count = it.count,
                    subscribed = subscribed.contains(it.userId),
                )
            },
        )
    }
}
