package com.wutsi.blog.app.backend

import com.wutsi.blog.transaction.dto.SearchSuperFanRequest
import com.wutsi.blog.transaction.dto.SearchSuperFanResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class SuperFanBackend(private val rest: RestTemplate) {
    @Value("\${wutsi.application.backend.super-fan.endpoint}")
    private lateinit var endpoint: String

    fun search(request: SearchSuperFanRequest): SearchSuperFanResponse =
        rest.postForEntity("$endpoint/queries/search", request, SearchSuperFanResponse::class.java).body!!
}
