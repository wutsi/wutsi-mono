package com.wutsi.blog.app.backend

import com.wutsi.blog.client.user.UpdateUserAttributeRequest
import com.wutsi.blog.user.dto.GetUserResponse
import com.wutsi.blog.user.dto.SearchUserRequest
import com.wutsi.blog.user.dto.SearchUserResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Deprecated("")
@Service
class UserBackendV0(private val rest: RestTemplate) {
    @Value("\${wutsi.application.backend.user.endpoint}")
    private lateinit var endpoint: String

    fun get(id: Long): GetUserResponse {
        return rest.getForEntity("$endpoint/$id", GetUserResponse::class.java).body!!
    }

    fun get(name: String): GetUserResponse {
        return rest.getForEntity("$endpoint/@/$name", GetUserResponse::class.java).body!!
    }

    fun search(request: SearchUserRequest): SearchUserResponse =
        rest.postForEntity("$endpoint/search", request, SearchUserResponse::class.java).body!!

    fun set(id: Long, request: UpdateUserAttributeRequest) {
        rest.postForEntity("$endpoint/$id/attributes", request, Any::class.java)
    }
}
