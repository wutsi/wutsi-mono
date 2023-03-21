package com.wutsi.marketplace.access.endpoint

import com.wutsi.marketplace.access.dao.FileRepository
import com.wutsi.marketplace.access.dto.CreateFileRequest
import com.wutsi.marketplace.access.dto.CreateFileResponse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/CreateFileController.sql"])
public class CreateFileControllerTest {
    @LocalServerPort
    public val port: Int = 0

    private val rest = RestTemplate()

    @Autowired
    private lateinit var dao: FileRepository

    private val productId: Long = 100L

    @Test
    fun add() {
        val request = CreateFileRequest(
            productId = productId,
            url = "https://img.com/2/4/Image-100.png",
            contentSize = 1024,
            contentType = "image/png",
            name = "Image 100.png",
        )
        val response = rest.postForEntity(url(), request, CreateFileResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val fileId = response.body!!.fileId
        val file = dao.findById(fileId)
        assertTrue(file.isPresent)
        assertEquals(productId, file.get().product.id)
        assertEquals(request.url, file.get().url)
        assertEquals(request.name, file.get().name)
        assertEquals(request.contentType, file.get().contentType)
        assertEquals(request.contentSize, file.get().contentSize)
    }

    private fun url() = "http://localhost:$port/v1/files"
}
