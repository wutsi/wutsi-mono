package com.wutsi.blog.app.page.settings.service

import com.wutsi.blog.app.backend.AuthenticationBackend
import com.wutsi.blog.app.backend.UserBackend
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.settings.model.UserAttributeForm
import com.wutsi.blog.app.page.settings.model.UserModel
import com.wutsi.blog.client.user.SearchUserRequest
import com.wutsi.blog.client.user.UpdateUserAttributeRequest
import org.springframework.stereotype.Service

@Service
class UserService(
    private val api: UserBackend,
    private val authBackend: AuthenticationBackend,
    private val mapper: UserMapper,
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

    fun set(request: UserAttributeForm) {
        requestContext.currentUser() ?: return

        api.set(
            requestContext.currentUser()?.id!!,
            UpdateUserAttributeRequest(
                name = request.name,
                value = request.value.trim(),
            ),
        )
    }
}
