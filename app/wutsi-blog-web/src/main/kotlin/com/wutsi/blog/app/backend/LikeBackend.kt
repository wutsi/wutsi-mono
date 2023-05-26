package com.wutsi.blog.app.backend

import com.wutsi.blog.client.like.CountLikeResponse
import com.wutsi.blog.client.like.CreateLikeRequest
import com.wutsi.blog.client.like.CreateLikeResponse
import com.wutsi.blog.client.like.SearchLikeRequest
import com.wutsi.blog.client.like.SearchLikeResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Deprecated("")
@Service
class LikeBackend(private val rest: RestTemplate) {
    @Value("\${wutsi.application.backend.like.endpoint}")
    private lateinit var endpoint: String

    fun create(request: CreateLikeRequest): CreateLikeResponse =
        rest.postForEntity(endpoint, request, CreateLikeResponse::class.java).body!!

    fun count(request: SearchLikeRequest): CountLikeResponse =
        rest.postForEntity("$endpoint/count", request, CountLikeResponse::class.java).body!!

    fun delete(id: Long) {
        rest.delete("$endpoint/$id")
    }

    fun search(request: SearchLikeRequest): SearchLikeResponse =
        rest.postForEntity("$endpoint/search", request, SearchLikeResponse::class.java).body!!
}
