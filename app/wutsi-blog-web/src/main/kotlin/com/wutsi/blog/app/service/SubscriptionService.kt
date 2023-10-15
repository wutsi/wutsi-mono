package com.wutsi.blog.app.service

import com.wutsi.blog.app.backend.SubscriptionBackend
import com.wutsi.blog.app.mapper.SubscriptionMapper
import com.wutsi.blog.app.model.SubscriptionModel
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.subscription.dto.ImportSubscriberCommand
import com.wutsi.blog.subscription.dto.SearchSubscriptionRequest
import com.wutsi.blog.subscription.dto.SubscribeCommand
import com.wutsi.blog.subscription.dto.UnsubscribeCommand
import com.wutsi.blog.user.dto.SearchUserRequest
import org.springframework.stereotype.Service

@Service
class SubscriptionService(
    private val backend: SubscriptionBackend,
    private val userService: UserService,
    private val mapper: SubscriptionMapper,
    private val currentSessionHolder: CurrentSessionHolder,
) {
    fun search(request: SearchSubscriptionRequest, withUser: Boolean = false): List<SubscriptionModel> {
        val subscriptions = backend.search(request).subscriptions
        val users = if (subscriptions.isEmpty()) {
            emptyMap()
        } else if (!withUser) {
            subscriptions.associate { it.subscriberId to UserModel(id = it.subscriberId) }
        } else {
            userService.search(
                SearchUserRequest(
                    userIds = subscriptions.map { it.subscriberId },
                    limit = request.limit,
                ),
            ).associateBy { it.id }
        }
        return subscriptions.map { mapper.toSubscriptionModel(it, users[it.subscriberId]) }
    }

    fun import(url: String) {
        val currentUserId = currentUserId() ?: return

        backend.import(
            ImportSubscriberCommand(
                userId = currentUserId,
                url = url,
            ),
        )
    }

    fun subscribe(userId: Long, email: String, referer: String?) {
        backend.subscribe(
            SubscribeCommand(
                userId = userId,
                email = email,
                referer = referer,
            ),
        )
    }

    fun subscribeTo(userId: Long, storyId: Long?, referer: String?) {
        val currentUserId = currentUserId() ?: return

        backend.subscribe(
            SubscribeCommand(
                userId = userId,
                subscriberId = currentUserId,
                storyId = storyId,
                referer = referer,
            ),
        )
    }

    fun unsubscribeFrom(userId: Long) {
        val currentUserId = currentUserId() ?: return

        backend.unsubscribe(
            UnsubscribeCommand(
                userId = userId,
                subscriberId = currentUserId,
            ),
        )
    }

    private fun currentUserId(): Long? =
        currentSessionHolder.session()?.userId
}
