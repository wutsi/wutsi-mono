package com.wutsi.blog.story.service

import com.wutsi.blog.EventHandler
import com.wutsi.blog.client.story.StoryStatus
import com.wutsi.blog.story.dao.StoryRepository
import com.wutsi.blog.story.domain.Story
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(value = ["/db/clean.sql", "/db/StoryPublisherJob.sql"])
class StoryPublisherJobTest {
    @Autowired
    lateinit var events: EventHandler

    @Autowired
    private lateinit var job: StoryPublisherJob

    @Autowired
    private lateinit var dao: StoryRepository

    @BeforeEach
    fun setUp() {
        events.init()
    }

    @Test
    fun run() {
        job.run()

        assertEquals(StoryStatus.published, dao.findById(10).get().status)
        assertPublished(dao.findById(20).get())
        assertEquals(StoryStatus.draft, dao.findById(30).get().status)
        assertEquals(StoryStatus.draft, dao.findById(40).get().status)

        assertNotNull(events.publishEvent)
        assertEquals(20L, events.publishEvent?.storyId)
    }

    private fun assertPublished(story: Story) {
        assertEquals(StoryStatus.published, story.status)
        assertNotNull(story.publishedDateTime)
        assertTrue(story.live)
        assertNotNull(story.liveDateTime)
    }
}
