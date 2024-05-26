package com.wutsi.blog.story.service.filter

import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.security.service.SecurityManager
import com.wutsi.blog.story.dao.ViewRepository
import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.story.dto.SearchStoryContext
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.platform.core.tracing.TracingContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock

class BubbleDownViewedStorySearchFilterTest {
    private val viewDao: ViewRepository = mock<ViewRepository>()
    private val securityManager: SecurityManager = mock<SecurityManager>()
    private val tracingContext: TracingContext = mock<TracingContext>()

    private val filter = BubbleDownViewedStorySearchFilter(viewDao, securityManager, tracingContext)
    private val stories = listOf(
        StoryEntity(id = 10L, userId = 1),
        StoryEntity(id = 11L, userId = 1),
        StoryEntity(id = 20L, userId = 2),
        StoryEntity(id = 31L, userId = 3),
        StoryEntity(id = 30L, userId = 3),
        StoryEntity(id = 32L, userId = 3),
    )

    @Test
    fun bubbleDown() {
        doReturn("xxx").whenever(tracingContext).deviceId()
        doReturn(1L).whenever(securityManager).getCurrentUserId()
        doReturn(
            listOf(11L, 31L)
        ).whenever(viewDao).findStoryIdsByUserIdOrDeviceId(anyOrNull(), anyOrNull())

        val result = filter.filter(SearchStoryRequest(bubbleDownViewedStories = true), stories)

        assertEquals(stories.size, result.size)
        assertEquals(listOf(10L, 20L, 30L, 32L, 11L, 31L), result.map { it.id })
    }

    @Test
    fun bubbleDownFromSearchContext() {
        doReturn("xxx").whenever(tracingContext).deviceId()
        doReturn(null).whenever(securityManager).getCurrentUserId()
        doReturn(
            listOf(11L, 31L)
        ).whenever(viewDao).findStoryIdsByUserIdOrDeviceId(anyOrNull(), anyOrNull())

        val result = filter.filter(
            SearchStoryRequest(
                bubbleDownViewedStories = true,
                searchContext = SearchStoryContext(userId = 1L)
            ),
            stories
        )

        assertEquals(stories.size, result.size)
        assertEquals(listOf(10L, 20L, 30L, 32L, 11L, 31L), result.map { it.id })
    }

    @Test
    fun noBubbleDown() {
        val result = filter.filter(SearchStoryRequest(bubbleDownViewedStories = false), stories)

        assertEquals(stories.size, result.size)
        assertEquals(stories.map { it.id }, result.map { it.id })
    }
}