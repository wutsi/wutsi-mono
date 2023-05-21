package com.wutsi.blog.event

import com.wutsi.event.store.PayloadDeserializer
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class RootPayloadDeserializer : PayloadDeserializer {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(RootPayloadDeserializer::class.java)
    }

    private val delegates = mutableMapOf<String, PayloadDeserializer>()

    fun register(type: String, deserializer: PayloadDeserializer) {
        LOGGER.info("Registering PayloadDeserializer $type: ${deserializer.javaClass.name}")
        if (delegates.containsKey(type)) {
            throw IllegalStateException("${delegates[type]?.javaClass?.name} already registered for $type")
        }
        delegates[type] = deserializer
    }

    override fun deserialize(type: String, payload: String): Any? =
        delegates[type]?.deserialize(type, payload)
}
