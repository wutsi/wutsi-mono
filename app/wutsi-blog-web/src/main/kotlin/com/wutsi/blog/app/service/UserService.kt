package com.wutsi.blog.app.service

import com.wutsi.blog.app.backend.AuthenticationBackend
import com.wutsi.blog.app.backend.SubscriptionBackend
import com.wutsi.blog.app.backend.UserBackend
import com.wutsi.blog.app.backend.UserBackendV0
import com.wutsi.blog.app.form.UserAttributeForm
import com.wutsi.blog.app.mapper.UserMapper
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.subscription.dto.SubscribeCommand
import com.wutsi.blog.subscription.dto.UnsubscribeCommand
import com.wutsi.blog.user.dto.CreateBlogCommand
import com.wutsi.blog.user.dto.SearchUserRequest
import com.wutsi.blog.user.dto.UpdateUserAttributeCommand
import org.springframework.stereotype.Service

@Service
class UserService(
    private val api: UserBackendV0,
    private val backend: UserBackend,
    private val authBackend: AuthenticationBackend,
    private val subscriptionBackend: SubscriptionBackend,
    private val mapper: UserMapper,
    private val currentSessionHolder: CurrentSessionHolder,
    private val requestContext: RequestContext,
) {
    fun get(id: Long): UserModel {
        val user = api.get(id).user
        return mapper.toUserModel(user)
    }

    fun get(name: String): UserModel {
        val user = api.get(name).user
        return mapper.toUserModel(user)
    }

    fun getByAccessToken(accessToken: String): UserModel {
        val session = authBackend.session(accessToken).session
        return get(session.userId)
    }

    fun search(request: SearchUserRequest): List<UserModel> {
        val users = api.search(request).users
        return users.map { mapper.toUserModel(it) }
    }

    fun createBlog() {
        val user = requestContext.currentUser() ?: return

        backend.execute(
            CreateBlogCommand(
                userId = user.id,
            ),
        )
    }

    fun updateAttribute(request: UserAttributeForm) {
        val userId = currentUserId() ?: return

        backend.execute(
            UpdateUserAttributeCommand(
                userId = userId,
                name = request.name,
                value = request.value?.trim(),
            ),
        )
    }

    fun subscribeTo(userId: Long) {
        val currentUserId = currentUserId() ?: return

        subscriptionBackend.subscribe(
            SubscribeCommand(
                userId = userId,
                subscriberId = currentUserId,
            ),
        )
    }

    fun unsubscribeFrom(userId: Long) {
        val currentUserId = currentUserId() ?: return

        subscriptionBackend.unsubscribe(
            UnsubscribeCommand(
                userId = userId,
                subscriberId = currentUserId,
            ),
        )
    }

    private fun currentUserId(): Long? =
        currentSessionHolder.session()?.userId
}
