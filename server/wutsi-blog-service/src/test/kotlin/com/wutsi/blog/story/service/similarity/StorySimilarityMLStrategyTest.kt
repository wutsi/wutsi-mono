package com.wutsi.blog.story.service.similarity

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.backend.SimilarityBackend
import com.wutsi.blog.story.dto.SearchSimilarStoryRequest
import com.wutsi.ml.similarity.dto.Item
import com.wutsi.ml.similarity.dto.SearchSimilarityRequest
import com.wutsi.ml.similarity.dto.SearchSimilarityResponse
import com.wutsi.ml.similarity.dto.SimilarityModelType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StorySimilarityMLStrategyTest {
    @MockBean
    private lateinit var similarityBackend: SimilarityBackend

    @Autowired
    private lateinit var strategy: StorySimilarityMLStrategy

    @Test
    fun search() {
        val response = listOf(
            Item(11L, 0.2),
            Item(12L, 0.1),
            Item(13L, 0.01),
            Item(14L, 0.01),
        )
        doReturn(SearchSimilarityResponse(response)).whenever(similarityBackend).search(any())

        // WHEN
        val request = SearchSimilarStoryRequest(
            storyIds = listOf(10L),
            limit = 3,
        )
        val storyIds = strategy.search(request)

        assertEquals(listOf(11L, 12L, 13L, 14L), storyIds)

        val req = argumentCaptor<SearchSimilarityRequest>()
        verify(similarityBackend).search(req.capture())
        assertEquals(request.storyIds, req.firstValue.itemIds)
        assertEquals(SimilarityModelType.STORY_TIFDF, req.firstValue.model)
        assertEquals(request.limit, req.firstValue.limit)
    }

    @Test
    fun `return empty on backend error`() {
        doThrow(RuntimeException::class).whenever(similarityBackend).search(any())

        // WHEN
        val request = SearchSimilarStoryRequest(
            storyIds = listOf(10L),
            limit = 3,
        )
        val storyIds = strategy.search(request)

        assertTrue(storyIds.isEmpty())
    }
}
