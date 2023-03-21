package com.wutsi.membership.access.endpoint

import com.wutsi.membership.access.dao.CategoryRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql"])
public class ImportCategoryControllerTest {
    @LocalServerPort
    public val port: Int = 0

    @Autowired
    private lateinit var dao: CategoryRepository

    private val rest = RestTemplate()

    @Test
    fun en() {
        // GIVEN
        val response = rest.getForEntity(url("en"), Any::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val categories = dao.findAll()
        assertEquals(1555, categories.toList().size)

        val category = dao.findById(10000).get()
        assertEquals("Abortion Service", category.title)
    }

    @Test
    fun fr() {
        // WHEN
        val response = rest.getForEntity(url("fr"), Any::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val categories = dao.findAll()
        assertEquals(1555, categories.toList().size)

        val category = dao.findById(10000).get()
        assertEquals("Service d'avortement", category.titleFrench)
    }

    fun url(language: String) = "http://localhost:$port/v1/categories/import?language=$language"
}
