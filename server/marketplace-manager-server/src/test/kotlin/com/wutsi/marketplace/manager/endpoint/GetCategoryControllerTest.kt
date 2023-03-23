package com.wutsi.marketplace.manager.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.marketplace.access.dto.GetCategoryResponse
import com.wutsi.marketplace.manager.Fixtures
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GetCategoryControllerTest : AbstractSecuredControllerTest() {
    @LocalServerPort
    val port: Int = 0

    @Test
    fun invoke() {
        // GIVEN
        val category = Fixtures.createCategory(1)
        doReturn(GetCategoryResponse(category)).whenever(marketplaceAccessApi).getCategory(any())

        // WHEN
        val response = rest.getForEntity(url(category.id), GetCategoryResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val result = response.body!!.category
        assertEquals(category.id, result.id)
        assertEquals(category.title, result.title)
        assertEquals(category.longTitle, result.longTitle)
        assertEquals(category.level, result.level)
        assertEquals(category.parentId, result.parentId)
    }

    private fun url(id: Long) = "http://localhost:$port/v1/categories/$id"
}
