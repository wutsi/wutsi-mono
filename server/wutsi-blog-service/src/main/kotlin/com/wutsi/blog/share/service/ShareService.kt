package com.wutsi.blog.share.service

import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType.STORY_SHARED_EVENT
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.share.dao.ShareRepository
import com.wutsi.blog.share.domain.ShareEntity
import com.wutsi.blog.share.dto.ShareStoryCommand
import com.wutsi.blog.story.service.StoryService
import com.wutsi.event.store.Event
import com.wutsi.event.store.EventStore
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.EventStream
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date

@Service
class ShareService(
    private val shareDao: ShareRepository,
    private val storyService: StoryService,
    private val eventStore: EventStore,
    private val eventStream: EventStream,
    private val logger: KVLogger,
) {
    fun search(storyIds: List<Long>, userId: Long): List<ShareEntity> =
        shareDao.findByStoryIdInAndUserId(storyIds, userId)

    @Transactional
    fun share(command: ShareStoryCommand) {
        log(command)
        execute(command)
        notify(STORY_SHARED_EVENT, command.storyId, command.userId, command.timestamp)
    }

    private fun execute(command: ShareStoryCommand) {
        shareDao.save(
            ShareEntity(
                userId = command.userId,
                storyId = command.storyId,
                timestamp = Date(command.timestamp),
            ),
        )
    }

    @Transactional
    fun onShared(payload: EventPayload) {
        val event = eventStore.event(payload.eventId)
        log(event)

        val story = storyService.findById(event.entityId.toLong())
        storyService.onStoryShared(story)
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
