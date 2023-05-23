package com.wutsi.blog.app

import com.wutsi.platform.core.WutsiApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.ServletComponentScan

@WutsiApplication
@SpringBootApplication
@ServletComponentScan
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
