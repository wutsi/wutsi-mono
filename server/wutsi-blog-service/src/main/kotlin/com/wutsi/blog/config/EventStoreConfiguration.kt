package com.wutsi.blog.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.event.RootPayloadDeserializer
import com.wutsi.event.store.EventStore
import com.wutsi.event.store.jpa.JPAEventStore
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.persistence.EntityManager

@Configuration
class EventStoreConfiguration(
    private val em: EntityManager,
    private val objectMapper: ObjectMapper,
    private val payloadDeserializer: RootPayloadDeserializer,
) {
    @Bean
    fun eventStore(): EventStore =
        JPAEventStore(em, objectMapper, payloadDeserializer)
}
