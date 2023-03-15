package com.wutsi.platform.core.stream.spring

import com.wutsi.platform.core.stream.Event
import com.wutsi.platform.core.stream.EventHandler
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.platform.core.stream.local.LocalEventStream
import com.wutsi.platform.core.tracing.TracingContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File

@Configuration
@ConditionalOnProperty(
    value = ["wutsi.platform.stream.type"],
    havingValue = "local",
)
open class StreamConfigurationLocal(
    @Autowired private val eventPublisher: ApplicationEventPublisher,
    @Autowired private val tracingContext: TracingContext,

    @Value("\${wutsi.platform.stream.name}") private val name: String,
    @Value("\${wutsi.platform.stream.local.directory:\${user.home}/wutsi/stream}") private val directory: String,
) : AbstractStreamConfiguration() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(StreamConfigurationLocal::class.java)
    }

    @Bean(destroyMethod = "close")
    override fun eventStream(): EventStream {
        LOGGER.info("Creating EventStream: $name")
        return LocalEventStream(
            name = name,
            root = File(directory),
            tracingContext = tracingContext,
            handler = object : EventHandler {
                override fun onEvent(event: Event) {
                    eventPublisher.publishEvent(event)
                }
            },
        )
    }
}
