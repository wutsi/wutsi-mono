package com.wutsi.security.manager

import com.wutsi.platform.core.WutsiApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling

@WutsiApplication
@SpringBootApplication
@EnableAsync
@EnableScheduling
open class Application

public fun main(vararg args: String) {
    org.springframework.boot.runApplication<Application>(*args)
}
