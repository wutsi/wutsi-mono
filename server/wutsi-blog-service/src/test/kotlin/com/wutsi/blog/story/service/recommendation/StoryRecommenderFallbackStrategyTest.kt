package com.wutsi.blog.story.service.recommendation

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.SortOrder
import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.story.dto.RecommendStoryRequest
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.dto.StorySortStrategy
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.blog.story.service.StoryService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StoryRecommenderFallbackStrategyTest {
    @MockBean
    private lateinit var storyService: StoryService

    @Autowired
    private lateinit var strategy: StoryRecommenderFallbackStrategy

    @Test
    fun recommend() {
        // GIVEN
        val stories = listOf(
            StoryEntity(id = 10L),
            StoryEntity(id = 21L),
        )
        doReturn(stories).whenever(storyService).searchStories(any())

        // WHEN
        val request = RecommendStoryRequest(
            readerId = 1L,
            deviceId = "r090e9re",
            limit = 20,
        )
        val response = strategy.recommend(request)

        val req = argumentCaptor<SearchStoryRequest>()
        verify(storyService).searchStories(req.capture())

        assertEquals(listOf(10L, 21L), response)

        assertEquals(StoryStatus.PUBLISHED, req.firstValue.status)
        assertEquals(StorySortStrategy.POPULARITY, req.firstValue.sortBy)
        assertEquals(SortOrder.DESCENDING, req.firstValue.sortOrder)
        assertEquals(true, req.firstValue.bubbleDownViewedStories)
        assertEquals(request.limit, req.firstValue.limit)
    }
}
