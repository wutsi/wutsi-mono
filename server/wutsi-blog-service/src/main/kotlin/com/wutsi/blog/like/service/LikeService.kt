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
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import java.util.Date
import javax.transaction.Transactional
import kotlin.math.max

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
        val eventId = eventStore.store(
            Event(
                streamId = StreamId.LIKE,
                type = STORY_LIKED_EVENT,
                entityId = command.storyId.toString(),
                userId = command.userId?.toString(),
                deviceId = command.deviceId,
                payload = command,
            ),
        )

        val payload = EventPayload(eventId = eventId)
        eventStream.enqueue(STORY_LIKED_EVENT, payload)
        eventStream.publish(STORY_LIKED_EVENT, payload)
    }

    @Transactional
    fun unlike(command: UnlikeStoryCommand) {
        val eventId = eventStore.store(
            Event(
                streamId = StreamId.LIKE,
                type = STORY_UNLIKED_EVENT,
                entityId = command.storyId.toString(),
                userId = command.userId?.toString(),
                deviceId = command.deviceId,
                timestamp = Date(command.timestamp),
                payload = command,
            ),
        )

        val payload = EventPayload(eventId = eventId)
        eventStream.enqueue(STORY_UNLIKED_EVENT, payload)
        eventStream.publish(STORY_UNLIKED_EVENT, payload)
    }

    @Transactional
    fun onLiked(payload: EventPayload) {
        try {
            // Like
            val event = eventStore.event(payload.eventId)
            val storyId = event.entityId.toLong()
            val like = LikeEntity(
                storyId = storyId,
                userId = event.userId?.toLong(),
                deviceId = if (event.userId == null) event.deviceId else null,
                timestamp = event.timestamp,
            )
            likeDao.save(like)
            logger.add("like_created", true)

            // Story
            val story = storyDao.findById(storyId)
            if (story.isEmpty) {
                storyDao.save(
                    LikeStoryEntity(
                        storyId = storyId,
                        count = 1,
                    ),
                )
            } else {
                val item = story.get()
                item.count++
                storyDao.save(item)
            }
        } catch (ex: DataIntegrityViolationException) {
            LOGGER.warn("Duplicate entry", ex)
            logger.add("like_already_created", true)
        }
    }

    @Transactional
    fun onUnliked(payload: EventPayload) {
        // Unlike
        val event = eventStore.event(payload.eventId)
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
            logger.add("like_deleted", true)
        }

        // Story
        val story = storyDao.findById(storyId)
        if (story.isPresent) {
            val item = story.get()
            item.count = max(0, item.count - 1)
            storyDao.save(item)
        }
    }
}
