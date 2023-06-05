package com.wutsi.blog.story.endpoint

import com.wutsi.blog.AbstractMigrateToEventStreamCommandExecutor
import com.wutsi.blog.story.dao.StoryRepository
import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.story.migration.StoryMigrator
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.scheduling.annotation.Async
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/stories/commands/migrate-to-event-stream")
class MigrateToEventStoreCommandExecutor(
    private val dao: StoryRepository,
    private val migrator: StoryMigrator,
    logger: KVLogger,
) : AbstractMigrateToEventStreamCommandExecutor<StoryEntity>(logger) {
    @Async
    @GetMapping
    @Transactional
    override fun execute() {
        super.execute()
    }

    override fun getItemsToMigrate(): List<StoryEntity> =
        dao.findAll().toList()

    override fun migrate(item: StoryEntity) {
        migrator.migrate(item)
    }
}
