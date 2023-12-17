package com.wutsi.blog.config

import com.amazonaws.services.sqs.AmazonSQS
import com.amazonaws.services.sqs.AmazonSQSClientBuilder
import com.wutsi.blog.mail.service.sqs.SQSHealthIndicator
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(
    value = ["wutsi.application.mail.sqs-notification.enabled"],
    havingValue = "true",
    matchIfMissing = false,
)
class SQSConfiguration(
    @Value("\${wutsi.application.mail.sqs-notification.region}") private val region: String,
) {
    @Bean
    fun sqs(): AmazonSQS =
        AmazonSQSClientBuilder
            .standard()
            .withRegion(region)
            .build()

    @Bean
    fun sqsHealth(): SQSHealthIndicator =
        SQSHealthIndicator(sqs())
}
