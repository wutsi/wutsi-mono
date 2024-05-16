package com.wutsi.platform.core.tracking.spring

import com.wutsi.platform.core.tracking.ChannelDetector
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class TrackingConfiguration {
    @Bean
    open fun channelDetector(): ChannelDetector =
        ChannelDetector()
}
