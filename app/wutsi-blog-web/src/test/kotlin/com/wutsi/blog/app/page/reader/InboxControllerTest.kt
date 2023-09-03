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
import com.wutsi.blog.user.dto.SearchUserResponse
import com.wutsi.blog.user.dto.UserSummary
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class InboxControllerTest : SeleniumTestSupport() {
    private val blogs = listOf(
        UserSummary(
            id = 100,
            name = "pragmaticdev",
            fullName = "Pragmatic Dev",
            email = "pragmaticdev@gmail.com",
            pictureUrl = "https://picsum.photos/200/200",
            blog = true,
            biography = "This is an example of bio",
        ),
        UserSummary(
            id = 200,
            name = "roger.milla",
            fullName = "Roger Milla",
            pictureUrl = "https://picsum.photos/200/200",
            blog = true,
        ),
    )

    private val stories = listOf(
        StorySummary(
            id = 100,
            userId = 100L,
            title = "Story 1",
            thumbnailUrl = "https://picsum.photos/400/400",
            commentCount = 11,
            likeCount = 12,
            shareCount = 13,
            summary = "this is summary 100",
        ),
        StorySummary(
            id = 200,
            userId = 100L,
            title = "Story 2",
            thumbnailUrl = "https://picsum.photos/450/400",
            commentCount = 20,
            likeCount = 21,
            shareCount = 22,
            summary = "this is summary 200",
        ),
        StorySummary(
            id = 300,
            userId = 100L,
            title = "Story 3",
            thumbnailUrl = "https://picsum.photos/450/400",
            commentCount = 20,
            likeCount = 21,
            shareCount = 22,
            summary = "this is summary 300",
        ),
        StorySummary(
            id = 400,
            userId = 100L,
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

        doReturn(SearchUserResponse(blogs)).whenever(userBackend).search(any())
    }

    @Test
    fun noSubscription() {
        // GIVEN
        setupLoggedInUser(1)
        doReturn(
            RecommendStoryResponse(
                storyIds = listOf(100L, 200L),
            ),
        ).whenever(storyBackend).recommend(any())
        doReturn(
            SearchStoryResponse(
                stories = stories.take(2),
            ),
        ).whenever(storyBackend).search(any())

        // WHEN
        driver.get("$url/inbox")

        assertCurrentPageIs(PageName.INBOX)

        assertElementCount(".story-summary-card", 2)
    }

    @Test
    fun withSubscription() {
        // GIVEN
        setupLoggedInUser(1)
        doReturn(
            SearchSubscriptionResponse(
                subscriptions = listOf(
                    Subscription(
                        userId = 1L,
                        subscriberId = 200L,
                    ),
                ),
            ),
        ).whenever(subscriptionBackend).search(any())
        doReturn(SearchStoryResponse(stories)).whenever(storyBackend).search(any())

        // WHEN
        driver.get("$url/inbox")

        assertCurrentPageIs(PageName.INBOX)

        assertElementCount(".story-summary-card", stories.size)
    }

    @Test
    fun notLoggedIn() {
        // WHEN
        driver.get("$url/inbox")

        assertCurrentPageIs(PageName.LOGIN)
    }
}
