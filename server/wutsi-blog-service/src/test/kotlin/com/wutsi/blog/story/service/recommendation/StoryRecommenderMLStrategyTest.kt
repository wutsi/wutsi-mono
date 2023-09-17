package com.wutsi.blog.story.service.recommendation

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.backend.PersonalizeBackend
import com.wutsi.blog.backend.SimilarityBackend
import com.wutsi.blog.story.dto.RecommendStoryRequest
import com.wutsi.blog.story.service.ReaderService
import com.wutsi.ml.personalize.dto.RecommendStoryResponse
import com.wutsi.ml.personalize.dto.Story
import com.wutsi.ml.similarity.dto.Item
import com.wutsi.ml.similarity.dto.SearchSimilarityRequest
import com.wutsi.ml.similarity.dto.SearchSimilarityResponse
import com.wutsi.ml.similarity.dto.SimilarityModelType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class StoryRecommenderMLStrategyTest {
    private lateinit var similarityBackend: SimilarityBackend
    private lateinit var personalizeBackend: PersonalizeBackend
    private lateinit var readerService: ReaderService
    private lateinit var strategy: StoryRecommenderMLStrategy

    @BeforeEach
    fun setUp() {
        similarityBackend = mock()
        personalizeBackend = mock()
        readerService = mock()
        strategy = StoryRecommenderMLStrategy(similarityBackend, personalizeBackend, readerService)
    }

    @Test
    fun `user recommendation`() {
        // GIVEN
        doReturn(
            RecommendStoryResponse(
                listOf(
                    Story(id = 10L),
                    Story(id = 21L),
                ),
            ),
        ).whenever(personalizeBackend).recommend(any())

        // WHEN
        val request = RecommendStoryRequest(
            readerId = 1L,
            deviceId = "r090e9re",
            limit = 20,
        )
        val response = strategy.recommend(request)

        val req = argumentCaptor<com.wutsi.ml.personalize.dto.RecommendStoryRequest>()
        verify(personalizeBackend).recommend(req.capture())

        assertEquals(listOf(10L, 21L), response)

        assertEquals(request.readerId, req.firstValue.userId)
        assertEquals(request.limit, req.firstValue.limit)
    }

    @Test
    fun `anonymous recommendation`() {
        // GIVEN
        doReturn(listOf(10L, 21L)).whenever(readerService).findViewedStoryIds(anyOrNull(), any())

        doReturn(
            SearchSimilarityResponse(
                listOf(
                    Item(11L),
                    Item(22L),
                    Item(33L),
                ),
            ),
        ).whenever(similarityBackend).search(any())

        // WHEN
        val request = RecommendStoryRequest(
            readerId = null,
            deviceId = "r090e9re",
            limit = 20,
        )
        val response = strategy.recommend(request)

        verify(readerService).findViewedStoryIds(null, request.deviceId)

        val req = argumentCaptor<SearchSimilarityRequest>()
        verify(similarityBackend).search(req.capture())

        assertEquals(listOf(11L, 22L, 33L), response)

        assertEquals(listOf(10L, 21L), req.firstValue.itemIds)
        assertEquals(SimilarityModelType.STORY_TIFDF, req.firstValue.model)
        assertEquals(request.limit, req.firstValue.limit)
    }

    @Test
    fun `return emptyList on backend error`() {
        // GIVEN
        doReturn(listOf(10L, 21L)).whenever(readerService).findViewedStoryIds(anyOrNull(), any())

        doThrow(RuntimeException::class).whenever(similarityBackend).search(any())

        // WHEN
        val request = RecommendStoryRequest(
            readerId = null,
            deviceId = "r090e9re",
            limit = 20,
        )
        val response = strategy.recommend(request)

        assertEquals(listOf(), response)
    }
}
