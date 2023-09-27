package com.wutsi.platform.core.stream.spring

import com.wutsi.platform.core.stream.EventStream
import com.wutsi.platform.core.stream.none.NoOpEventStream
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(
    value = ["wutsi.platform.stream.type"],
    havingValue = "none",
    matchIfMissing = true,
)
open class NoStreamConfiguration {
    @Bean(destroyMethod = "close")
    open fun eventStream(): EventStream =
        NoOpEventStream()
}
