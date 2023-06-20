package com.wutsi.platform.core.stream.rabbitmq

import com.rabbitmq.client.ShutdownListener
import com.rabbitmq.client.ShutdownSignalException
import org.slf4j.LoggerFactory

class RabbitMQShutdownListener : ShutdownListener {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(RabbitMQShutdownListener::class.java)
    }

    override fun shutdownCompleted(ex: ShutdownSignalException) {
        LOGGER.info("Channel shutdown", ex)
    }
}
