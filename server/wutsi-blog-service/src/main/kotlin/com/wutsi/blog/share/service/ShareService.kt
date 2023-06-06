package com.wutsi.blog.share.service

import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType.STORY_SHARED_EVENT
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.share.dao.ShareStoryRepository
import com.wutsi.blog.share.domain.ShareStoryEntity
import com.wutsi.blog.share.dto.CountShareRequest
import com.wutsi.blog.share.dto.CountShareResponse
import com.wutsi.blog.share.dto.ShareCounter
import com.wutsi.blog.share.dto.ShareStoryCommand
import com.wutsi.event.store.Event
import com.wutsi.event.store.EventStore
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.EventStream
import org.springframework.stereotype.Service
import java.util.Date
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
        notify(STORY_SHARED_EVENT, command.storyId, command.userId, command.timestamp)
    }

    @Transactional
    fun onShared(payload: EventPayload) {
        val event = eventStore.event(payload.eventId)
        log(event)

        val storyId = event.entityId.toLong()
        val count = updateStory(storyId)
        logger.add("count", count)
    }

    fun count(request: CountShareRequest): CountShareResponse {
        // Stories
        val stories = storyDao.findAllById(request.storyIds.toSet()).toList()
        if (stories.isEmpty()) {
            return CountShareResponse()
        }

        // Result
        return CountShareResponse(
            counters = stories.map {
                ShareCounter(
                    storyId = it.storyId,
                    count = it.count,
                )
            },
        )
    }

    private fun updateStory(storyId: Long): Long {
        val opt = storyDao.findById(storyId)
        val count = eventStore.eventCount(StreamId.SHARE, entityId = storyId.toString(), type = STORY_SHARED_EVENT)

        val counter = if (opt.isEmpty) {
            storyDao.save(
                ShareStoryEntity(
                    storyId = storyId,
                    count = count,
                ),
            )
        } else {
            val item = opt.get()
            item.count = count
            storyDao.save(item)
        }

        return counter.count
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

    private fun notify(type: String, storyId: Long, userId: Long?, timestamp: Long) {
        val eventId = eventStore.store(
            Event(
                streamId = StreamId.SHARE,
                type = type,
                entityId = storyId.toString(),
                userId = userId?.toString(),
                timestamp = Date(timestamp),
            ),
        )
        logger.add("evt_id", eventId)

        val payload = EventPayload(eventId = eventId)
        eventStream.enqueue(type, payload)
        eventStream.publish(type, payload)
    }
}
