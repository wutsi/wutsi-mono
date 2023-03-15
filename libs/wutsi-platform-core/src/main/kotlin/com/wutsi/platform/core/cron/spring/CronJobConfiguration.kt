package com.wutsi.platform.core.cron.spring

import com.wutsi.platform.core.cron.CronLockManager
import org.springframework.cache.Cache
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class CronJobConfiguration(private val cache: Cache) {
    @Bean
    open fun cronLockManager(): CronLockManager =
        CronLockManager(cache)
}
