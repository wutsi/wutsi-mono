package com.wutsi.membership.access.endpoint

import com.wutsi.membership.access.dao.CategoryRepository
import com.wutsi.membership.access.dto.SaveCategoryRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/SaveCategoryController.sql"])
class SaveCategoryControllerTest : AbstractLanguageAwareControllerTest() {
    @LocalServerPort
    val port: Int = 0

    @Autowired
    private lateinit var dao: CategoryRepository

    @Test
    fun create() {
        // WHEN
        val request = SaveCategoryRequest(title = "New Category")
        val response = rest.postForEntity(url(1), request, Any::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val category = dao.findById(1L)
        assertTrue(category.isPresent)
        assertEquals(request.title, category.get().title)
        assertNull(category.get().titleFrench)
        assertNull(category.get().titleFrenchAscii)
    }

    @Test
    fun update() {
        // GIVEN
        language = "fr"

        // WHEN
        val request = SaveCategoryRequest(title = "Marketing/Publicité")
        val response = rest.postForEntity(url(1000), request, Any::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val category = dao.findById(1000L)
        assertTrue(category.isPresent)
        assertEquals("Advertising/Marketing", category.get().title)
        assertEquals(request.title, category.get().titleFrench)
        assertEquals("Marketing/Publicite", category.get().titleFrenchAscii)
    }

    private fun url(id: Long) = "http://localhost:$port/v1/categories/$id"
}
