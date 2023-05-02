package com.wutsi.blog.story.service.sort.algo

import com.wutsi.blog.Fixtures.createStory
import com.wutsi.blog.client.SortOrder
import com.wutsi.blog.client.story.SearchStoryRequest
import com.wutsi.blog.client.story.SortStoryRequest
import com.wutsi.blog.client.story.StorySortStrategy
import com.wutsi.blog.story.service.StoryService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class MostRecentSortAlgorithmTest {
    @Mock
    private lateinit var service: StoryService

    @InjectMocks
    private lateinit var algo: MostRecentSortAlgorithm

    @Test
    fun sort() {
        val storyIds = arrayListOf(4L, 3L, 2L, 1L)
        val stories = arrayListOf(
            createStory(1),
            createStory(2),
            createStory(3),
            createStory(4),
        )
        `when`(
            service.searchStories(
                SearchStoryRequest(
                    storyIds = storyIds,
                    sortBy = StorySortStrategy.published,
                    sortOrder = SortOrder.descending,
                    limit = 4,
                ),
            ),
        ).thenReturn(stories)

        val request = SortStoryRequest(storyIds = storyIds, deviceId = "1111")
        val response = algo.sort(request)

        assertEquals(4, response.size)
        assertEquals(1L, response[0])
        assertEquals(2L, response[1])
        assertEquals(3L, response[2])
        assertEquals(4L, response[3])
    }
}
