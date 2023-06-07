package com.wutsi.blog.pin.service

import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType.STORY_PINED_EVENT
import com.wutsi.blog.event.EventType.STORY_UNPINED_EVENT
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.pin.dto.PinStoryCommand
import com.wutsi.blog.pin.dto.UnpinStoryCommand
import com.wutsi.blog.story.dao.StoryRepository
import com.wutsi.blog.user.dao.UserRepository
import com.wutsi.event.store.Event
import com.wutsi.event.store.EventStore
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.EventStream
import org.springframework.stereotype.Service
import java.util.Date
import javax.transaction.Transactional

@Service
class PinService(
    private val storyDao: StoryRepository,
    private val userDao: UserRepository,
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
        if (execute(command)) {
            notify(STORY_UNPINED_EVENT, command.storyId, command.timestamp)
        }
    }

    private fun execute(command: PinStoryCommand): Boolean {
        val story = storyDao.findById(command.storyId).get()
        val user = userDao.findById(story.userId).get()
        if (user.pinStoryId == command.storyId) {
            return false
        }

        user.pinStoryId = command.storyId
        user.pinDateTime = Date(command.timestamp)
        user.modificationDateTime = Date()
        userDao.save(user)
        return true
    }

    private fun execute(command: UnpinStoryCommand): Boolean {
        val story = storyDao.findById(command.storyId).get()
        val user = userDao.findById(story.userId).get()
        if (user.pinStoryId == null) {
            return false;
        }

        user.pinStoryId = null
        user.pinDateTime = null
        user.modificationDateTime = Date()
        userDao.save(user)
        return true
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
