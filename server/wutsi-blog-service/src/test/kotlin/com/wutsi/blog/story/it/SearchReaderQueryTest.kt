package com.wutsi.blog.story.it

import com.wutsi.blog.story.dto.SearchReaderRequest
import com.wutsi.blog.story.dto.SearchReaderResponse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(value = ["/db/clean.sql", "/db/story/SearchReaderQuery.sql"])
class SearchReaderQueryTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Test
    fun bySubscribers() {
        val request = SearchReaderRequest(
            subscribedToUserId = 100L,
        )
        val result = rest.postForEntity("/v1/readers/queries/search", request, SearchReaderResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val readers = result.body!!.readers
        assertEquals(2, readers.size)

        assertEquals(3001L, readers[0].id)
        assertEquals(12L, readers[0].storyId)
        assertEquals(300, readers[0].userId)
        assertEquals(false, readers[0].commented)
        assertEquals(false, readers[0].liked)
        assertEquals(false, readers[0].subscribed)

        assertEquals(2001, readers[1].id)
        assertEquals(11L, readers[1].storyId)
        assertEquals(200, readers[1].userId)
        assertEquals(false, readers[1].commented)
        assertEquals(false, readers[1].liked)
        assertEquals(true, readers[1].subscribed)
    }

    @Test
    fun byStory() {
        val request = SearchReaderRequest(
            storyId = 10L,
        )
        val result = rest.postForEntity("/v1/readers/queries/search", request, SearchReaderResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val readers = result.body!!.readers
        assertEquals(1, readers.size)

        assertEquals(1001L, readers[0].id)
        assertEquals(10L, readers[0].storyId)
        assertEquals(100, readers[0].userId)
        assertEquals(true, readers[0].commented)
        assertEquals(true, readers[0].liked)
        assertEquals(true, readers[0].subscribed)
    }
}
