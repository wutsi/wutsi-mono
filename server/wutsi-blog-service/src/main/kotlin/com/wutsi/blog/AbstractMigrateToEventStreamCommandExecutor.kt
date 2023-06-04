package com.wutsi.blog.pin.endpoint

import com.wutsi.blog.pin.dao.PinRepository
import com.wutsi.blog.pin.domain.Pin
import com.wutsi.blog.pin.dto.PinStoryCommand
import com.wutsi.blog.pin.service.PinService
import com.wutsi.platform.core.logging.KVLogger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/pins/commands/migrate-to-event-stream")
class MigratePinToEventStreamCommandExecutor(
    private val dao: PinRepository,
    private val service: PinService,
    private val logger: KVLogger,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(MigratePinToEventStreamCommandExecutor::class.java)
    }

    @Async
    @GetMapping
    fun execute() {
        val pins = dao.findAll()
        logger.add("pin_count", pins.toList().size)

        var migrated = 0
        var errors = 0
        pins.forEach {
            try {
                migrate(it)
                migrated++
            } catch (ex: Exception) {
                LOGGER.warn("Unable to migration $it", ex)
                errors++
            } finally {
                logger.log()
            }
        }
        logger.add("migration_count", migrated)
        logger.add("migration_errors", errors)
    }

    private fun migrate(pin: Pin) {
        service.pin(
            PinStoryCommand(
                storyId = pin.storyId,
                timestamp = pin.creationDateTime.time,
            )
        )
    }
}
