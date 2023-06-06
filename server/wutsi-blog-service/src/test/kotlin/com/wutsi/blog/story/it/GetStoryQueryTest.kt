package com.wutsi.blog.story.it

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.EventHandler
import com.wutsi.blog.story.dto.GetStoryResponse
import com.wutsi.blog.story.dto.StoryAccess
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.platform.core.tracing.TracingContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import java.util.TimeZone
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(value = ["/db/clean.sql", "/db/GetStoryController.sql"])
class GetControllerTest {
    @Autowired
    lateinit var events: EventHandler

    @Autowired
    private lateinit var rest: TestRestTemplate

    private lateinit var fmt: SimpleDateFormat

    @MockBean
    private lateinit var traceContext: TracingContext

    @BeforeEach
    fun setUp() {
        events.init()

        fmt = SimpleDateFormat("yyyy-MM-dd")
        fmt.timeZone = TimeZone.getTimeZone("UTC")

        doReturn("the-device-id").whenever(traceContext).deviceId()
    }

    @Test
    fun anonymous() {
        val result = rest.getForEntity("/v1/stories/10", GetStoryResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)
        val story = result.body!!.story
        assertEquals("Sample Story", story.title)
        assertEquals("Sample Tagline", story.tagline)
        assertEquals("text/plain", story.contentType)
        assertEquals("World", story.content)
        assertEquals("en", story.language)
        assertEquals("https://www.img.com/goo.png", story.thumbnailUrl)
        assertEquals("https://www.test.com/1/1/test.txt", story.sourceUrl)
        assertEquals(7, story.readingMinutes)
        assertEquals(1430, story.wordCount)
        assertEquals("/read/2/sample-story", story.slug)
        assertEquals(2, story.readabilityScore)
        assertEquals(StoryStatus.PUBLISHED, story.status)

        assertEquals(2, story.tags.size)

        assertEquals(1, story.tags[0].id)
        assertEquals("covid-19", story.tags[0].name)
        assertEquals("COVID-19", story.tags[0].displayName)
        assertEquals(100L, story.tags[0].totalStories)

        assertEquals(4, story.tags[1].id)
        assertEquals("gitflow", story.tags[1].name)
        assertEquals("GitFlow", story.tags[1].displayName)
        assertEquals(7L, story.tags[1].totalStories)

        assertEquals(101L, story.topic?.id)
        assertEquals("art", story.topic?.name)

        assertEquals("2040-01-02", fmt.format(story.scheduledPublishDateTime))
        assertEquals(StoryAccess.PUBLIC, story.access)
    }
}
