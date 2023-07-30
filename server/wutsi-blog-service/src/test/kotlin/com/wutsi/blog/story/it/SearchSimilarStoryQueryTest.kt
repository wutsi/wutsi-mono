package com.wutsi.blog.story.it

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.backend.SimilarityBackend
import com.wutsi.blog.similarity.dto.SearchSimilarityRequest
import com.wutsi.blog.similarity.dto.SearchSimilarityResponse
import com.wutsi.blog.similarity.dto.Similarity
import com.wutsi.blog.story.dto.SearchSimilarStoryRequest
import com.wutsi.blog.story.dto.SearchSimilarStoryResponse
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
    private lateinit var similarityBackend: SimilarityBackend

    @Test
    fun search() {
        // GIVEN
        val similarities = listOf(
            Similarity(11L, 0.2),
            Similarity(12L, 0.1),
            Similarity(13L, 0.1),
        )
        doReturn(SearchSimilarityResponse(similarities)).whenever(similarityBackend).search(any())

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

        val req = argumentCaptor<SearchSimilarityRequest>()
        verify(similarityBackend).search(req.capture())
        assertEquals(storyIds, req.firstValue.ids)
        assertEquals(listOf(10L, 11L, 12L, 13L, 14L), req.firstValue.similarIds)
    }
}
