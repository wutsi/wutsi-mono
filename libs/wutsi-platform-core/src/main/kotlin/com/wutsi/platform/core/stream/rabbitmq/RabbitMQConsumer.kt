package com.wutsi.platform.core.stream.rabbitmq

import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client.AMQP.BasicProperties
import com.rabbitmq.client.Channel
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope
import com.wutsi.platform.core.logging.DefaultKVLogger
import com.wutsi.platform.core.logging.ThreadLocalKVLoggerHolder
import com.wutsi.platform.core.stream.Event
import com.wutsi.platform.core.stream.EventHandler
import com.wutsi.platform.core.stream.StreamLoggerHelper
import com.wutsi.platform.core.tracing.DefaultTracingContext
import com.wutsi.platform.core.tracing.ThreadLocalTracingContextHolder
import org.slf4j.LoggerFactory

internal class RabbitMQConsumer(
    private val handler: EventHandler,
    private val mapper: ObjectMapper,
    channel: Channel,
) : DefaultConsumer(channel) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(RabbitMQEventStream::class.java)
    }

    override fun handleDelivery(
        consumerTag: String,
        envelope: Envelope,
        properties: BasicProperties,
        body: ByteArray,
    ) {
        // Add Logger into the ThreadLocal
        val logger = DefaultKVLogger()
        ThreadLocalKVLoggerHolder.set(logger)

        logger.add("rabbitmq_consumer_tag", consumerTag)
        try {
            // Read the event...
            val event = mapper.readValue(body, Event::class.java)
            StreamLoggerHelper.log(event, logger)

            // Add TracingContext into the ThreadLocal
            val tc = DefaultTracingContext(
                clientId = "_rabbitmq_",
                traceId = event.tracingData.traceId,
                deviceId = event.tracingData.deviceId,
                tenantId = event.tracingData.tenantId,
                clientInfo = null,
            )
            ThreadLocalTracingContextHolder.set(tc)
            StreamLoggerHelper.log(tc, logger)

            // Process the event
            handler.onEvent(event)
            channel.basicAck(envelope.deliveryTag, false)

            logger.add("success", true)
        } catch (ex: Exception) {
            logger.setException(ex)
            logger.add("success", false)

            try {
                channel.basicReject(
                    envelope.deliveryTag,
                    false, // do not requeue - message will go to DLQ
                )
            } catch (ex2: Exception) { // Just in case
                LOGGER.warn("Unable to reject message rabbitmq_consumer_tag=$consumerTag", ex)
            }
        } finally {
            logger.log()

            // Clear the tracing context
            ThreadLocalKVLoggerHolder.remove()
            ThreadLocalTracingContextHolder.remove()
        }
    }
}
