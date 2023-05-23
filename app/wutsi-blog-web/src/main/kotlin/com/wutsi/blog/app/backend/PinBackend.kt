package com.wutsi.blog.app.backend

import com.wutsi.blog.client.pin.CreatePinRequest
import com.wutsi.blog.client.pin.CreatePinResponse
import com.wutsi.blog.client.pin.GetPinResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class PinBackend(private val rest: RestTemplate) {
    @Value("\${wutsi.application.backend.user.endpoint}")
    private lateinit var endpoint: String

    fun get(userId: Long): GetPinResponse =
        rest.getForEntity("$endpoint/$userId/pin", GetPinResponse::class.java).body!!

    fun create(userId: Long, request: CreatePinRequest): CreatePinResponse =
        rest.postForEntity("$endpoint/$userId/pin", request, CreatePinResponse::class.java).body!!

    fun delete(userId: Long) {
        rest.delete("$endpoint/$userId/pin")
    }
}
