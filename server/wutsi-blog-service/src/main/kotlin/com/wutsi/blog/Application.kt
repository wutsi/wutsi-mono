package com.wutsi.blog

import com.wutsi.platform.core.WutsiApplication
import com.wutsi.platform.payment.EnableWutsiPayment
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableTransactionManagement
@EnableCaching
@ComponentScan(
    value = [
        "com.wutsi.blog",
    ],
)
@EntityScan(
    value = [
        "com.wutsi.blog.account.domain",
        "com.wutsi.blog.channel.domain",
        "com.wutsi.blog.comment.domain",
        "com.wutsi.blog.kpi.domain",
        "com.wutsi.blog.like.domain",
        "com.wutsi.blog.pin.domain",
        "com.wutsi.blog.share.domain",
        "com.wutsi.blog.story.domain",
        "com.wutsi.blog.subscription.domain",
        "com.wutsi.blog.transaction.domain",
        "com.wutsi.blog.user.domain",
        "com.wutsi.event.store.jpa",
    ],
)
@EnableJpaRepositories(
    value = [
        "com.wutsi.blog.account.dao",
        "com.wutsi.blog.channel.dao",
        "com.wutsi.blog.comment.dao",
        "com.wutsi.blog.kpi.dao",
        "com.wutsi.blog.like.dao",
        "com.wutsi.blog.pin.dao",
        "com.wutsi.blog.share.dao",
        "com.wutsi.blog.story.dao",
        "com.wutsi.blog.subscription.dao",
        "com.wutsi.blog.transaction.dao",
        "com.wutsi.blog.user.dao",
    ],
)
@WutsiApplication
@EnableWutsiPayment
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
