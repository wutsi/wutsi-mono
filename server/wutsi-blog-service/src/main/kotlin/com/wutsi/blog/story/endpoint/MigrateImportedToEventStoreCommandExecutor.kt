package com.wutsi.blog.story.endpoint

import com.wutsi.blog.AbstractMigrateToEventStreamCommandExecutor
import com.wutsi.blog.event.EventType
import com.wutsi.blog.story.dao.StoryRepository
import com.wutsi.blog.story.domain.Story
import com.wutsi.blog.story.dto.StoryImportedEventPayload
import com.wutsi.blog.story.service.StoryService
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/stories/commands/migrate-imported-to-event-stream")
class MigrateImportedToEventStoreCommandExecutor(
    private val dao: StoryRepository,
    private val service: StoryService,
    logger: KVLogger,
) : AbstractMigrateToEventStreamCommandExecutor<Story>(logger) {
    override fun getItemsToMigrate(): List<Story> =
        dao.findBySourceUrlNotNull().toList()

    override fun migrate(item: Story) {
        val payload = StoryImportedEventPayload(item.sourceUrl!!)
        service.notify(EventType.STORY_IMPORTED_EVENT, item.id!!, item.userId, item.creationDateTime.time, payload)
    }
}
