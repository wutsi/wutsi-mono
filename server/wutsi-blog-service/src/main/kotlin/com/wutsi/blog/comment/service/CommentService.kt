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
        log(command)

        try {
            if (!isValid(command)) {
                return
            }

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
            logger.add("evt_id", eventId)

            val payload = EventPayload(eventId = eventId)
            eventStream.enqueue(STORY_COMMENTED_EVENT, payload)
            eventStream.publish(STORY_COMMENTED_EVENT, payload)
        } catch (ex: Exception) {
            LOGGER.warn("Duplicate entry", ex)
            logger.add("comment_already_created", true)
        }
    }

    @Transactional
    fun onCommented(payload: EventPayload) {
        val event = eventStore.event(payload.eventId)
        log(event)

        try {
            val comment = CommentEntity(
                storyId = event.entityId.toLong(),
                userId = event.userId!!.toLong(),
                eventId = payload.eventId,
                timestamp = event.timestamp,
            )
            commentDao.save(comment)
            logger.add("comment_status", "created")

            // Story
            updateStory(comment.storyId)
        } catch (ex: DataIntegrityViolationException) {
            LOGGER.warn("Duplicate entry", ex)
            logger.add("comment_already_created", true)
        }
    }

    private fun isValid(command: CommentStoryCommand): Boolean {
        if (command.text.trim().isEmpty()) {
            logger.add("validation_failure", "empty")
            return false
        }
        return true
    }

    private fun updateStory(storyId: Long) {
        val opt = storyDao.findById(storyId)
        if (opt.isEmpty) {
            storyDao.save(
                CommentStoryEntity(
                    storyId = storyId,
                    count = commentDao.countByStoryId(storyId)
                )
            )
        } else {
            val story = opt.get()
            story.count = commentDao.countByStoryId(storyId)
            storyDao.save(story)
        }
    }

    private fun log(command: CommentStoryCommand) {
        logger.add("command_user_id", command.userId)
        logger.add("command_storyId", command.storyId)
        logger.add("command_text", command.text)
        logger.add("command_timestamp", command.timestamp)
    }

    private fun log(event: Event) {
        logger.add("evt_id", event.id)
        logger.add("evt_type", event.type)
        logger.add("evt_entity_id", event.entityId)
        logger.add("evt_user_id", event.userId)
        logger.add("evt_payload", event.payload)
    }
}
