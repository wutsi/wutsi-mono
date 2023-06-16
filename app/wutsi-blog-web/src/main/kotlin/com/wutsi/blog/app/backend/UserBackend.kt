package com.wutsi.blog.app.backend

import com.wutsi.blog.user.dto.CreateBlogCommand
import com.wutsi.blog.user.dto.GetUserResponse
import com.wutsi.blog.user.dto.SearchUserRequest
import com.wutsi.blog.user.dto.SearchUserResponse
import com.wutsi.blog.user.dto.UpdateUserAttributeCommand
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class UserBackend(private val rest: RestTemplate) {
    @Value("\${wutsi.application.backend.user.endpoint}")
    private lateinit var endpoint: String

    fun get(id: Long): GetUserResponse {
        return rest.getForEntity("$endpoint/$id", GetUserResponse::class.java).body!!
    }

    fun get(name: String): GetUserResponse {
        return rest.getForEntity("$endpoint/@/$name", GetUserResponse::class.java).body!!
    }

    fun search(request: SearchUserRequest): SearchUserResponse =
        rest.postForEntity("$endpoint/queries/search", request, SearchUserResponse::class.java).body!!

    fun createBlog(command: CreateBlogCommand) {
        rest.postForEntity("$endpoint/commands/create-blog", command, Any::class.java)
    }

    fun updateAttribute(command: UpdateUserAttributeCommand) {
        rest.postForEntity("$endpoint/commands/update-attribute", command, Any::class.java)
    }
}
