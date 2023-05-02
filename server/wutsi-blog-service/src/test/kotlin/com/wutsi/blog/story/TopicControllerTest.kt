package com.wutsi.blog.story

import com.wutsi.blog.client.story.SearchTopicResponse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class TopicControllerTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Test
    fun all() {
        val result = rest.getForEntity("/v1/topics", SearchTopicResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val topics = result.body!!.topics
        assertEquals(102, topics.size)
    }
}
