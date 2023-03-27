package com.wutsi.application.feed.config

import com.wutsi.regulation.RegulationEngine
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RegulationEngineConfiguration {
    @Bean
    fun regulationEngine() = RegulationEngine()
}
