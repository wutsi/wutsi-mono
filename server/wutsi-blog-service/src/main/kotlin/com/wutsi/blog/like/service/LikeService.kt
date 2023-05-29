package com.wutsi.blog.like.service

import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType.LIKE_STORY_COMMAND
import com.wutsi.blog.event.EventType.STORY_LIKED_EVENT
import com.wutsi.blog.event.EventType.STORY_UNLIKED_EVENT
import com.wutsi.blog.event.EventType.UNLIKE_STORY_COMMAND
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.like.dao.LikeRepository
import com.wutsi.blog.like.dao.LikeStoryRepository
import com.wutsi.blog.like.domain.LikeEntity
import com.wutsi.blog.like.domain.LikeStoryEntity
import com.wutsi.blog.like.dto.LikeStoryCommand
import com.wutsi.blog.like.dto.UnlikeStoryCommand
import com.wutsi.event.store.Event
import com.wutsi.event.store.EventStore
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.EventStream
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import java.util.Date
import javax.transaction.Transactional

@Service
class LikeService(
    private val likeDao: LikeRepository,
    private val storyDao: LikeStoryRepository,
    private val logger: KVLogger,
    private val eventStore: EventStore,
    private val eventStream: EventStream,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(LikeService::class.java)
    }

    @Transactional
    fun like(command: LikeStoryCommand) {
        log(command)

        val eventId = eventStore.store(
            Event(
                streamId = StreamId.LIKE,
                type = LIKE_STORY_COMMAND,
                entityId = command.storyId.toString(),
                userId = command.userId?.toString(),
                deviceId = command.deviceId,
                payload = command,
            ),
        )
        logger.add("evt_id", eventId)

        val payload = EventPayload(eventId = eventId)
        eventStream.enqueue(STORY_LIKED_EVENT, payload)
        eventStream.publish(STORY_LIKED_EVENT, payload)
    }

    @Transactional
    fun unlike(command: UnlikeStoryCommand) {
        log(command)

        val eventId = eventStore.store(
            Event(
                streamId = StreamId.LIKE,
                type = UNLIKE_STORY_COMMAND,
                entityId = command.storyId.toString(),
                userId = command.userId?.toString(),
                deviceId = command.deviceId,
                timestamp = Date(command.timestamp),
                payload = command,
            ),
        )
        logger.add("evt_id", eventId)

        val payload = EventPayload(eventId = eventId)
        eventStream.enqueue(STORY_UNLIKED_EVENT, payload)
        eventStream.publish(STORY_UNLIKED_EVENT, payload)
    }

    @Transactional
    fun onLiked(payload: EventPayload) {
        val event = eventStore.event(payload.eventId)
        log(event)

        try {
            // Like
            val storyId = event.entityId.toLong()
            val like = LikeEntity(
                storyId = storyId,
                userId = event.userId?.toLong(),
                deviceId = if (event.userId == null) event.deviceId else null,
                timestamp = event.timestamp,
            )
            likeDao.save(like)
            logger.add("like_status", "created")

            // Story
            updateStory(storyId)
        } catch (ex: DataIntegrityViolationException) {
            LOGGER.warn("Duplicate entry", ex)
            logger.add("like_already_created", true)
        }
    }

    @Transactional
    fun onUnliked(payload: EventPayload) {
        val event = eventStore.event(payload.eventId)
        log(event)

        // Unlike
        val storyId = event.entityId.toLong()
        val like = if (event.userId != null) {
            likeDao.findByStoryIdAndUserId(storyId, event.userId!!.toLong())
        } else if (event.deviceId != null) {
            likeDao.findByStoryIdAndDeviceId(storyId, event.deviceId!!)
        } else {
            null
        }

        if (like != null) {
            likeDao.delete(like)
            logger.add("like_status", "deleted")
        } else {
            logger.add("like_not_found", true)
        }

        // Story
        updateStory(storyId)
    }

    private fun updateStory(storyId: Long) {
        val opt = storyDao.findById(storyId)
        if (opt.isEmpty) {
            storyDao.save(
                LikeStoryEntity(
                    storyId = storyId,
                    count = likeDao.countByStoryId(storyId),
                ),
            )
        } else {
            val counter = opt.get()
            counter.count = likeDao.countByStoryId(storyId)
            storyDao.save(counter)
        }
    }

    private fun log(command: LikeStoryCommand) {
        logger.add("command_story_id", command.storyId)
        logger.add("command_user_id", command.userId)
        logger.add("command_device_id", command.deviceId)
        logger.add("command_timestamp", command.timestamp)
    }

    private fun log(command: UnlikeStoryCommand) {
        logger.add("command_story_id", command.storyId)
        logger.add("command_user_id", command.userId)
        logger.add("command_device_id", command.deviceId)
        logger.add("command_timestamp", command.timestamp)
    }

    private fun log(event: Event) {
        logger.add("evt_id", event.id)
        logger.add("evt_type", event.type)
        logger.add("evt_entity_id", event.entityId)
        logger.add("evt_user_id", event.userId)
        logger.add("evt_device_id", event.deviceId)
    }
}
