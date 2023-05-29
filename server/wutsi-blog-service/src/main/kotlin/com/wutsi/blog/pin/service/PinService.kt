package com.wutsi.blog.pin.service

import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType.PIN_STORY_COMMAND
import com.wutsi.blog.event.EventType.STORY_PINED_EVENT
import com.wutsi.blog.event.EventType.STORY_UNPINED_EVENT
import com.wutsi.blog.event.EventType.UNPIN_STORY_COMMAND
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.pin.dao.PinStoryRepository
import com.wutsi.blog.pin.domain.PinStoryEntity
import com.wutsi.blog.pin.dto.PinStoryCommand
import com.wutsi.blog.pin.dto.UnpinStoryCommand
import com.wutsi.blog.story.service.StoryService
import com.wutsi.event.store.Event
import com.wutsi.event.store.EventStore
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.EventStream
import org.springframework.stereotype.Service
import java.util.Date
import javax.transaction.Transactional

@Service
class PinService(
    private val storyService: StoryService,
    private val dao: PinStoryRepository,
    private val eventStore: EventStore,
    private val eventStream: EventStream,
    private val logger: KVLogger,
) {
    @Transactional
    fun pin(command: PinStoryCommand) {
        val eventId = eventStore.store(
            Event(
                streamId = StreamId.PIN,
                type = PIN_STORY_COMMAND,
                entityId = command.storyId.toString(),
                payload = command,
            ),
        )

        val payload = EventPayload(eventId = eventId)
        eventStream.enqueue(STORY_PINED_EVENT, payload)
        eventStream.publish(STORY_PINED_EVENT, payload)
    }

    @Transactional
    fun unpin(command: UnpinStoryCommand) {
        val eventId = eventStore.store(
            Event(
                streamId = StreamId.PIN,
                type = UNPIN_STORY_COMMAND,
                entityId = command.storyId.toString(),
                timestamp = Date(command.timestamp),
                payload = command,
            ),
        )

        val payload = EventPayload(eventId = eventId)
        eventStream.enqueue(STORY_UNPINED_EVENT, payload)
        eventStream.publish(STORY_UNPINED_EVENT, payload)
    }

    @Transactional
    fun onPinned(payload: EventPayload): PinStoryEntity {
        val event = eventStore.event(payload.eventId)
        val story = storyService.findById(event.entityId.toLong())
        val entity = dao.findById(story.userId)

        return if (entity.isPresent) {
            val pin = entity.get()
            pin.storyId = story.id!!
            pin.timestamp = Date()

            dao.save(pin)
            logger.add("pin_status", "created")

            pin
        } else {
            val pin = dao.save(
                PinStoryEntity(
                    userId = story.userId,
                    storyId = story.id!!,
                    timestamp = Date(),
                ),
            )
            logger.add("pin_status", "updated")

            pin
        }
    }

    @Transactional
    fun onUnpined(payload: EventPayload) {
        val event = eventStore.event(payload.eventId)
        val story = storyService.findById(event.entityId.toLong())
        val entity = dao.findById(story.userId)
        if (entity.isPresent) {
            dao.delete(entity.get())
            logger.add("pin_status", "deleted")
        } else {
            logger.add("pin_not_found", true)
        }
    }
}
