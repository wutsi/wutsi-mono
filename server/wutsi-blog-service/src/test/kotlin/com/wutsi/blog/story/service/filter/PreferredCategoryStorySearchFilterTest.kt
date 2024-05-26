package com.wutsi.blog.story.service.filter

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.story.dao.PreferredCategoryRepository
import com.wutsi.blog.story.domain.PreferredCategoryEntity
import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.story.dto.SearchStoryContext
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.dto.StorySortStrategy
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import kotlin.test.assertEquals

class PreferredCategoryStorySearchFilterTest {
    private val dao: PreferredCategoryRepository = Mockito.mock<PreferredCategoryRepository>()

    private val filter = PreferredCategoryStorySearchFilter(dao)
    private val stories = listOf(
        StoryEntity(id = 10L, categoryId = 1),
        StoryEntity(id = 11L, categoryId = 1),
        StoryEntity(id = 20L, categoryId = 2),
        StoryEntity(id = 31L, categoryId = 3),
        StoryEntity(id = 30L, categoryId = 3),
        StoryEntity(id = 32L, categoryId = 3),
        StoryEntity(id = 50L, categoryId = 5L),
    )

    @Test
    fun filter() {
        doReturn(
            listOf(
                PreferredCategoryEntity(categoryId = 5L),
                PreferredCategoryEntity(categoryId = 3L),
            )
        ).whenever(dao).findByUserIdOrderByTotalReadsDesc(any())

        val result = filter.filter(
            SearchStoryRequest(
                sortBy = StorySortStrategy.RECOMMENDED,
                searchContext = SearchStoryContext(userId = 1L)
            ),
            stories
        )

        assertEquals(stories.size, result.size)
        assertEquals(listOf(50L, 31L, 30L, 32L, 10L, 11L, 20L), result.map { it.id })
    }

    @Test
    fun notRecommendedSearch() {
        val result = filter.filter(
            SearchStoryRequest(
                sortBy = StorySortStrategy.POPULARITY,
                searchContext = SearchStoryContext(userId = 1L)
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
                sortBy = StorySortStrategy.RECOMMENDED,
                searchContext = SearchStoryContext(userId = null)
            ),
            stories
        )

        assertEquals(stories.size, result.size)
        assertEquals(stories.map { it.id }, result.map { it.id })
    }
}