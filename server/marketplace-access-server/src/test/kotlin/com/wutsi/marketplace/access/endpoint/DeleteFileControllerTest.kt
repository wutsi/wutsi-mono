package com.wutsi.marketplace.access.endpoint

import com.wutsi.marketplace.access.dao.FileRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.RestTemplate
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/DeleteFileController.sql"])
public class DeleteFileControllerTest {
    @LocalServerPort
    public val port: Int = 0

    private val rest = RestTemplate()

    @Autowired
    private lateinit var dao: FileRepository

    @Test
    public fun delete() {
        rest.delete(url(101))

        val file = dao.findById(101L)
        assertTrue(file.get().isDeleted)
        assertNotNull(file.get().deleted)
    }

    @Test
    public fun alreadyDeleted() {
        rest.delete(url(199))

        val file = dao.findById(199)
        assertTrue(file.get().isDeleted)
        assertNotNull(file.get().deleted)
    }

    private fun url(id: Long) = "http://localhost:$port/v1/files/$id"
}
