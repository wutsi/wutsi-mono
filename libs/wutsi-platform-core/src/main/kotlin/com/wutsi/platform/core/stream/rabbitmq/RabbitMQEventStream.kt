package com.wutsi.platform.core.stream.rabbitmq

import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.BuiltinExchangeType
import com.rabbitmq.client.Channel
import com.wutsi.platform.core.stream.Event
import com.wutsi.platform.core.stream.EventHandler
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.platform.core.stream.EventTracingData
import com.wutsi.platform.core.tracing.TracingContext
import com.wutsi.platform.core.util.ObjectMapperBuilder
import org.slf4j.LoggerFactory
import java.nio.charset.Charset
import java.time.OffsetDateTime
import java.util.Timer
import java.util.TimerTask
import java.util.UUID

/**
 * Implementation of {@link com.wutsi.platform.core.stream.Stream} based on RabbitMQ
 */
class RabbitMQEventStream(
    private val name: String,
    private val channel: Channel,
    private val handler: EventHandler,
    private val consumerSetupDelaySeconds: Long = 30,
    private val queueTtlSeconds: Long = 6 * 60 * 60, // Queue TTL: 6 hours
    private val dlqMaxRetries: Int = 10,
    private val tracingContext: TracingContext,
    private val consume: Boolean = true,
) : EventStream {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(RabbitMQEventStream::class.java)
    }

    private val queue: String = "${name}_queue_in"
    private val topic: String = toTopicName(name)
    private val queueDLQ: String = "${name}_queue_dlq"
    private val mapper: ObjectMapper = ObjectMapperBuilder.build()

    init {
        if (consume) {
            // DLQ
            LOGGER.info("Setup DLQ")
            channel.queueDeclare(
                queueDLQ,
                true, // durable
                false, // exclusive
                false, // autoDelete
                mapOf(),
            )

            // Queue
            LOGGER.info("Setup queue: $queue")
            channel.queueDeclare(
                queue,
                true, // durable
                false, // exclusive
                false, // autoDelete
                mapOf(
                    "x-dead-letter-exchange" to "",
                    "x-dead-letter-routing-key" to queueDLQ,
                ),
            )
            setupConsumer()
        } else {
            LOGGER.info("consume=false. Ignoring all incoming events")
        }

        // Topic
        LOGGER.info("Setup topic: $topic")
        channel.exchangeDeclare(
            topic,
            BuiltinExchangeType.FANOUT,
            true, // durable
        )
    }

    private fun setupConsumer() {
        /*
        Wait before registering the consumer, so that the server is completely UP.
        With Spring, this caused by bug because the consumer was registered during the startup,
        then it dispatched the event using EventListener, but the event was lost because all the
        event listener was not yet setup by spring!
         */
        LOGGER.info("Will setup queue consumer in $consumerSetupDelaySeconds seconds(s)")
        val task = object : TimerTask() {
            override fun run() {
                LOGGER.info("Registering queue consumer")
                channel.basicConsume(
                    queue,
                    false, // auto-ack
                    RabbitMQConsumer(handler, mapper, channel),
                )
            }
        }
        Timer(queue, false).schedule(task, consumerSetupDelaySeconds * 1000)
    }

    override fun close() {
    }

    override fun enqueue(type: String, payload: Any) {
        LOGGER.info("enqueue($type, ${logPayload(payload)})")

        val event = createEvent(type, payload)
        val json: String = mapper.writeValueAsString(event)
        channel.basicPublish(
            "",
            this.queue, // routing-key
            properties(), // basic-properties
            json.toByteArray(Charset.forName("utf-8")),
        )
    }

    override fun publish(type: String, payload: Any) {
        LOGGER.info("publish($type, ${logPayload(payload)})")

        val event = createEvent(type, payload)
        val json: String = mapper.writeValueAsString(event)
        channel.basicPublish(
            this.topic,
            "", // routing-key
            properties(), // basic-properties
            json.toByteArray(Charset.forName("utf-8")),
        )
    }

    private fun logPayload(payload: Any): String {
        val str = payload.toString()
        val maxlen = 512
        return if (str.length > maxlen) {
            str.take(maxlen) + "..."
        } else {
            str
        }
    }

    override fun subscribeTo(source: String) {
        LOGGER.info("Subscribing: $source --> $name")
        channel.queueBind(
            queue,
            toTopicName(source),
            "",
        )
    }

    fun replayDlq() {
        if (!consume) {
            return
        }

        LOGGER.info("Replaying DLQ")
        while (true) {
            // Get the response
            val response = channel.basicGet(queueDLQ, false) ?: break

            // Too many retries?
            val retries = response.props.headers["x-retries"] as Int
            if (retries >= dlqMaxRetries) {
                LOGGER.info("Too many retries - ${response.envelope.deliveryTag}")
                channel.basicReject(response.envelope.deliveryTag, false) // Reject + Drop
            } else {
                // Replay
                channel.basicPublish(
                    "",
                    this.queue, // routing-key
                    properties(retries + 1), // basic-properties
                    response.body,
                )
                channel.basicAck(response.envelope.deliveryTag, false) // ACK
            }
        }
    }

    private fun properties(retries: Int = 0) = AMQP.BasicProperties().builder()
        .headers(
            mapOf(
                "x-max-retries" to dlqMaxRetries,
                "x-retries" to retries,
            ),
        )
        .expiration((queueTtlSeconds * 1000).toString())
        .build()

    private fun createEvent(type: String, payload: Any) = Event(
        id = UUID.randomUUID().toString(),
        type = type,
        timestamp = OffsetDateTime.now(),
        payload = mapper.writeValueAsString(payload),
        tracingData = EventTracingData(
            clientId = tracingContext.clientId(),
            traceId = tracingContext.traceId(),
            deviceId = tracingContext.deviceId(),
            tenantId = tracingContext.tenantId(),
            clientInfo = tracingContext.clientInfo(),
        ),
    )

    private fun toTopicName(name: String) = "${name}_topic_out"
}
