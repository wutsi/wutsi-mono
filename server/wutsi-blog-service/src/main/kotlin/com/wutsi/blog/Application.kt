package com.wutsi.blog

import com.wutsi.platform.core.WutsiApplication
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
        "com.wutsi.blog.follower.domain",
        "com.wutsi.blog.feed.domain",
        "com.wutsi.blog.like.domain",
        "com.wutsi.blog.pin.domain",
        "com.wutsi.blog.story.domain",
        "com.wutsi.event.store.jpa",
    ],
)
@EnableJpaRepositories(
    value = [
        "com.wutsi.blog.account.dao",
        "com.wutsi.blog.channel.dao",
        "com.wutsi.blog.comment.dao",
        "com.wutsi.blog.follower.dao",
        "com.wutsi.blog.like.dao",
        "com.wutsi.blog.pin.dao",
        "com.wutsi.blog.story.dao",
    ],
)
@WutsiApplication
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
