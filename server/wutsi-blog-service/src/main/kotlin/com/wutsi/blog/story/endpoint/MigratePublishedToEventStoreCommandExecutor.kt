package com.wutsi.blog.story.endpoint

import com.wutsi.blog.AbstractMigrateToEventStreamCommandExecutor
import com.wutsi.blog.story.dao.StoryRepository
import com.wutsi.blog.story.domain.Story
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.blog.story.migration.PublishedStoryMigrator
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.scheduling.annotation.Async
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/stories/commands/migrate-published-to-event-stream")
class MigratePublishedToEventStoreCommandExecutor(
    private val dao: StoryRepository,
    private val migrator: PublishedStoryMigrator,
    logger: KVLogger,
) : AbstractMigrateToEventStreamCommandExecutor<Story>(logger) {
    @Async
    @GetMapping
    @Transactional
    override fun execute() {
        super.execute()
    }

    override fun getItemsToMigrate(): List<Story> =
        dao.findByStatus(StoryStatus.PUBLISHED).toList()

    override fun migrate(item: Story) {
        migrator.migrate(item)
    }
}
