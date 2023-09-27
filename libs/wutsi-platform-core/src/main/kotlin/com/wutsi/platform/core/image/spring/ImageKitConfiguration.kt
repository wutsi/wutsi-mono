package com.wutsi.platform.core.image.spring

import com.wutsi.platform.core.image.ImageService
import com.wutsi.platform.core.image.imagekit.ImageKitService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(
    value = ["wutsi.platform.image.type"],
    havingValue = "image-kit",
)
open class ImageKitConfiguration(
    @Value("\${wutsi.platform.image.image-kit.origin-url}") private val originUrl: String,
    @Value("\${wutsi.platform.image.image-kit.endpoint-url}") private val endpointUrl: String,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ImageKitConfiguration::class.java)
    }

    @Bean
    open fun imageService(): ImageService {
        LOGGER.info("Creating ImageService")
        return ImageKitService(originUrl, endpointUrl)
    }
}
