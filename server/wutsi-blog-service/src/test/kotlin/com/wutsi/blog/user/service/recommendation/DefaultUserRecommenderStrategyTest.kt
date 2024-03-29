package com.wutsi.blog.user.service.recommendation

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.like.domain.LikeEntity
import com.wutsi.blog.like.service.LikeService
import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.story.service.ReaderService
import com.wutsi.blog.story.service.StoryService
import com.wutsi.blog.subscription.domain.SubscriptionEntity
import com.wutsi.blog.subscription.dto.SearchSubscriptionRequest
import com.wutsi.blog.subscription.service.SubscriptionService
import com.wutsi.blog.user.dto.RecommendUserRequest
import com.wutsi.platform.core.logging.DefaultKVLogger
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DefaultUserRecommenderStrategyTest {
    private lateinit var subscriptionService: SubscriptionService
    private lateinit var likeService: LikeService
    private lateinit var readerService: ReaderService
    private lateinit var storyService: StoryService
    private lateinit var strategy: DefaultUserRecommenderStrategy

    private val userId = 100L
    private val deviceId = "1203920934"

    @BeforeEach
    fun setUp() {
        subscriptionService = mock()
        likeService = mock()
        readerService = mock()
        storyService = mock()
        strategy = DefaultUserRecommenderStrategy(
            subscriptionService,
            likeService,
            readerService,
            storyService,
            DefaultKVLogger(),
        )
    }

    @Test
    fun `recommend for user`() {
        // GIVEN
        doReturn(
            // user liked stories {10} -> blogs {1}
            listOf(
                LikeEntity(storyId = 10),
            ),
        ).whenever(likeService).findByUserIdOrDeviceId(userId, deviceId, DefaultUserRecommenderStrategy.LIMIT)

        doReturn(
            // user views stories {10,21,22,40} -> blogs {1,2,4}
            listOf(10L, 21L, 22L, 30L, 40L),
        ).whenever(readerService).findViewedStoryIds(userId, deviceId)

        doReturn(
            // user subscribed to blog#{4,5,6}
            listOf(
                SubscriptionEntity(userId = 4L, subscriberId = userId),
                SubscriptionEntity(userId = 5L, subscriberId = userId),
                SubscriptionEntity(userId = 6L, subscriberId = userId),
            ),
        ).whenever(subscriptionService)
            .search(
                SearchSubscriptionRequest(subscriberId = userId, limit = DefaultUserRecommenderStrategy.LIMIT),
            )

        doReturn(
            listOf(
                StoryEntity(id = 10L, userId = 1L),
                StoryEntity(id = 20L, userId = 2L),
                StoryEntity(id = 21L, userId = 2L),
                StoryEntity(id = 30L, userId = 3L),
                StoryEntity(id = 40L, userId = 4L),
            ),
        ).whenever(storyService).searchStories(any())

        // WHEN
        val result = strategy.recommend(
            RecommendUserRequest(
                readerId = userId,
                deviceId = deviceId,
                limit = 3,
            ),
        )

        // THEN
        assertEquals(listOf(1L, 2L, 3L), result)
    }

    @Test
    fun `recommend for anonymous`() {
        // GIVEN
        doReturn(
            // user liked stories {10} -> blogs {1}
            listOf(
                LikeEntity(storyId = 10),
            ),
        ).whenever(likeService).findByUserIdOrDeviceId(null, deviceId, 100)

        doReturn(
            // user views stories {10,21,22} -> blogs {1,2}
            listOf(10L, 21L, 22L, 30L),
        ).whenever(readerService).findViewedStoryIds(null, deviceId)

        doReturn(
            listOf(
                StoryEntity(id = 10L, userId = 1L),
                StoryEntity(id = 20L, userId = 2L),
                StoryEntity(id = 21L, userId = 2L),
                StoryEntity(id = 30L, userId = 3L),
            ),
        ).whenever(storyService).searchStories(any())

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
    }

    @Test
    fun `recommend for first time user`() {
        // GIVEN
        doReturn(listOf<Long>()).whenever(likeService).findByUserIdOrDeviceId(null, deviceId, 100)

        doReturn(listOf<Long>()).whenever(readerService).findViewedStoryIds(null, deviceId)

        // WHEN
        val result = strategy.recommend(
            RecommendUserRequest(
                readerId = null,
                deviceId = deviceId,
                limit = 3,
            ),
        )

        // THEN
        assertTrue(result.isEmpty())
    }
}
