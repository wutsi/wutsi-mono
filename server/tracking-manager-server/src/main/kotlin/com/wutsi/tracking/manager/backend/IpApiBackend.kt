package com.wutsi.tracking.manager.backend

import com.wutsi.blog.app.backend.dto.IpApiResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class IpApiBackend(private val rest: RestTemplate) {
    @Value("\${wutsi.application.backend.ip-api.endpoint}")
    private lateinit var endpoint: String

    fun resolve(ip: String): IpApiResponse =
        rest.getForEntity("$endpoint/$ip", IpApiResponse::class.java).body!!
}
