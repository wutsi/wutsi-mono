package com.wutsi.blog.app.service

import com.wutsi.blog.app.backend.IpApiBackend
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class IpApiService(
    private val ipApiBackend: IpApiBackend,
    private val requestContext: RequestContext,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(IpApiService::class.java)
    }

    fun resolveCountry(): String? {
        val ip = requestContext.remoteIp()
        return try {
            val country = ipApiBackend.resolve(ip).countryCode
            country
        } catch (ex: Exception) {
            LOGGER.warn("Unable to resolve country from $ip", ex)
            null
        }
    }
}
