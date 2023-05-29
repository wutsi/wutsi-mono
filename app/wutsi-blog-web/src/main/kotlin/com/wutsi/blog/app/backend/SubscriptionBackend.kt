package com.wutsi.blog.app.backend

import com.wutsi.blog.client.follower.CountFollowerResponse
import com.wutsi.blog.client.follower.CreateFollowerRequest
import com.wutsi.blog.client.follower.CreateFollowerResponse
import com.wutsi.blog.client.follower.SearchFollowerRequest
import com.wutsi.blog.client.follower.SearchFollowerResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class FollowerBackend(private val rest: RestTemplate) {
    @Value("\${wutsi.application.backend.follower.endpoint}")
    private lateinit var endpoint: String

    fun count(request: SearchFollowerRequest): CountFollowerResponse {
        return rest.postForEntity("$endpoint/count", request, CountFollowerResponse::class.java).body!!
    }

    fun create(request: CreateFollowerRequest): CreateFollowerResponse {
        return rest.postForEntity(endpoint, request, CreateFollowerResponse::class.java).body!!
    }

    fun search(request: SearchFollowerRequest): SearchFollowerResponse =
        rest.postForEntity("$endpoint/search", request, SearchFollowerResponse::class.java).body!!

    fun delete(id: Long) {
        rest.delete("$endpoint/$id")
    }
}
