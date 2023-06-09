package com.wutsi.blog.config

import com.amazonaws.services.s3.AmazonS3
import com.wutsi.blog.kpi.service.TrackingStorageService
import com.wutsi.platform.core.storage.aws.S3StorageService
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(
    value = ["wutsi.application.tracking.type"],
    havingValue = "aws",
    matchIfMissing = false,
)
open class TrackingConfigurationAws(
    private val s3: AmazonS3,
    @Value("\${wutsi.application.tracking.aws.bucket}") private val bucket: String,
) {
    @Bean
    fun trackingStorage(): TrackingStorageService =
        TrackingStorageService(
            S3StorageService(s3, bucket),
        )
}
