package com.wutsi.blog.pin.endpoint

import com.wutsi.blog.AbstractMigrateToEventStreamCommandExecutor
import com.wutsi.blog.pin.dao.PinRepository
import com.wutsi.blog.pin.domain.Pin
import com.wutsi.blog.pin.dto.PinStoryCommand
import com.wutsi.blog.pin.service.PinService
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/pins/commands/migrate-to-event-stream")
class MigratePinToEventStreamCommandExecutor(
    private val dao: PinRepository,
    private val service: PinService,
    val logger: KVLogger,
) : AbstractMigrateToEventStreamCommandExecutor<Pin>(logger) {
    override fun getItemsToMigrate(): List<Pin> =
        dao.findAll().toList()

    override fun migrate(pin: Pin) {
        service.pin(
            PinStoryCommand(
                storyId = pin.storyId,
                timestamp = pin.creationDateTime.time,
            ),
        )
    }
}
