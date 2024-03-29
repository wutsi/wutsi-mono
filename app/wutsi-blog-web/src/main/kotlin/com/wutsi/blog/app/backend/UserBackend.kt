package com.wutsi.blog.app.backend

import com.wutsi.blog.product.dto.CreateStoreCommand
import com.wutsi.blog.user.dto.CreateBlogCommand
import com.wutsi.blog.user.dto.GetUserResponse
import com.wutsi.blog.user.dto.JoinWPPCommand
import com.wutsi.blog.user.dto.RecommendUserRequest
import com.wutsi.blog.user.dto.RecommendUserResponse
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

    fun recommend(request: RecommendUserRequest): RecommendUserResponse =
        rest.postForEntity("$endpoint/queries/recommend", request, RecommendUserResponse::class.java).body!!

    fun joinWpp(command: JoinWPPCommand) {
        rest.postForEntity("$endpoint/commands/join-wpp", command, Any::class.java)
    }

    fun createStore(command: CreateStoreCommand) {
        rest.postForEntity("$endpoint/commands/create-store", command, Any::class.java)
    }
}
