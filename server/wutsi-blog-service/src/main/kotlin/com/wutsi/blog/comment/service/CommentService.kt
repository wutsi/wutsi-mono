package com.wutsi.blog.comment.service

import com.wutsi.blog.comment.dao.CommentRepository
import com.wutsi.blog.comment.dao.CommentStoryRepository
import com.wutsi.blog.comment.domain.CommentEntity
import com.wutsi.blog.comment.domain.CommentStoryEntity
import com.wutsi.blog.comment.dto.CommentStoryCommand
import com.wutsi.blog.comment.dto.StoryCommentedEvent
import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType.STORY_COMMENTED_EVENT
import com.wutsi.blog.event.StreamId
import com.wutsi.event.store.Event
import com.wutsi.event.store.EventStore
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.EventStream
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
    @Transactional
    fun comment(command: CommentStoryCommand) {
        log(command)

        if (!isValid(command)) {
            return
        }

        execute(command)

        val payload = StoryCommentedEvent(command.text)
        notify(STORY_COMMENTED_EVENT, command.storyId, command.userId, payload, command.timestamp)
    }

    @Transactional
    fun onCommented(payload: EventPayload) {
        val event = eventStore.event(payload.eventId)
        log(event)

        updateStory(event.entityId.toLong())
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
        val count = eventStore.eventCount(StreamId.COMMENT, type = STORY_COMMENTED_EVENT, entityId = storyId.toString())
        if (opt.isEmpty) {
            storyDao.save(
                CommentStoryEntity(
                    storyId = storyId,
                    count = count,
                ),
            )
        } else {
            val story = opt.get()
            story.count = count
            storyDao.save(story)
        }
    }

    private fun execute(command: CommentStoryCommand) {
        val comment = CommentEntity(
            storyId = command.storyId,
            userId = command.userId,
            text = command.text,
            timestamp = Date(command.timestamp),
        )
        commentDao.save(comment)
        logger.add("comment_status", "created")
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

    private fun notify(type: String, storyId: Long, userId: Long, payload: Any, timestamp: Long) {
        val eventId = eventStore.store(
            Event(
                streamId = StreamId.COMMENT,
                type = type,
                entityId = storyId.toString(),
                userId = userId.toString(),
                timestamp = Date(timestamp),
                payload = payload,
            ),
        )
        logger.add("evt_id", eventId)

        val payload = EventPayload(eventId = eventId)
        eventStream.enqueue(type, payload)
        eventStream.publish(type, payload)
    }
}
