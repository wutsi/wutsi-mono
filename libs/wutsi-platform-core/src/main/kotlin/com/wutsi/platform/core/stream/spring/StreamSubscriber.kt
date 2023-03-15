package com.wutsi.platform.core.stream.spring

import com.wutsi.platform.core.stream.EventStream
import org.slf4j.LoggerFactory
import javax.annotation.PostConstruct

class StreamSubscriber(
    private val eventStream: EventStream,
    private val properties: StreamConfigurationProperties,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(StreamSubscriber::class.java)
    }

    @PostConstruct
    fun init() {
        val subscriptions = properties.subscriptions
        LOGGER.info("Subscriptions: $subscriptions")

        subscriptions.forEach { eventStream.subscribeTo(it) }
    }
}
