package com.wutsi.blog.subscription.endpoint

import com.wutsi.blog.AbstractMigrateToEventStreamCommandExecutor
import com.wutsi.blog.subscription.dao.FollowerRepository
import com.wutsi.blog.subscription.domain.Follower
import com.wutsi.blog.subscription.dto.SubscribeCommand
import com.wutsi.blog.subscription.service.SubscriptionService
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/subscriptions/commands/migrate-to-event-stream")
class MigrateSubscriptionToEventStreamCommand(
    private val dao: FollowerRepository,
    private val service: SubscriptionService,
    logger: KVLogger,
) : AbstractMigrateToEventStreamCommandExecutor<Follower>(logger) {
    override fun getItemsToMigrate(): List<Follower> =
        dao.findAll().toList()

    override fun migrate(item: Follower) {
        service.subscribe(
            SubscribeCommand(
                userId = item.userId,
                subscriberId = item.followerUserId,
                timestamp = item.followDateTime.time,
            ),
        )
    }
}
