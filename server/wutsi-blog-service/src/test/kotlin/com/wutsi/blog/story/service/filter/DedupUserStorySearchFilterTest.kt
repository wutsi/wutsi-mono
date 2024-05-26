package com.wutsi.blog.story.service.filter

import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.story.dto.SearchStoryRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DedupUserStorySearchFilterTest {
    private val filter = DedupUserStorySearchFilter()
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
        val result = filter.filter(SearchStoryRequest(dedupUser = true), stories)

        assertEquals(3, result.size)
        assertEquals(listOf(10L, 20L, 31), result.map { it.id })
    }

    @Test
    fun noDedup() {
        val result = filter.filter(SearchStoryRequest(dedupUser = false), stories)

        assertEquals(stories.size, result.size)
        assertEquals(stories.map { it.id }, result.map { it.id })
    }
}