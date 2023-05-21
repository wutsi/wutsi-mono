package com.wutsi.blog.event

import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.Event
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class RootEventHandler(
    private val logger: KVLogger,
) : EventHandler {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(RootEventHandler::class.java)
    }

    private val delegates = mutableMapOf<String, EventHandler>()

    fun register(type: String, handler: EventHandler) {
        LOGGER.info("Registering EventHandler $type: ${handler.javaClass.name}")
        if (delegates.containsKey(type)) {
            throw IllegalStateException("${delegates[type]?.javaClass?.name} already registered for $type")
        }
        delegates[type] = handler
    }

    @EventListener
    override fun handle(event: Event) {
        val delegate = delegates[event.type]
        if (delegate != null) {
            logger.add("delegate", delegate::class.java)
            delegate.handle(event)
        }
    }
}
