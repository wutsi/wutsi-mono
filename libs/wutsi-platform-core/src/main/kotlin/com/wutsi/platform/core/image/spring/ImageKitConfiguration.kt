package com.wutsi.platform.core.image.spring

import com.wutsi.platform.core.image.ImageService
import com.wutsi.platform.core.image.imagekit.ImageKitService
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(
    value = ["wutsi.platform.image.type"],
    havingValue = "image-kit",
)
@ConfigurationProperties(prefix = "wutsi.platform.image.image-kit")
open class ImageKitConfiguration {
    private var originUrl: String = ""
    private var endpointUrls: List<String> = emptyList()

    companion object {
        private val LOGGER = LoggerFactory.getLogger(ImageKitConfiguration::class.java)
    }

    @Bean
    open fun imageService(): ImageService {
        LOGGER.info("Creating ImageService")
        return ImageKitService(originUrl, endpointUrls)
    }
}
