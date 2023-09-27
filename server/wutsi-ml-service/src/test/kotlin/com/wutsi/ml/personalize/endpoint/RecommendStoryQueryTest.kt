package com.wutsi.ml.personalize.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.ml.personalize.dto.RecommendStoryRequest
import com.wutsi.ml.personalize.dto.RecommendStoryResponse
import com.wutsi.ml.personalize.service.PersonalizeV1Service
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RecommendStoryQueryTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @MockBean
    private lateinit var service: PersonalizeV1Service

    @Test
    fun sort() {
        // GIVEN
        val result = listOf(
            Pair(3L, 0.9),
            Pair(2L, 0.8),
            Pair(1L, 0.7),
        )
        doReturn(result).whenever(service).recommend(any())

        // WHEN
        val request = RecommendStoryRequest(
            userId = 1L,
            limit = 10,
        )
        val response = rest.postForEntity(
            "/v1/personalize/queries/recommend",
            request,
            RecommendStoryResponse::class.java,
        )

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        verify(service).recommend(request)

        val stories = response.body!!.stories
        assertEquals(result[0].first, stories[0].id)
        assertEquals(result[1].first, stories[1].id)
        assertEquals(result[2].first, stories[2].id)
    }
}
