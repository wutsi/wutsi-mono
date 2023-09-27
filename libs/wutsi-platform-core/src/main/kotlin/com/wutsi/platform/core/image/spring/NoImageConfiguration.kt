package com.wutsi.platform.core.image.spring

import com.wutsi.platform.core.image.ImageService
import com.wutsi.platform.core.image.none.ImageServiceNone
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(
    value = ["wutsi.platform.image.type"],
    havingValue = "none",
    matchIfMissing = true,
)
open class NoImageConfiguration {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(NoImageConfiguration::class.java)
    }

    @Bean
    open fun imageService(): ImageService {
        LOGGER.info("Creating ImageService")
        return ImageServiceNone()
    }
}
