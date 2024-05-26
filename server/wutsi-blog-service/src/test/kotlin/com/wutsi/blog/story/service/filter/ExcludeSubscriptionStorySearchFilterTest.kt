package com.wutsi.blog.story.service.filter

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.story.dto.SearchStoryContext
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.subscription.dao.SubscriptionRepository
import com.wutsi.blog.subscription.domain.SubscriptionEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ExcludeSubscriptionStorySearchFilterTest {
    private val dao = mock<SubscriptionRepository> { }

    private val filter = ExcludeSubscriptionStorySearchFilter(dao)
    private val stories = listOf(
        StoryEntity(id = 10L, userId = 1),
        StoryEntity(id = 11L, userId = 1),
        StoryEntity(id = 20L, userId = 2),
        StoryEntity(id = 31L, userId = 3),
        StoryEntity(id = 30L, userId = 3),
        StoryEntity(id = 32L, userId = 3),
    )

    @Test
    fun filter() {
        doReturn(
            listOf(
                SubscriptionEntity(userId = 1L, subscriberId = 100L),
                SubscriptionEntity(userId = 3L, subscriberId = 100L)
            )
        ).whenever(dao).findBySubscriberId(100)

        val result = filter.filter(
            SearchStoryRequest(
                excludeStoriesFromSubscriptions = true,
                searchContext = SearchStoryContext(userId = 100)
            ),
            stories
        )

        assertEquals(1, result.size)
        assertEquals(listOf(20L), result.map { it.id })
    }

    @Test
    fun noSubscritions() {
        doReturn(emptyList<SubscriptionEntity>()).whenever(dao).findBySubscriberId(100)

        val result = filter.filter(
            SearchStoryRequest(
                excludeStoriesFromSubscriptions = true,
                searchContext = SearchStoryContext(userId = 100)
            ),
            stories
        )

        assertEquals(stories.size, result.size)
        assertEquals(stories.map { it.id }, result.map { it.id })
    }

    @Test
    fun anonymous() {
        val result = filter.filter(
            SearchStoryRequest(
                excludeStoriesFromSubscriptions = true,
                searchContext = SearchStoryContext(userId = null)
            ),
            stories
        )

        assertEquals(stories.size, result.size)
        assertEquals(stories.map { it.id }, result.map { it.id })
    }

    @Test
    fun includeStoriesFromSubscription() {
        val result = filter.filter(
            SearchStoryRequest(
                excludeStoriesFromSubscriptions = true,
                searchContext = SearchStoryContext(userId = 100)
            ),
            stories
        )

        assertEquals(stories.size, result.size)
        assertEquals(stories.map { it.id }, result.map { it.id })
    }
}