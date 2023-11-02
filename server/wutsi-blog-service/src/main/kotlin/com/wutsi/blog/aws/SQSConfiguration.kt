package com.wutsi.blog.aws

import com.amazonaws.regions.Regions
import com.amazonaws.services.sqs.AmazonSQS
import com.amazonaws.services.sqs.AmazonSQSClientBuilder
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(
    value = ["wutsi.platform.aws.sqs.enabled"],
    havingValue = "true",
    matchIfMissing = false
)
class SQSConfiguration {
    @Bean
    fun sqs(): AmazonSQS =
        AmazonSQSClientBuilder.standard().withRegion(Regions.US_EAST_1).build()

    @Bean
    fun sqsHealth(): SQSHealthIndicator =
        SQSHealthIndicator(sqs())
}
