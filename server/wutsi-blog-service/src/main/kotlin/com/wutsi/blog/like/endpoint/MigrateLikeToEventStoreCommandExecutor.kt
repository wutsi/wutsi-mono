package com.wutsi.blog.like.endpoint

import com.wutsi.blog.AbstractMigrateToEventStreamCommandExecutor
import com.wutsi.blog.like.dao.LikeV0Repository
import com.wutsi.blog.like.domain.LikeV0
import com.wutsi.blog.like.dto.LikeStoryCommand
import com.wutsi.blog.like.service.LikeService
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/likes/commands/migrate-to-event-stream")
class MigrateLikeToEventStoreCommandExecutor(
    private val likeDao: LikeV0Repository,
    private val service: LikeService,
    logger: KVLogger,
) : AbstractMigrateToEventStreamCommandExecutor<LikeV0>(logger) {
    override fun getItemsToMigrate(): List<LikeV0> =
        likeDao.findAll().toList()

    override fun migrate(item: LikeV0) {
        service.like(
            LikeStoryCommand(
                storyId = item.story.id!!,
                userId = item.user?.id,
                deviceId = item.deviceId,
                timestamp = item.likeDateTime.time,
            ),
        )
    }
}
