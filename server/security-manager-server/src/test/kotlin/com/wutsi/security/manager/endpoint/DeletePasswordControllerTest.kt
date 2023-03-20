package com.wutsi.security.manager.endpoint

import com.wutsi.security.manager.dao.PasswordRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/DeletePasswordController.sql"])
public class DeletePasswordControllerTest : AbstractSecuredControllerTest() {
    @LocalServerPort
    public val port: Int = 0

    @Autowired
    private lateinit var dao: PasswordRepository

    @Test
    fun delete() {
        // WHEN
        rest.delete(url())

        // THEN
        val password = dao.findById(100).get()
        assertTrue(password.isDeleted)
        assertNotNull(password.deleted)
    }

    @Test
    fun notFound() {
        createRestTemplate(99999).delete(url())
    }

    @Test
    fun deleted() {
        createRestTemplate(999).delete(url())

        // THEN
        val password = dao.findById(999).get()
        assertTrue(password.isDeleted)
        assertNotNull(password.deleted)
    }

    private fun url() = "http://localhost:$port/v1/passwords/"
}
