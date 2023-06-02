package com.wutsi.blog.like.service

import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType.STORY_LIKED_EVENT
import com.wutsi.blog.event.EventType.STORY_UNLIKED_EVENT
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
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.lang.Long.max
import java.util.Date

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

        execute(command)
        logger.add("like_status", "created")

        notify(STORY_LIKED_EVENT, command.storyId, command.userId, command.deviceId, command.timestamp)
    }

    @Transactional
    fun unlike(command: UnlikeStoryCommand) {
        log(command)

        if (execute(command)) {
            logger.add("like_status", "deleted")

            notify(STORY_UNLIKED_EVENT, command.storyId, command.userId, command.deviceId, command.timestamp)
        }
    }

    @Transactional
    fun onLiked(payload: EventPayload) {
        val event = eventStore.event(payload.eventId)
        log(event)

        val count = updateCounter(event.entityId.toLong())
        logger.add("count", count)
    }

    @Transactional
    fun onUnliked(payload: EventPayload) {
        val event = eventStore.event(payload.eventId)
        log(event)

        val count = updateCounter(event.entityId.toLong())
        logger.add("count", count)
    }

    private fun updateCounter(storyId: Long): Long {
        val opt = storyDao.findById(storyId)
        val count = max(
            0L,
            count(storyId, STORY_LIKED_EVENT) - count(storyId, STORY_UNLIKED_EVENT),
        )
        if (opt.isEmpty) {
            storyDao.save(
                LikeStoryEntity(
                    storyId = storyId,
                    count = count,
                ),
            )
        } else {
            val counter = opt.get()
            counter.count = count
            storyDao.save(counter)
        }
        return count
    }

    private fun count(storyId: Long, type: String): Long =
        eventStore.eventCount(streamId = StreamId.LIKE, entityId = storyId.toString(), type = type)

    private fun execute(command: LikeStoryCommand) {
        // Like
        val like = LikeEntity(
            storyId = command.storyId,
            userId = command.userId,
            deviceId = if (command.userId == null) command.deviceId else null,
            timestamp = Date(command.timestamp),
        )
        likeDao.save(like)
    }

    private fun execute(command: UnlikeStoryCommand): Boolean {
        // Like
        val like = if (command.userId != null) {
            likeDao.findByStoryIdAndUserId(command.storyId, command.userId!!)
        } else if (command.deviceId != null) {
            likeDao.findByStoryIdAndDeviceId(command.storyId, command.deviceId!!)
        } else {
            null
        }

        if (like != null) {
            likeDao.delete(like)
            return true
        } else {
            return false
        }
    }

    private fun notify(type: String, storyId: Long, userId: Long?, deviceId: String?, timestamp: Long) {
        val eventId = eventStore.store(
            Event(
                streamId = StreamId.LIKE,
                type = type,
                entityId = storyId.toString(),
                userId = userId?.toString(),
                deviceId = deviceId,
                timestamp = Date(timestamp),
                payload = null,
            ),
        )
        logger.add("evt_id", eventId)

        val payload = EventPayload(eventId = eventId)
        eventStream.enqueue(type, payload)
        eventStream.publish(type, payload)
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
