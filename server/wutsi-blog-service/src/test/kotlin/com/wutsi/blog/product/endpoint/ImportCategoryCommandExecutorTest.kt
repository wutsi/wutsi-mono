package com.wutsi.blog.product.endpoint

import com.wutsi.blog.product.dao.CategoryRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql"])
class ImportCategoryCommandExecutorTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Autowired
    private lateinit var dao: CategoryRepository

    @Test
    fun import() {
        val result = rest.getForEntity("/v1/categories/commands/import", Any::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val categories = dao.findAll().toList()
        assertEquals(90, categories.size)

        val category = dao.findById(1610L).get()
        assertEquals(1, category.level)
        assertEquals(1600L, category.parent?.id)
        assertEquals("Animals", category.title)
        assertEquals("Leisure and hobbies > Animals", category.longTitle)
        assertEquals("Animaux", category.titleFrench)
        assertEquals("Loisir et hobbies > Animaux", category.longTitleFrench)
        assertEquals("Animaux", category.titleFrenchAscii)
    }
}
