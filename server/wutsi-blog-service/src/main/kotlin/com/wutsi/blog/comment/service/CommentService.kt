package com.wutsi.blog.comment.service

import com.wutsi.blog.comment.dao.CommentRepository
import com.wutsi.blog.comment.domain.CommentEntity
import com.wutsi.blog.comment.dto.CommentStoryCommand
import com.wutsi.blog.comment.dto.StoryCommentedEventPayload
import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType.STORY_COMMENTED_EVENT
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.story.dao.StoryRepository
import com.wutsi.blog.story.service.ReaderService
import com.wutsi.event.store.Event
import com.wutsi.event.store.EventStore
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.EventStream
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date

@Service
class CommentService(
    private val storyDao: StoryRepository,
    private val commentDao: CommentRepository,
    private val readerService: ReaderService,
    private val logger: KVLogger,
    private val eventStore: EventStore,
    private val eventStream: EventStream,
) {
    fun search(storyIds: List<Long>, userId: Long): List<CommentEntity> =
        commentDao.findByStoryIdInAndUserId(storyIds, userId)

    fun searchByDates(start: Date, end: Date): List<CommentEntity> =
        commentDao.findByTimestampBetween(start, end)

    @Transactional
    fun comment(command: CommentStoryCommand) {
        log(command)

        if (isValid(command)) {
            execute(command)

            val payload = StoryCommentedEventPayload(command.text)
            notify(STORY_COMMENTED_EVENT, command.storyId, command.userId, payload, command.timestamp)
        }
    }

    @Transactional
    fun onCommented(payload: EventPayload) {
        val event = eventStore.event(payload.eventId)
        log(event)

        val storyId = event.entityId.toLong()
        updateStoryCounter(storyId)

        if (event.userId != null) {
            readerService.onCommented(event.userId!!.toLong(), storyId)
        }
    }

    private fun isValid(command: CommentStoryCommand): Boolean {
        if (command.text.trim().isEmpty()) {
            logger.add("validation_failure", "empty")
            return false
        }
        return true
    }

    private fun updateStoryCounter(storyId: Long) {
        val story = storyDao.findById(storyId).get()
        story.commentCount = eventStore.eventCount(
            streamId = StreamId.COMMENT,
            type = STORY_COMMENTED_EVENT,
            entityId = storyId.toString(),
        )
        story.modificationDateTime = Date()
        storyDao.save(story)
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

        val eventPayload = EventPayload(eventId = eventId)
        eventStream.enqueue(type, eventPayload)
        eventStream.publish(type, eventPayload)
    }
}
