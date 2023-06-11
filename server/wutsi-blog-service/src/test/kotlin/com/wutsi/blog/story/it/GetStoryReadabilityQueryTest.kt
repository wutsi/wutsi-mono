package com.wutsi.blog.story.it

import com.wutsi.blog.EventHandler
import com.wutsi.blog.ResourceHelper
import com.wutsi.blog.story.dto.CreateStoryCommand
import com.wutsi.blog.story.dto.CreateStoryResponse
import com.wutsi.blog.subscription.dto.GetStoryReadabilityResponse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(value = ["/db/clean.sql", "/db/story/GetStoryReadabilityQuery.sql"])
class GetStoryReadabilityQueryTest {
    @Autowired
    lateinit var events: EventHandler

    @Autowired
    private lateinit var rest: TestRestTemplate

    @Test
    fun readability() {
        val request = CreateStoryCommand(
            title = "Hello world",
            content = ResourceHelper.loadResourceAsString("/editorjs.json"),
            userId = 1L,
        )
        val storyId =
            rest.postForEntity("/v1/stories/commands/create", request, CreateStoryResponse::class.java).body!!.storyId

        val result = rest.getForEntity("/v1/stories/$storyId/readability", GetStoryReadabilityResponse::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)
        val readability = result.body!!.readability
        assertEquals(71, readability.score)
        assertEquals(50, readability.scoreThreshold)
        assertTrue(readability.rules.isNotEmpty())
    }
}
