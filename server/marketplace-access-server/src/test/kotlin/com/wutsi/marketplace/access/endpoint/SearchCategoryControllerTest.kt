package com.wutsi.marketplace.access.endpoint

import com.wutsi.marketplace.access.dto.SearchCategoryRequest
import com.wutsi.marketplace.access.dto.SearchCategoryResponse
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/SearchCategoryController.sql"])
class SearchCategoryControllerTest : AbstractLanguageAwareControllerTest() {
    @LocalServerPort
    val port: Int = 0

    @Test
    fun topCategories() {
        // WHEN
        val request = SearchCategoryRequest(level = 0)
        val response = rest.postForEntity(url(), request, SearchCategoryResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val categories = response.body!!.categories.sortedBy { it.id }
        assertEquals(2, categories.size)
    }

    @Test
    fun byParent() {
        // WHEN
        val request = SearchCategoryRequest(parentId = 1100L)
        val response = rest.postForEntity(url(), request, SearchCategoryResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val categories = response.body!!.categories.sortedBy { it.id }
        assertEquals(16, categories.size)
    }

    @Test
    fun byKeyword() {
        // WHEN
        val url = "http://localhost:$port/v1/categories/search"
        val request = SearchCategoryRequest(
            keyword = "el",
        )
        val response = rest.postForEntity(url, request, SearchCategoryResponse::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)

        val categories = response.body!!.categories.sortedBy { it.id }
        assertEquals(2, categories.size)
        assertEquals(listOf(1100L, 1160L), categories.map { it.id })
    }

    @Test
    fun byKeywordFr() {
        // GIVEN
        language = "fr"

        // WHEN
        val url = "http://localhost:$port/v1/categories/search"
        val request = SearchCategoryRequest(
            keyword = "e",
        )
        val response = rest.postForEntity(url, request, SearchCategoryResponse::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)

        val categories = response.body!!.categories.sortedBy { it.id }
        assertEquals(4, categories.size)
        assertEquals(listOf(1100, 1114L, 1151L, 1152L), categories.map { it.id })
    }

    private fun url() = "http://localhost:$port/v1/categories/search"
}
