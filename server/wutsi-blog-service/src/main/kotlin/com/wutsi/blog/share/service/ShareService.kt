package com.wutsi.blog.share.service

import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType.SHARE_STORY_COMMAND
import com.wutsi.blog.event.EventType.STORY_SHARED_EVENT
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.share.dao.ShareStoryRepository
import com.wutsi.blog.share.domain.ShareStoryEntity
import com.wutsi.blog.share.dto.ShareStoryCommand
import com.wutsi.event.store.Event
import com.wutsi.event.store.EventStore
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.EventStream
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class ShareService(
    private val storyDao: ShareStoryRepository,
    private val eventStore: EventStore,
    private val eventStream: EventStream,
    private val logger: KVLogger,
) {
    @Transactional
    fun share(command: ShareStoryCommand) {
        log(command)

        val eventId = eventStore.store(
            Event(
                streamId = StreamId.SHARE,
                type = SHARE_STORY_COMMAND,
                entityId = command.storyId.toString(),
                userId = command.userId?.toString(),
                payload = command,
            ),
        )
        logger.add("evt_id", eventId)

        val payload = EventPayload(eventId = eventId)
        eventStream.enqueue(STORY_SHARED_EVENT, payload)
        eventStream.publish(STORY_SHARED_EVENT, payload)
    }

    @Transactional
    fun onShared(payload: EventPayload) {
        val event = eventStore.event(payload.eventId)
        log(event)

        val storyId = event.entityId.toLong()
        updateStory(storyId)
    }

    private fun updateStory(storyId: Long) {
        val opt = storyDao.findById(storyId)
        val count = eventStore.eventCount(StreamId.SHARE, entityId = storyId.toString(), type = SHARE_STORY_COMMAND)

        if (opt.isEmpty) {
            storyDao.save(
                ShareStoryEntity(
                    storyId = storyId,
                    count = count
                ),
            )
        } else {
            val counter = opt.get()
            counter.count = count
            storyDao.save(counter)
        }
    }

    private fun log(command: ShareStoryCommand) {
        logger.add("command_story_id", command.storyId)
        logger.add("command_user_id", command.userId)
        logger.add("command_timestamp", command.timestamp)
    }

    private fun log(event: Event) {
        logger.add("evt_id", event.id)
        logger.add("evt_type", event.type)
        logger.add("evt_entity_id", event.entityId)
        logger.add("evt_user_id", event.userId)
    }
}
