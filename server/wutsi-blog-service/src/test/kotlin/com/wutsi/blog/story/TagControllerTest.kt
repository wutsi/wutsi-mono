package com.wutsi.blog.story

import com.wutsi.blog.client.story.SearchTagResponse
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
@Sql(value = ["/db/clean.sql", "/db/TagController.sql"])
class TagControllerTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Test
    fun search() {
        val result = rest.getForEntity("/v1/tags?query=Gît", SearchTagResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val tags = result.body!!.tags
        assertEquals(4, tags.size)

        assertEquals(3, tags[0].id)
        assertEquals("git", tags[0].name)
        assertEquals("Git", tags[0].displayName)
        assertEquals(102L, tags[0].totalStories)

        assertEquals(4, tags[1].id)
        assertEquals("gitflow", tags[1].name)
        assertEquals("GitFlow", tags[1].displayName)
        assertEquals(7L, tags[1].totalStories)

        assertEquals(2, tags[2].id)
        assertEquals("github", tags[2].name)
        assertEquals("Github", tags[2].displayName)
        assertEquals(1L, tags[2].totalStories)

        assertEquals(8, tags[3].id)
        assertEquals("gites", tags[3].name)
        assertEquals("Gîtes", tags[3].displayName)
        assertEquals(0L, tags[3].totalStories)
    }
}
