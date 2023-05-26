package com.wutsi.blog.pin.service

import com.wutsi.blog.event.StreamId
import com.wutsi.blog.pin.dao.PinStoryRepository
import com.wutsi.blog.pin.domain.PinStoryEntity
import com.wutsi.blog.pin.dto.PinEventType
import com.wutsi.blog.pin.dto.PinStoryCommand
import com.wutsi.blog.pin.dto.StoryPinedEvent
import com.wutsi.blog.pin.dto.StoryUnpinedEvent
import com.wutsi.blog.pin.dto.UnpinStoryCommand
import com.wutsi.blog.story.service.StoryService
import com.wutsi.event.store.Event
import com.wutsi.event.store.EventStore
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
) {
    @Transactional
    fun pin(command: PinStoryCommand) {
        val payload = StoryPinedEvent(
            storyId = command.storyId,
            timestamp = System.currentTimeMillis(),
        )
        val event = Event(
            streamId = StreamId.PIN,
            type = PinEventType.STORY_PINED_EVENT,
            entityId = command.storyId.toString(),
            payload = payload,
        )
        eventStore.store(event)
        eventStream.enqueue(event.type, payload)
        eventStream.publish(event.type, payload)
    }

    @Transactional
    fun unpin(command: UnpinStoryCommand) {
        val payload = StoryUnpinedEvent(
            storyId = command.storyId,
            timestamp = System.currentTimeMillis(),
        )
        val event = Event(
            streamId = StreamId.PIN,
            type = PinEventType.STORY_UNPINED_EVENT,
            entityId = command.storyId.toString(),
            payload = payload,
        )
        eventStore.store(event)
        eventStream.enqueue(event.type, payload)
        eventStream.publish(event.type, payload)
    }

    @Transactional
    fun onPinned(event: StoryPinedEvent): PinStoryEntity {
        val story = storyService.findById(event.storyId)
        val entity = dao.findById(story.userId)
        return if (entity.isPresent) {
            val pin = entity.get()
            pin.storyId = story.id!!
            pin.timestamp = Date()

            dao.save(pin)
        } else {
            dao.save(
                PinStoryEntity(
                    userId = story.userId,
                    storyId = story.id!!,
                    timestamp = Date(),
                ),
            )
        }
    }

    @Transactional
    fun onUnpined(event: StoryUnpinedEvent) {
        val story = storyService.findById(event.storyId)
        val entity = dao.findById(story.userId)
        if (entity.isPresent) {
            dao.delete(entity.get())
        }
    }
}
