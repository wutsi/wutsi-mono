package com.wutsi.marketplace.manager.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.marketplace.manager.Fixtures
import com.wutsi.marketplace.manager.dto.SearchCategoryRequest
import com.wutsi.marketplace.manager.dto.SearchCategoryResponse
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SearchCategoryControllerTest : AbstractSecuredControllerTest() {
    @LocalServerPort
    public val port: Int = 0

    @Test
    public fun search() {
        val categories = listOf(
            Fixtures.createCategorySummary(100),
            Fixtures.createCategorySummary(200),
            Fixtures.createCategorySummary(300),
        )
        doReturn(com.wutsi.marketplace.access.dto.SearchCategoryResponse(categories)).whenever(marketplaceAccessApi)
            .searchCategory(any())

        // WHEN
        val request = SearchCategoryRequest(
            limit = 300,
            offset = 1,
            level = 3,
            categoryIds = listOf(1L, 2L),
            keyword = "Yom",
            parentId = 8777L,
        )
        val response = rest.postForEntity(url(), request, SearchCategoryResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val result = response.body!!.categories
        assertEquals(categories.size, result.size)

        verify(marketplaceAccessApi).searchCategory(
            com.wutsi.marketplace.access.dto.SearchCategoryRequest(
                categoryIds = request.categoryIds,
                level = request.level,
                keyword = request.keyword,
                limit = request.limit,
                offset = request.offset,
                parentId = request.parentId,
            ),
        )
    }

    private fun url() = "http://localhost:$port/v1/categories/search"
}
