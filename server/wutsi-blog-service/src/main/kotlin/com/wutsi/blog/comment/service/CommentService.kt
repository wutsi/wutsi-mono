package com.wutsi.blog.comment.service

import com.wutsi.blog.comment.dao.CommentRepository
import com.wutsi.blog.comment.dao.CommentStoryRepository
import com.wutsi.blog.comment.domain.CommentEntity
import com.wutsi.blog.comment.domain.CommentStoryEntity
import com.wutsi.blog.comment.dto.CommentStoryCommand
import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType.COMMENT_STORY_COMMAND
import com.wutsi.blog.event.EventType.STORY_COMMENTED_EVENT
import com.wutsi.blog.event.StreamId
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
class CommentService(
    private val storyDao: CommentStoryRepository,
    private val commentDao: CommentRepository,
    private val logger: KVLogger,
    private val eventStore: EventStore,
    private val eventStream: EventStream,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(CommentService::class.java)
    }

    @Transactional
    fun comment(command: CommentStoryCommand) {
        val eventId = eventStore.store(
            Event(
                streamId = StreamId.COMMENT,
                type = COMMENT_STORY_COMMAND,
                entityId = command.storyId.toString(),
                userId = command.userId.toString(),
                payload = command,
                timestamp = Date(command.timestamp),
            ),
        )

        val payload = EventPayload(eventId = eventId)
        eventStream.enqueue(STORY_COMMENTED_EVENT, payload)
        eventStream.publish(STORY_COMMENTED_EVENT, payload)
    }

    @Transactional
    fun onCommented(payload: EventPayload) {
        try {
            // Comment
            val event = eventStore.event(payload.eventId)
            val storyId = event.entityId.toLong()
            val comment = CommentEntity(
                storyId = storyId,
                userId = event.userId!!.toLong(),
                eventId = payload.eventId,
                timestamp = event.timestamp,
            )
            commentDao.save(comment)
            logger.add("comment_created", true)

            // Story
            val story = storyDao.findById(storyId)
            if (story.isEmpty) {
                storyDao.save(
                    CommentStoryEntity(
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
            logger.add("comment_already_created", true)
        }
    }
}
