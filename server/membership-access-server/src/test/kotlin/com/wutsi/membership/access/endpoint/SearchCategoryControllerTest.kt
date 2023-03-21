package com.wutsi.membership.access.endpoint

import com.wutsi.membership.access.dto.SearchCategoryRequest
import com.wutsi.membership.access.dto.SearchCategoryResponse
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
    fun all() {
        val request = SearchCategoryRequest(limit = 100)
        val response = rest.postForEntity(url(), request, SearchCategoryResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val categories = response.body!!.categories
        assertEquals(13, categories.size)
    }

    @Test
    fun byCategoryIds() {
        val request = SearchCategoryRequest(
            categoryIds = listOf(1001, 1002, 1003),
        )
        val response = rest.postForEntity(url(), request, SearchCategoryResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val categories = response.body!!.categories
        assertEquals(3, categories.size)
    }

    @Test
    fun byKeyword() {
        val request = SearchCategoryRequest(
            keyword = "A",
        )
        val response = rest.postForEntity(url(), request, SearchCategoryResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val categories = response.body!!.categories
        assertEquals(4, categories.size)
        assertEquals(listOf(1000L, 1001L, 1002L, 1003L), categories.map { it.id })
    }

    @Test
    fun byKeywordFrench() {
        language = "fr"
        val request = SearchCategoryRequest(
            keyword = "So",
        )
        val response = rest.postForEntity(url(), request, SearchCategoryResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val categories = response.body!!.categories
        assertEquals(2, categories.size)
        assertEquals(listOf(1012L, 1011L), categories.map { it.id })
    }

    private fun url() = "http://localhost:$port/v1/categories/search"
}
