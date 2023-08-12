package com.wutsi.blog.story.it

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.backend.EmbeddingBackend
import com.wutsi.blog.story.dto.SearchSimilarStoryRequest
import com.wutsi.blog.story.dto.SearchSimilarStoryResponse
import com.wutsi.ml.embedding.dto.Story
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(value = ["/db/clean.sql", "/db/story/SearchSimilarStoryQuery.sql"])
class SearchSimilarStoryQueryTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @MockBean
    private lateinit var embeddingBackend: EmbeddingBackend

    @Test
    fun search() {
        // GIVEN
        val response = listOf(
            Story(11L, 0.2),
            Story(12L, 0.1),
            Story(13L, 0.01),
            Story(14L, 0.01),
        )
        doReturn(com.wutsi.ml.embedding.dto.SearchSimilarStoryResponse(response)).whenever(embeddingBackend)
            .search(any())

        // WHEN
        val request = SearchSimilarStoryRequest(
            storyIds = listOf(10L),
            limit = 3,
        )
        val result =
            rest.postForEntity("/v1/stories/queries/search-similar", request, SearchSimilarStoryResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val storyIds = result.body!!.storyIds
        assertEquals(listOf(11L, 12L, 13L), storyIds)

        val req = argumentCaptor<com.wutsi.ml.embedding.dto.SearchSimilarStoryRequest>()
        verify(embeddingBackend).search(req.capture())
        assertEquals(request.storyIds, req.firstValue.storyIds)
        assertEquals(5, req.firstValue.limit)
    }
}
