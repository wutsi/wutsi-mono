package com.wutsi.blog.story.it

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.blog.event.EventType
import com.wutsi.blog.story.dao.StoryRepository
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.blog.story.job.StoryPublisherJob
import com.wutsi.platform.core.stream.EventStream
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql
import java.util.Date
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(value = ["/db/clean.sql", "/db/story/PublishScheduledStoryJob.sql"])
class PublishScheduledStoryJobTest {
    @Autowired
    private lateinit var job: StoryPublisherJob

    @Autowired
    private lateinit var dao: StoryRepository

    @MockBean
    private lateinit var eventStream: EventStream

    @Test
    fun run() {
        val now = Date()
        Thread.sleep(1000)

        job.run()

        val story10 = dao.findById(10).get()
        assertEquals(StoryStatus.PUBLISHED, story10.status)
        assertEquals(true, story10.publishedDateTime?.before(now))

        val story20 = dao.findById(20).get()
        assertEquals(StoryStatus.PUBLISHED, story20.status)
        assertEquals(true, story20.publishedDateTime?.after(now))

        assertEquals(StoryStatus.DRAFT, dao.findById(30).get().status)

        assertEquals(StoryStatus.DRAFT, dao.findById(40).get().status)

        verify(eventStream).enqueue(eq(EventType.STORY_PUBLISHED_EVENT), any())
        verify(eventStream).publish(eq(EventType.STORY_PUBLISHED_EVENT), any())
    }
}
