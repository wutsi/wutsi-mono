package com.wutsi.blog.product.service.filter

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.product.dto.SearchProductContext
import com.wutsi.blog.product.dto.SearchProductRequest
import com.wutsi.blog.story.dao.StoryRepository
import com.wutsi.blog.story.domain.StoryEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.Optional

class TaggedProductSearchFilterTest {
    private val storyDao = mock<StoryRepository>()
    private val filter = TaggedProductSearchFilter(storyDao)
    private val products = listOf(
        ProductEntity(id = 10, title = "Mon amants, mes enfants"),
        ProductEntity(id = 11, title = "Drools II"),
        ProductEntity(id = 12, title = "Les feux de l'amour - Episode 1"),
        ProductEntity(id = 13, title = "Les feux et l'amour - Prologue")
    )

    @Test
    fun `no story id`() {
        val request = SearchProductRequest(
            searchContext = SearchProductContext(storyId = null),
        )

        val response = filter.filter(request, products)
        assertEquals(products.map { it.id }, response.map { it.id })
    }

    @Test
    fun filter() {
        val request = SearchProductRequest(
            searchContext = SearchProductContext(storyId = 1),
        )

        val story = StoryEntity(id = 1, title = "Les feux de l'amour")
        doReturn(Optional.of(story)).whenever(storyDao).findById(any())

        val response = filter.filter(request, products)
        assertEquals(listOf(12L, 10L, 11L, 13L), response.map { it.id })
    }
}