package com.wutsi.blog.story.it

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.story.dto.RecommendStoryRequest
import com.wutsi.blog.story.dto.RecommendStoryResponse
import com.wutsi.blog.story.service.StoryRecommendationService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class RecommendStoryQueryTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @MockBean
    private lateinit var service: StoryRecommendationService

    @Test
    fun recommend() {
        // GIVEN
        val result = listOf(11L, 13L, 14L)
        doReturn(result).whenever(service).recommend(any())

        // WHEN
        val request = RecommendStoryRequest(
            readerId = 1L,
            deviceId = "103920932",
            limit = 3,
        )
        val response = rest.postForEntity(
            "/v1/stories/queries/recommend",
            request,
            RecommendStoryResponse::class.java,
        )

        assertEquals(HttpStatus.OK, response.statusCode)

        val storyIds = response.body!!.storyIds
        assertEquals(listOf(11L, 13L, 14L), storyIds)

        verify(service).recommend(request)
    }
}
