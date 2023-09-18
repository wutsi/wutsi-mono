package com.wutsi.blog.app.page.reader

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.story.dto.RecommendStoryResponse
import com.wutsi.blog.story.dto.SearchStoryResponse
import com.wutsi.blog.story.dto.StorySummary
import com.wutsi.blog.subscription.dto.SearchSubscriptionResponse
import com.wutsi.blog.subscription.dto.Subscription
import com.wutsi.blog.user.dto.RecommendUserResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class HomeControllerTest : SeleniumTestSupport() {
    private val stories = listOf(
        StorySummary(
            id = 100,
            userId = 1L,
            title = "Story 1",
            thumbnailUrl = "https://picsum.photos/400/400",
            commentCount = 11,
            likeCount = 12,
            shareCount = 13,
            summary = "this is summary 100",
        ),
        StorySummary(
            id = 200,
            userId = 2L,
            title = "Story 2",
            thumbnailUrl = "https://picsum.photos/450/400",
            commentCount = 20,
            likeCount = 21,
            shareCount = 22,
            summary = "this is summary 200",
        ),
        StorySummary(
            id = 300,
            userId = 3L,
            title = "Story 3",
            thumbnailUrl = "https://picsum.photos/450/400",
            commentCount = 20,
            likeCount = 21,
            shareCount = 22,
            summary = "this is summary 300",
        ),
        StorySummary(
            id = 400,
            userId = 4L,
            title = "Story 4",
            thumbnailUrl = "https://picsum.photos/450/400",
            commentCount = 20,
            likeCount = 21,
            shareCount = 22,
            summary = "this is summary 400",
        ),
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(RecommendUserResponse(listOf(1L, 2L, 3L))).whenever(userBackend).recommend(any())
        doReturn(RecommendStoryResponse(listOf(1L, 2L, 3L))).whenever(storyBackend).recommend(any())
        doReturn(SearchStoryResponse(stories)).whenever(storyBackend).search(any())
    }

    @Test
    fun anonymous() {
        // WHEN
        driver.get(url)
        assertCurrentPageIs(PageName.HOME)

        // THEN
        assertElementCount(".story-summary-card", 4)

        assertElementAttributeEndsWith("#btn-create-hero", "href", "/create")
        assertElementAttributeEndsWith("#btn-create-bottom", "href", "/create")
    }

    @Test
    fun authenticated() {
        // GIVEN
        setupLoggedInUser(100, blog = true)

        doReturn(
            SearchSubscriptionResponse(
                subscriptions = listOf(
                    Subscription(10, 100),
                    Subscription(20, 100),
                ),
            ),
        ).whenever(subscriptionBackend).search(any())

        // WHEN
        driver.get(url)
        assertCurrentPageIs(PageName.HOME)

        // THEN
        assertElementCount(".story-summary-card", 4)

        assertElementAttributeEndsWith("#btn-create-hero", "href", "/create")
        assertElementAttributeEndsWith("#btn-create-bottom", "href", "/create")
    }
}
