package com.wutsi.blog.product.endpoint

import com.wutsi.blog.product.dto.SearchCategoryRequest
import com.wutsi.blog.product.dto.SearchCategoryResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/product/SearchCategoryQuery.sql"])
class SearchCategoryQueryTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

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
        val request = SearchCategoryRequest(
            keyword = "el",
        )
        val response = rest.postForEntity(url(), request, SearchCategoryResponse::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)

        val categories = response.body!!.categories.sortedBy { it.id }
        assertEquals(2, categories.size)
        assertEquals(listOf(1100L, 1160L), categories.map { it.id })
    }

    @Test
    fun byKeywordFr() {
        // WHEN
        val request = SearchCategoryRequest(
            keyword = "e",
            language = "fr"
        )
        val response = rest.postForEntity(url(), request, SearchCategoryResponse::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)

        val categories = response.body!!.categories.sortedBy { it.id }
        assertEquals(4, categories.size)
        assertEquals(listOf(1100, 1114L, 1151L, 1152L), categories.map { it.id })
    }

    private fun url() = "/v1/categories/queries/search"
}
