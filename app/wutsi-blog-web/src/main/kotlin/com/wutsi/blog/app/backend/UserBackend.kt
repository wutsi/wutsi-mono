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
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.Cache
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class UserBackend(
    private val rest: RestTemplate,
    private val cache: Cache,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(UserBackend::class.java)
    }


    @Value("\${wutsi.application.backend.user.endpoint}")
    private lateinit var endpoint: String

    fun get(id: Long): GetUserResponse {
        var response = cacheGet(id)
        if (response == null) {
            response = rest.getForEntity("$endpoint/$id", GetUserResponse::class.java).body!!
            cachePut(id, response)
        }
        return response
    }

    fun get(name: String): GetUserResponse {
        val response = rest.getForEntity("$endpoint/@/$name", GetUserResponse::class.java).body!!
        cachePut(response.user.id, response)
        return response
    }

    fun search(request: SearchUserRequest): SearchUserResponse =
        rest.postForEntity("$endpoint/queries/search", request, SearchUserResponse::class.java).body!!

    fun createBlog(command: CreateBlogCommand) {
        rest.postForEntity("$endpoint/commands/create-blog", command, Any::class.java)
        cacheEvict(command.userId)
    }

    fun updateAttribute(command: UpdateUserAttributeCommand) {
        rest.postForEntity("$endpoint/commands/update-attribute", command, Any::class.java)
        cacheEvict(command.userId)
    }

    fun recommend(request: RecommendUserRequest): RecommendUserResponse =
        rest.postForEntity("$endpoint/queries/recommend", request, RecommendUserResponse::class.java).body!!

    fun joinWpp(command: JoinWPPCommand) {
        rest.postForEntity("$endpoint/commands/join-wpp", command, Any::class.java)
        cacheEvict(command.userId)
    }

    fun createStore(command: CreateStoreCommand) {
        rest.postForEntity("$endpoint/commands/create-store", command, Any::class.java)
        cacheEvict(command.userId)
    }

    private fun cacheEvict(key: Long) {
        try {
            cache.evict(key)
        } catch (ex: Exception) {
            LOGGER.warn("Caching error", ex)
        }
    }

    private fun cacheGet(key: Long): GetUserResponse? =
        try {
            cache.get(key, GetUserResponse::class.java)
        } catch (ex: Exception) {
            LOGGER.warn("Caching error", ex)
            null
        }

    private fun cachePut(key: Long, value: GetUserResponse) {
        try {
            cache.put(key, value)
        } catch (ex: Exception) {
            LOGGER.warn("Caching error", ex)
        }
    }
}
