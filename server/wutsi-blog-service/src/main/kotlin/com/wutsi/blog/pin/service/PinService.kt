package com.wutsi.blog.pin.service

import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType.STORY_PINED_EVENT
import com.wutsi.blog.event.EventType.STORY_UNPINED_EVENT
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.pin.dto.PinStoryCommand
import com.wutsi.blog.pin.dto.UnpinStoryCommand
import com.wutsi.blog.story.dao.StoryRepository
import com.wutsi.blog.user.service.UserService
import com.wutsi.event.store.Event
import com.wutsi.event.store.EventStore
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.EventStream
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date

@Service
class PinService(
    private val storyDao: StoryRepository,
    private val userService: UserService,
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

    private fun execute(command: PinStoryCommand): Boolean {
        val story = storyDao.findById(command.storyId).get()
        return userService.pin(story, command.timestamp)
    }

    @Transactional
    fun unpin(command: UnpinStoryCommand) {
        log(command)
        if (execute(command)) {
            notify(STORY_UNPINED_EVENT, command.storyId, command.timestamp)
        }
    }

    private fun execute(command: UnpinStoryCommand): Boolean {
        val story = storyDao.findById(command.storyId).get()
        return userService.unpin(story)
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
