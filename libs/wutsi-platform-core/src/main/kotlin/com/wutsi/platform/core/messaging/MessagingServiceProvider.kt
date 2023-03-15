package com.wutsi.platform.core.messaging

import org.slf4j.LoggerFactory

open class MessagingServiceProvider {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(MessagingServiceProvider::class.java)
    }

    private val map = mutableMapOf<MessagingType, MessagingService>()

    open fun register(type: MessagingType, service: MessagingService): MessagingService {
        LOGGER.info("Registering Messaging Service. $type = ${service.javaClass}")
        map[type] = service
        return service
    }

    open fun get(type: MessagingType): MessagingService =
        map[type] ?: throw IllegalStateException("Unsupported: $type")
}
