package com.wutsi.blog.story.it

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.blog.event.EventType.STORY_ATTACHMENT_DOWNLOADED_EVENT
import com.wutsi.blog.event.EventType.SUBSCRIBE_COMMAND
import com.wutsi.blog.event.RootEventHandler
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.story.dao.StoryRepository
import com.wutsi.blog.story.dto.StoryAttachmentDownloadedEventPayload
import com.wutsi.blog.subscription.dto.SubscribeCommand
import com.wutsi.event.store.EventStore
import com.wutsi.platform.core.stream.Event
import com.wutsi.platform.core.stream.EventStream
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/story/StoryAttachmentDownloaded.sql"])
class StoryAttachmentDownloadedTest {
    @Autowired
    private lateinit var eventHandler: RootEventHandler

    @Autowired
    private lateinit var eventStore: EventStore

    @Autowired
    private lateinit var storyDao: StoryRepository

    @MockBean
    private lateinit var eventStream: EventStream

    @Test
    fun downloaded() {
        // WHEN
        val payload = StoryAttachmentDownloadedEventPayload(
            userId = 2L,
            storyId = 10L,
            subscribe = true,
        )
        eventHandler.handle(
            Event(
                type = STORY_ATTACHMENT_DOWNLOADED_EVENT,
                payload = ObjectMapper().writeValueAsString(payload),
            ),
        )

        // THEN
        val events = eventStore.events(
            streamId = StreamId.STORY,
            type = STORY_ATTACHMENT_DOWNLOADED_EVENT,
            entityId = payload.storyId.toString(),
            userId = payload.userId?.toString(),
        )
        assertEquals(1, events.size)

        val story = storyDao.findById(payload.storyId).get()
        assertEquals(1, story.attachmentDownloadCount)

        verify(eventStream).enqueue(
            type = SUBSCRIBE_COMMAND,
            payload = SubscribeCommand(
                userId = 1L,
                subscriberId = payload.userId!!,
                timestamp = payload.timestamp,
            ),
        )
    }

    @Test
    fun downloadedDoNotSubscribe() {
        // WHEN
        val payload = StoryAttachmentDownloadedEventPayload(
            userId = 2L,
            storyId = 10L,
            subscribe = false,
        )
        eventHandler.handle(
            Event(
                type = STORY_ATTACHMENT_DOWNLOADED_EVENT,
                payload = ObjectMapper().writeValueAsString(payload),
            ),
        )

        // THEN
        val events = eventStore.events(
            streamId = StreamId.STORY,
            type = STORY_ATTACHMENT_DOWNLOADED_EVENT,
            entityId = payload.storyId.toString(),
            userId = payload.userId?.toString(),
        )
        assertEquals(1, events.size)

        verify(eventStream, never()).enqueue(eq(SUBSCRIBE_COMMAND), any())
    }

    @Test
    fun downloadedNoUser() {
        // WHEN
        val payload = StoryAttachmentDownloadedEventPayload(
            userId = null,
            storyId = 10L,
            subscribe = false,
        )
        eventHandler.handle(
            Event(
                type = STORY_ATTACHMENT_DOWNLOADED_EVENT,
                payload = ObjectMapper().writeValueAsString(payload),
            ),
        )

        // THEN
        val events = eventStore.events(
            streamId = StreamId.STORY,
            type = STORY_ATTACHMENT_DOWNLOADED_EVENT,
            entityId = payload.storyId.toString(),
            userId = payload.userId?.toString(),
        )
        assertEquals(1, events.size)

        verify(eventStream, never()).enqueue(eq(SUBSCRIBE_COMMAND), any())
    }
}
