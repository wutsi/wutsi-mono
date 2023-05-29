package com.wutsi.blog.app.service

import com.wutsi.blog.app.backend.AuthenticationBackend
import com.wutsi.blog.app.backend.SubscriptionBackend
import com.wutsi.blog.app.backend.UserBackend
import com.wutsi.blog.app.form.UserAttributeForm
import com.wutsi.blog.app.mapper.UserMapper
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.client.user.SearchUserRequest
import com.wutsi.blog.client.user.UpdateUserAttributeRequest
import com.wutsi.blog.subscription.dto.CountSubscriptionRequest
import com.wutsi.blog.subscription.dto.SubscribeCommand
import com.wutsi.blog.subscription.dto.SubscriptionCounter
import com.wutsi.blog.subscription.dto.UnsubscribeCommand
import org.springframework.stereotype.Service

@Service
class UserService(
    private val api: UserBackend,
    private val authBackend: AuthenticationBackend,
    private val subscriptionBackend: SubscriptionBackend,
    private val mapper: UserMapper,
    private val currentSessionHolder: CurrentSessionHolder,
) {
    fun get(id: Long): UserModel {
        val user = api.get(id).user
        val subscriptions = getSubscriptions(listOf(id))
        return mapper.toUserModel(user, subscriptions)
    }

    fun get(name: String): UserModel {
        val user = api.get(name).user
        val subscriptions = getSubscriptions(listOf(user.id))
        return mapper.toUserModel(user, subscriptions)
    }

    fun getByAccessToken(accessToken: String): UserModel {
        val session = authBackend.session(accessToken).session
        return get(session.userId)
    }

    fun search(request: SearchUserRequest): List<UserModel> {
        val users = api.search(request).users
        val subscriptions = getSubscriptions(users.map { it.id })
        return users.map { mapper.toUserModel(it, subscriptions) }
    }

    fun set(request: UserAttributeForm) {
        val userId = currentUserId() ?: return

        api.set(
            userId,
            UpdateUserAttributeRequest(
                name = request.name,
                value = request.value.trim(),
            ),
        )
    }

    fun subscribeTo(userId: Long) {
        val currentUserId = currentUserId() ?: return

        subscriptionBackend.execute(
            SubscribeCommand(
                userId = userId,
                subscriberId = currentUserId,
            ),
        )
    }

    fun unsubscribeFrom(userId: Long) {
        val currentUserId = currentUserId() ?: return

        subscriptionBackend.execute(
            UnsubscribeCommand(
                userId = userId,
                subscriberId = currentUserId,
            ),
        )
    }

    private fun getSubscriptions(userIds: List<Long>): List<SubscriptionCounter> =
        subscriptionBackend.count(
            CountSubscriptionRequest(
                userIds = userIds,
                subscriberId = currentUserId(),
            ),
        ).counters

    private fun currentUserId(): Long? =
        currentSessionHolder.session()?.userId
}
