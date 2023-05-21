package com.wutsi.blog.like.service

import com.wutsi.blog.event.StreamId
import com.wutsi.blog.like.dao.LikeRepository
import com.wutsi.blog.like.dao.LikeStoryRepository
import com.wutsi.blog.like.domain.LikeEntity
import com.wutsi.blog.like.domain.LikeStoryEntity
import com.wutsi.blog.like.dto.LikeEventType
import com.wutsi.blog.like.dto.LikeStoryCommand
import com.wutsi.blog.like.dto.StoryLikedEvent
import com.wutsi.blog.like.dto.StoryUnlikedEvent
import com.wutsi.blog.like.dto.UnlikeStoryCommand
import com.wutsi.event.store.Event
import com.wutsi.event.store.EventStore
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.platform.core.tracing.TracingContext
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestBody
import java.util.Date
import javax.transaction.Transactional
import javax.validation.Valid
import kotlin.math.max

@Service
class LikeService(
    private val likeDao: LikeRepository,
    private val storyDao: LikeStoryRepository,
    private val logger: KVLogger,
    private val tracingContext: TracingContext,
    private val eventStore: EventStore,
    private val eventStream: EventStream,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(LikeService::class.java)
    }

    @Transactional
    fun like(command: LikeStoryCommand) {
        val payload = StoryLikedEvent(
            storyId = command.storyId,
            userId = command.userId,
            deviceId = tracingContext.deviceId(),
        )
        val event = Event(
            streamId = StreamId.LIKE,
            type = LikeEventType.STORY_LIKED,
            entityId = command.storyId.toString(),
            userId = command.userId?.toString(),
            deviceId = tracingContext.deviceId(),
            payload = payload,
        )
        eventStore.store(event)
        eventStream.enqueue(event.type, payload)
        eventStream.publish(event.type, payload)
    }

    @Transactional
    fun unlike(@Valid @RequestBody command: UnlikeStoryCommand) {
        val payload = StoryUnlikedEvent(
            storyId = command.storyId,
            userId = command.userId,
            deviceId = tracingContext.deviceId(),
        )
        val event = Event(
            streamId = StreamId.LIKE,
            type = LikeEventType.STORY_UNLIKED,
            entityId = command.storyId.toString(),
            userId = command.userId?.toString(),
            deviceId = tracingContext.deviceId(),
            payload = payload,
        )
        eventStore.store(event)
        eventStream.enqueue(event.type, payload)
        eventStream.publish(event.type, payload)
    }

    @Transactional
    fun onLiked(event: StoryLikedEvent) {
        try {
            // Like
            val like = LikeEntity(
                storyId = event.storyId,
                userId = event.userId,
                deviceId = if (event.userId == null) event.deviceId else null,
                timestamp = Date(event.timestamp),
            )
            likeDao.save(like)
            logger.add("like_created", true)

            // Story
            val story = storyDao.findById(event.storyId)
            if (story.isEmpty) {
                storyDao.save(
                    LikeStoryEntity(
                        storyId = event.storyId,
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
    fun onUnliked(event: StoryUnlikedEvent) {
        // Unlike
        val like = if (event.userId != null) {
            likeDao.findByStoryIdAndUserId(event.storyId, event.userId)
        } else {
            likeDao.findByStoryIdAndDeviceId(event.storyId, event.deviceId)
        }
        if (like != null) {
            likeDao.delete(like)
            logger.add("like_deleted", true)
        }

        // Story
        val story = storyDao.findById(event.storyId)
        if (story.isPresent) {
            val item = story.get()
            item.count = max(0, item.count - 1)
            storyDao.save(item)
        }
    }
}
