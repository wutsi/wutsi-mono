package com.wutsi.platform.core.stream.spring

import com.rabbitmq.client.Channel
import com.rabbitmq.client.ConnectionFactory
import com.wutsi.platform.core.stream.Event
import com.wutsi.platform.core.stream.EventHandler
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.platform.core.stream.rabbitmq.RabbitMQEventStream
import com.wutsi.platform.core.stream.rabbitmq.RabbitMQHealthIndicator
import com.wutsi.platform.core.stream.rabbitmq.RabbitMQShutdownListener
import com.wutsi.platform.core.tracing.TracingContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.Scheduled
import java.util.concurrent.ExecutorService

@Configuration
@ConditionalOnProperty(
    value = ["wutsi.platform.stream.type"],
    havingValue = "rabbitmq",
)
open class StreamConfigurationRabbitMQ(
    @Autowired private val eventPublisher: ApplicationEventPublisher,
    @Autowired private val tracingContext: TracingContext,

    @Value("\${wutsi.platform.stream.name}") private val name: String,
    @Value("\${wutsi.platform.stream.consume:true}") private val consume: Boolean,
    @Value("\${wutsi.platform.stream.rabbitmq.url}") private val url: String,
    @Value("\${wutsi.platform.stream.rabbitmq.thread-pool-size:8}") private val threadPoolSize: Int,
    @Value("\${wutsi.platform.stream.rabbitmq.dlq.max-retries:10}") private val dlqMaxRetries: Int,
    @Value("\${wutsi.platform.stream.rabbitmq.queue-ttl-seconds:86400}") private val queueTtlSeconds: Long,
) : AbstractStreamConfiguration() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(StreamConfigurationRabbitMQ::class.java)
    }

    @Scheduled(cron = "\${wutsi.platform.stream.rabbitmq.dlq.replay-cron:0 */15 * * * *}")
    fun replayDlq() {
        (eventStream() as RabbitMQEventStream).replayDlq()
    }

    @Bean(destroyMethod = "close")
    override fun eventStream(): EventStream {
        LOGGER.info("Creating EventStream: $name")
        return RabbitMQEventStream(
            name = name,
            consume = consume,
            channel = channel(),
            queueTtlSeconds = queueTtlSeconds,
            dlqMaxRetries = dlqMaxRetries,
            tracingContext = tracingContext,
            handler = object : EventHandler {
                override fun onEvent(event: Event) {
                    eventPublisher.publishEvent(event)
                }
            },
        )
    }

    @Bean
    open fun connectionFactory(): ConnectionFactory {
        val factory = ConnectionFactory()
        factory.setUri(url)
        return factory
    }

    @Bean(destroyMethod = "shutdown")
    open fun executorService(): ExecutorService =
        java.util.concurrent.Executors.newFixedThreadPool(threadPoolSize)

    @Bean(destroyMethod = "close")
    open fun channel(): Channel {
        val result = connectionFactory()
            .newConnection(executorService())
            .createChannel()

        result.addShutdownListener(RabbitMQShutdownListener())
        return result
    }

    @Bean
    open fun rabbitMQHealthIndicator(): HealthIndicator =
        RabbitMQHealthIndicator(channel())
}
