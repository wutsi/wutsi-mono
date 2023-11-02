package com.wutsi.blog.aws

import com.amazonaws.services.sqs.AmazonSQS
import org.slf4j.LoggerFactory
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator

class SQSHealthIndicator(private val sqs: AmazonSQS) : HealthIndicator {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(SQSHealthIndicator::class.java)
    }

    override fun health(): Health {
        val now = System.currentTimeMillis()
        try {
            sqs.listQueues()
            return Health.up()
                .withDetail("durationMillis", System.currentTimeMillis() - now)
                .build()
        } catch (ex: Exception) {
            LOGGER.error("Health failure", ex)
            return Health.down()
                .withDetail("durationMillis", System.currentTimeMillis() - now)
                .withException(ex)
                .build()
        }
    }
}
