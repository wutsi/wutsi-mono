package com.wutsi.platform.core.stream.spring

import com.wutsi.platform.core.stream.EventStream
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean

abstract class AbstractStreamConfiguration {
    abstract fun eventStream(): EventStream

    @Bean
    @ConfigurationProperties(prefix = "wutsi.platform.stream")
    open fun streamProperties(): StreamConfigurationProperties =
        StreamConfigurationProperties()

    @Bean
    open fun streamSubscriber(): StreamSubscriber =
        StreamSubscriber(eventStream(), streamProperties())
}
