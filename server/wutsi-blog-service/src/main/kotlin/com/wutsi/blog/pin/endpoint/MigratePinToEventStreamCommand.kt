package com.wutsi.blog.pin.endpoint

import com.wutsi.blog.event.EventType.PIN_STORY_COMMAND
import com.wutsi.blog.pin.dao.PinRepository
import com.wutsi.blog.pin.dto.PinStoryCommand
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.EventStream
import org.springframework.scheduling.annotation.Async
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/pins/commands/migrate-to-event-stream")
class MigratePinToEventStreamCommand(
    private val dao: PinRepository,
    private val eventStream: EventStream,
    private val logger: KVLogger,
) {
    @Async
    @GetMapping
    fun migrate() {
        val pins = dao.findAll()
        logger.add("pin_count", pins.toList().size)

        var migrated = 0
        pins.forEach {
            eventStream.enqueue(
                type = PIN_STORY_COMMAND,
                payload = PinStoryCommand(
                    storyId = it.storyId,
                    timestamp = it.creationDateTime.time,
                ),
            )
            migrated++
        }
        logger.add("pin_migrated", migrated)
    }
}
