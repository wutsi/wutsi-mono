package com.wutsi.blog.config

import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor

@ConfigurationProperties(prefix = "threadpool")
class ThreadPoolProperties {
    var name: String = "-"
    var minPoolSize: Int = 16
    var maxPoolSize: Int = 16
    val queueSize: Int = 100
}

@EnableAsync
@EnableConfigurationProperties(ThreadPoolProperties::class)
@Configuration
class AsyncConfiguration(val properties: ThreadPoolProperties) : AsyncConfigurer {

    @Bean
    override fun getAsyncExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = properties.minPoolSize
        executor.maxPoolSize = properties.maxPoolSize
        executor.setQueueCapacity(properties.queueSize)
//        executor.threadNamePrefix = properties.name
        executor.initialize()
        return executor
    }

    override fun getAsyncUncaughtExceptionHandler() = SimpleAsyncUncaughtExceptionHandler()
}
