package com.wutsi.platform.core.storage.spring

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.wutsi.platform.core.storage.StorageService
import com.wutsi.platform.core.storage.aws.S3StorageService
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(
    value = ["wutsi.platform.storage.type"],
    havingValue = "aws",
    matchIfMissing = false,
)
open class S3StorageConfiguration(
    @Value("\${wutsi.platform.storage.aws.region:us-east-1}") private val region: String,
    @Value("\${wutsi.platform.storage.aws.bucket}") private val bucket: String,
) {
    @Bean
    open fun storageService(): StorageService =
        S3StorageService(amazonS3(), bucket)

    @Bean
    open fun amazonS3(): AmazonS3 {
        return AmazonS3ClientBuilder
            .standard()
            .withRegion(region)
            .build()
    }

    @Bean
    open fun storageHealthCheck(): HealthIndicator {
        return S3HealthIndicator(amazonS3(), bucket)
    }
}
