package com.wutsi.blog.user.service.recommendation

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.backend.SimilarityBackend
import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.story.service.ReaderService
import com.wutsi.blog.story.service.StoryService
import com.wutsi.blog.subscription.domain.SubscriptionEntity
import com.wutsi.blog.subscription.service.SubscriptionService
import com.wutsi.blog.user.dto.RecommendUserRequest
import com.wutsi.ml.similarity.dto.Item
import com.wutsi.ml.similarity.dto.SearchSimilarityRequest
import com.wutsi.ml.similarity.dto.SearchSimilarityResponse
import com.wutsi.ml.similarity.dto.SimilarityModelType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UserRecommenderMLStrategyTest {
    private lateinit var subscriptionService: SubscriptionService
    private lateinit var similarityBackend: SimilarityBackend
    private lateinit var readerService: ReaderService
    private lateinit var storyService: StoryService
    private lateinit var strategy: UserRecommenderMLStrategy

    @BeforeEach
    fun setUp() {
        subscriptionService = mock()
        similarityBackend = mock()
        readerService = mock()
        storyService = mock()
        strategy = UserRecommenderMLStrategy(subscriptionService, similarityBackend, readerService, storyService)
    }

    @Test
    fun `recommend to user with subscriptions`() {
        // GIVEN
        doReturn(
            listOf(
                SubscriptionEntity(userId = 1L, subscriberId = 100L),
                SubscriptionEntity(userId = 2L, subscriberId = 100L),
                SubscriptionEntity(userId = 3L, subscriberId = 100L),
            ),
        ).whenever(subscriptionService).search(any())

        doReturn(
            SearchSimilarityResponse(
                listOf(
                    Item(10L),
                    Item(20L),
                    Item(30L),
                    Item(40L),
                ),
            ),
        ).whenever(similarityBackend).search(any())

        // WHEN
        val result = strategy.recommend(
            RecommendUserRequest(
                readerId = 100L,
                deviceId = "1203920934",
                limit = 10,
            ),
        )

        // THEN
        assertEquals(listOf(10L, 20L, 30L, 40L), result)

        val req = argumentCaptor<SearchSimilarityRequest>()
        verify(similarityBackend).search(req.capture())
        assertEquals(listOf(1L, 2L, 3L), req.firstValue.itemIds)
        assertEquals(SimilarityModelType.AUTHOR_TIFDF, req.firstValue.model)
        assertEquals(10, req.firstValue.limit)
    }

    @Test
    fun `recommend to user without subscriptions`() {
        // GIVEN
        doReturn(listOf<Long>()).whenever(subscriptionService).search(any())

        doReturn(
            listOf(11L, 21L, 22L),
        ).whenever(readerService).findViewedStoryIds(anyOrNull(), any())

        doReturn(
            listOf(
                StoryEntity(id = 11L, userId = 1L),
                StoryEntity(id = 21L, userId = 2L),
                StoryEntity(id = 22L, userId = 3L),
            ),
        ).whenever(storyService).searchStories(any())

        doReturn(
            SearchSimilarityResponse(
                listOf(
                    Item(10L),
                    Item(20L),
                    Item(30L),
                    Item(40L),
                ),
            ),
        ).whenever(similarityBackend).search(any())

        // WHEN
        val result = strategy.recommend(
            RecommendUserRequest(
                readerId = 100L,
                deviceId = "1203920934",
                limit = 10,
            ),
        )

        // THEN
        assertEquals(listOf(10L, 20L, 30L, 40L), result)

        verify(readerService).findViewedStoryIds(100L, "1203920934")

        val req = argumentCaptor<SearchSimilarityRequest>()
        verify(similarityBackend).search(req.capture())
        assertEquals(listOf(1L, 2L, 3L), req.firstValue.itemIds)
        assertEquals(SimilarityModelType.AUTHOR_TIFDF, req.firstValue.model)
        assertEquals(10, req.firstValue.limit)
    }

    @Test
    fun `recommend to anonymous`() {
        // GIVEN
        doReturn(
            listOf(11L, 21L, 22L),
        ).whenever(readerService).findViewedStoryIds(anyOrNull(), any())

        doReturn(
            listOf(
                StoryEntity(id = 11L, userId = 1L),
                StoryEntity(id = 21L, userId = 2L),
                StoryEntity(id = 22L, userId = 3L),
            ),
        ).whenever(storyService).searchStories(any())

        doReturn(
            SearchSimilarityResponse(
                listOf(
                    Item(10L),
                    Item(20L),
                    Item(30L),
                    Item(40L),
                ),
            ),
        ).whenever(similarityBackend).search(any())

        // WHEN
        val result = strategy.recommend(
            RecommendUserRequest(
                readerId = null,
                deviceId = "1203920934",
                limit = 10,
            ),
        )

        // THEN
        assertEquals(listOf(10L, 20L, 30L, 40L), result)

        verify(readerService).findViewedStoryIds(null, "1203920934")

        val req = argumentCaptor<SearchSimilarityRequest>()
        verify(similarityBackend).search(req.capture())
        assertEquals(listOf(1L, 2L, 3L), req.firstValue.itemIds)
        assertEquals(SimilarityModelType.AUTHOR_TIFDF, req.firstValue.model)
        assertEquals(10, req.firstValue.limit)
    }

    @Test
    fun `recommend to first time user`() {
        // GIVEN
        doReturn(listOf<Long>()).whenever(subscriptionService).search(any())

        doReturn(listOf<Long>()).whenever(readerService).findViewedStoryIds(anyOrNull(), any())

        // WHEN
        val result = strategy.recommend(
            RecommendUserRequest(
                readerId = 100L,
                deviceId = "1203920934",
                limit = 10,
            ),
        )

        // THEN
        assertTrue(result.isEmpty())

        verify(similarityBackend, never()).search(any())
    }
}
