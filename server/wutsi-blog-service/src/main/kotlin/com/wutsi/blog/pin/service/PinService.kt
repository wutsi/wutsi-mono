package com.wutsi.blog.pin.service

import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType.STORY_PINED_EVENT
import com.wutsi.blog.event.EventType.STORY_UNPINED_EVENT
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
        log(command)
        if (execute(command)) {
            notify(STORY_PINED_EVENT, command.storyId, command.timestamp)
        }
    }

    @Transactional
    fun unpin(command: UnpinStoryCommand) {
        log(command)
        execute(command)
        notify(STORY_UNPINED_EVENT, command.storyId, command.timestamp)
    }

    private fun execute(command: PinStoryCommand): Boolean {
        val story = storyService.findById(command.storyId)
        val entity = dao.findById(story.userId)
        if (entity.isPresent) {
            val pin = entity.get()
            if (pin.storyId != story.id) {
                pin.storyId = story.id!!
                pin.timestamp = Date()

                dao.save(pin)
                logger.add("pin_status", "created")
            } else {
                logger.add("story_already_pinned", true)
                return false
            }
        } else {
            dao.save(
                PinStoryEntity(
                    userId = story.userId,
                    storyId = story.id!!,
                    timestamp = Date(),
                ),
            )
            logger.add("pin_status", "updated")
        }
        return true
    }

    private fun execute(command: UnpinStoryCommand) {
        val story = storyService.findById(command.storyId)
        val entity = dao.findById(story.userId)
        if (entity.isPresent) {
            dao.delete(entity.get())
            logger.add("pin_status", "deleted")
        } else {
            logger.add("pin_not_found", true)
        }
    }

    private fun log(command: PinStoryCommand) {
        logger.add("command_story_id", command.storyId)
        logger.add("command_timestamp", command.timestamp)
    }

    private fun log(command: UnpinStoryCommand) {
        logger.add("command_story_id", command.storyId)
        logger.add("command_timestamp", command.timestamp)
    }

    private fun notify(type: String, storyId: Long, timestamp: Long) {
        val eventId = eventStore.store(
            Event(
                streamId = StreamId.PIN,
                type = type,
                entityId = storyId.toString(),
                timestamp = Date(timestamp),
            ),
        )
        logger.add("evt_id", eventId)

        val payload = EventPayload(eventId = eventId)
        eventStream.publish(type, payload)
    }
}
