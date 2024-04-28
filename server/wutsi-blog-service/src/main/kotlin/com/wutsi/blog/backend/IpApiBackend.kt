package com.wutsi.tracking.manager.backend

import com.wutsi.blog.app.backend.dto.IpApiResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.Cache
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class IpApiBackend(
    private val rest: RestTemplate,
    private val cache: Cache,
) {
    @Value("\${wutsi.application.backend.ip-api.endpoint}")
    private lateinit var endpoint: String

    fun resolve(ip: String): IpApiResponse {
        val key = "IP-$ip"
        var response = cache.get(key, IpApiResponse::class.java)
        if (response == null) {
            response = rest.getForEntity("$endpoint/$ip", IpApiResponse::class.java).body!!
            cache.put(key, response)
        }
        return response
    }
}
