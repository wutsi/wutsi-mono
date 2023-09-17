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
import com.wutsi.platform.core.logging.DefaultKVLogger
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class UserRecommenderMLStrategyTest {
    private lateinit var subscriptionService: SubscriptionService
    private lateinit var similarityBackend: SimilarityBackend
    private lateinit var readerService: ReaderService
    private lateinit var storyService: StoryService
    private lateinit var strategy: UserRecommenderMLStrategy

    private val deviceId = "1203920934"

    @BeforeEach
    fun setUp() {
        subscriptionService = mock()
        similarityBackend = mock()
        readerService = mock()
        storyService = mock()
        strategy = UserRecommenderMLStrategy(
            subscriptionService,
            similarityBackend,
            readerService,
            storyService,
            DefaultKVLogger(),
        )
    }

    @Test
    fun `recommend from subscriptions`() {
        // GIVEN
        doReturn(
            listOf(
                SubscriptionEntity(userId = 1L, subscriberId = 100L),
                SubscriptionEntity(userId = 2L, subscriberId = 100L),
                SubscriptionEntity(userId = 3L, subscriberId = 100L),
                SubscriptionEntity(userId = 5L, subscriberId = 100L),
                SubscriptionEntity(userId = 6L, subscriberId = 100L),
            ),
        ).whenever(subscriptionService).search(any())

        // WHEN
        val result = strategy.recommend(
            RecommendUserRequest(
                readerId = 100L,
                deviceId = deviceId,
                limit = 3,
            ),
        )

        // THEN
        assertEquals(listOf(1L, 2L, 3L), result)

        verify(readerService, never()).findViewedStoryIds(anyOrNull(), any())
        verify(similarityBackend, never()).search(any())
    }

    @Test
    fun `recommend from subscriptions and readed`() {
        // GIVEN
        doReturn(
            listOf(
                SubscriptionEntity(userId = 1L, subscriberId = 100L),
            ),
        ).whenever(subscriptionService).search(any())

        doReturn(
            listOf(10L, 20L, 30L, 40L, 50L, 60L),
        ).whenever(readerService).findViewedStoryIds(anyOrNull(), any())

        doReturn(
            listOf(
                StoryEntity(id = 10L, userId = 1L),
                StoryEntity(id = 20L, userId = 2L),
                StoryEntity(id = 30L, userId = 2L),
                StoryEntity(id = 40L, userId = 2L),
                StoryEntity(id = 50L, userId = 3L),
                StoryEntity(id = 60L, userId = 4L),
            ),
        ).whenever(storyService).searchStories(any())

        // WHEN
        val result = strategy.recommend(
            RecommendUserRequest(
                readerId = 100L,
                deviceId = deviceId,
                limit = 3,
            ),
        )

        // THEN
        assertEquals(listOf(1L, 2L, 3L), result)

        verify(readerService).findViewedStoryIds(100L, deviceId)
        verify(similarityBackend, never()).search(any())
    }

    @Test
    fun `recommend from subscriptions and readed and similar`() {
        // GIVEN
        doReturn(
            listOf(
                SubscriptionEntity(userId = 1L, subscriberId = 100L),
            ),
        ).whenever(subscriptionService).search(any())

        doReturn(
            listOf(10L, 20L, 30L, 40L, 50L, 60L),
        ).whenever(readerService).findViewedStoryIds(anyOrNull(), any())

        doReturn(
            listOf(
                StoryEntity(id = 10L, userId = 1L),
                StoryEntity(id = 20L, userId = 2L),
                StoryEntity(id = 30L, userId = 2L),
                StoryEntity(id = 40L, userId = 2L),
            ),
        ).whenever(storyService).searchStories(any())

        doReturn(
            SearchSimilarityResponse(
                listOf(
                    Item(3L),
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
                deviceId = deviceId,
                limit = 3,
            ),
        )

        // THEN
        assertEquals(listOf(1L, 2L, 3L), result)

        verify(readerService).findViewedStoryIds(100L, deviceId)

        val req = argumentCaptor<SearchSimilarityRequest>()
        verify(similarityBackend).search(req.capture())
        assertEquals(listOf(1L, 2L), req.firstValue.itemIds)
        assertEquals(SimilarityModelType.AUTHOR_TIFDF, req.firstValue.model)
        assertEquals(3, req.firstValue.limit)
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
            ),
        ).whenever(storyService).searchStories(any())

        doReturn(
            SearchSimilarityResponse(
                listOf(
                    Item(3L),
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
                deviceId = deviceId,
                limit = 3,
            ),
        )

        // THEN
        assertEquals(listOf(1L, 2L, 3L), result)

        verify(readerService).findViewedStoryIds(null, deviceId)

        val req = argumentCaptor<SearchSimilarityRequest>()
        verify(similarityBackend).search(req.capture())
        assertEquals(listOf(1L, 2L), req.firstValue.itemIds)
        assertEquals(SimilarityModelType.AUTHOR_TIFDF, req.firstValue.model)
        assertEquals(3, req.firstValue.limit)
    }
}
