package com.wutsi.membership.manager.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.membership.access.dto.SearchCategoryResponse
import com.wutsi.membership.manager.Fixtures
import com.wutsi.membership.manager.dto.SearchCategoryRequest
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
    public fun invoke() {
        // GIVEN
        val category1 = Fixtures.createCategorySummary()
        val category2 = Fixtures.createCategorySummary()
        doReturn(SearchCategoryResponse(listOf(category1, category2))).whenever(membershipAccess).searchCategory(any())

        // WHEN
        val request = SearchCategoryRequest(
            keyword = "332",
            categoryIds = listOf(11L, 33L),
            limit = 3,
            offset = 100,
        )
        val response =
            rest.postForEntity(url(), request, com.wutsi.membership.manager.dto.SearchCategoryResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        verify(membershipAccess).searchCategory(
            request = com.wutsi.membership.access.dto.SearchCategoryRequest(
                keyword = request.keyword,
                categoryIds = request.categoryIds,
                limit = request.limit,
                offset = request.offset,
            ),
        )

        val categories = response.body!!.categories
        assertEquals(2, categories.size)
    }

    private fun url() = "http://localhost:$port/v1/categories/search"
}
