package com.wutsi.blog.story.service.similarity

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.SortOrder
import com.wutsi.blog.story.dao.StoryRepository
import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.story.dto.SearchSimilarStoryRequest
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.dto.StorySortStrategy
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.blog.story.service.StoryService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StorySimilarityFallbackStrategyTest {
    @MockBean
    private lateinit var storyDao: StoryRepository

    @MockBean
    private lateinit var storyService: StoryService

    @Autowired
    private lateinit var strategy: StorySimilarityFallbackStrategy

    @Test
    fun search() {
        // GIVEN
        doReturn(
            listOf(
                StoryEntity(id = 11L, userId = 6L),
                StoryEntity(id = 21L, userId = 10L),
            ),
        ).whenever(storyDao).findAllById(any())

        val stories = listOf(
            StoryEntity(id = 10L),
            StoryEntity(id = 11L),
            StoryEntity(id = 21L),
            StoryEntity(id = 31L),
        )
        doReturn(stories).whenever(storyService).searchStories(any())

        // WHEN
        val request = SearchSimilarStoryRequest(
            storyIds = listOf(10L, 55L),
            limit = 3,
        )
        val storyIds = strategy.search(request)

        assertEquals(listOf(11L, 21L, 31L), storyIds)

        val req = argumentCaptor<SearchStoryRequest>()
        verify(storyService).searchStories(req.capture())
        assertEquals(listOf(6L, 10L), req.firstValue.userIds)
        kotlin.test.assertEquals(StoryStatus.PUBLISHED, req.firstValue.status)
        kotlin.test.assertEquals(StorySortStrategy.PUBLISHED, req.firstValue.sortBy)
        kotlin.test.assertEquals(SortOrder.DESCENDING, req.firstValue.sortOrder)
        kotlin.test.assertEquals(true, req.firstValue.bubbleDownViewedStories)
        kotlin.test.assertEquals(request.limit + request.storyIds.size, req.firstValue.limit)
    }
}
