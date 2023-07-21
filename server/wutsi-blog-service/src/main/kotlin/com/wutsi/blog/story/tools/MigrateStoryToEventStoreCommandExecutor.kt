package com.wutsi.blog.story.tools

import com.wutsi.blog.AbstractMigrateToEventStreamCommandExecutor
import com.wutsi.blog.story.dao.StoryRepository
import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.scheduling.annotation.Async
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Deprecated("")
@RestController
@RequestMapping("/v1/stories/commands/migrate-to-event-stream")
class MigrateStoryToEventStoreCommandExecutor(
    private val dao: StoryRepository,
    private val migrator: StoryMigrator,
    logger: KVLogger,
) : AbstractMigrateToEventStreamCommandExecutor<StoryEntity>(logger) {
    @Async
    @GetMapping
    override fun execute() {
        super.execute()
    }

    override fun getItemsToMigrate(): List<StoryEntity> =
        dao.findAll().toList()

    override fun migrate(item: StoryEntity) {
        migrator.migrate(item)
    }
}
