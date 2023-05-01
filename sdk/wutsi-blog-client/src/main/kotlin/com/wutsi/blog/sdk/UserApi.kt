package com.wutsi.blog.sdk

import com.wutsi.blog.client.user.CountUserResponse
import com.wutsi.blog.client.user.GetUserResponse
import com.wutsi.blog.client.user.SearchUserRequest
import com.wutsi.blog.client.user.SearchUserResponse
import com.wutsi.blog.client.user.UpdateUserAttributeRequest
import com.wutsi.blog.client.user.UpdateUserAttributeResponse

interface UserApi {
    fun get(userId: Long): GetUserResponse
    fun get(username: String): GetUserResponse
    fun search(request: SearchUserRequest): SearchUserResponse
    fun count(request: SearchUserRequest): CountUserResponse
    fun set(userId: Long, request: UpdateUserAttributeRequest): UpdateUserAttributeResponse
}
