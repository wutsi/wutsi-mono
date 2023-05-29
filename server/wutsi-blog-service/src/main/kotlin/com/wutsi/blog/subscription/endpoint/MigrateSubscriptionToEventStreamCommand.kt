package com.wutsi.blog.subscription.endpoint

import com.wutsi.blog.event.EventType.SUBSCRIBE_COMMAND
import com.wutsi.blog.subscription.dao.FollowerRepository
import com.wutsi.blog.subscription.dto.SubscribeCommand
import com.wutsi.platform.core.stream.EventStream
import org.springframework.scheduling.annotation.Async
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/subscriptions/commands/migrate-to-event-stream")
class MigrateSubscriptionToEventStreamCommand(
    private val dao: FollowerRepository,
    private val eventStream: EventStream,
) {
    @Async
    @GetMapping
    fun migrate() {
        val followers = dao.findAll()
        followers.forEach {
            eventStream.enqueue(
                type = SUBSCRIBE_COMMAND,
                payload = SubscribeCommand(
                    userId = it.userId,
                    subscriberId = it.followerUserId,
                    timestamp = it.followDateTime.time,
                ),
            )
        }
    }
}
