package com.wutsi.blog.story.service.sort.algo

import com.wutsi.blog.client.story.SortStoryRequest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class NoSortAlgorithmTest {
    val algo = NoSortAlgorithm()

    @Test
    fun sort() {
        val storyIds = listOf(1L, 3L, 2L)

        val result = algo.sort(SortStoryRequest(storyIds = storyIds))

        assertEquals(storyIds, result)
    }
}
