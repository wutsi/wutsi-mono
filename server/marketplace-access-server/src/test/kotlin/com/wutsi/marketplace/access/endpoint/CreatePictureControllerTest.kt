package com.wutsi.marketplace.access.endpoint

import com.wutsi.marketplace.access.dao.PictureRepository
import com.wutsi.marketplace.access.dao.ProductRepository
import com.wutsi.marketplace.access.dto.CreatePictureRequest
import com.wutsi.marketplace.access.dto.CreatePictureResponse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/CreatePictureController.sql"])
class CreatePictureControllerTest {
    @LocalServerPort
    val port: Int = 0

    private val rest = RestTemplate()

    @Autowired
    private lateinit var dao: PictureRepository

    @Autowired
    private lateinit var productDao: ProductRepository

    @Test
    fun create() {
        val request = CreatePictureRequest(
            productId = 100,
            url = "https://img.com/image-100.png",
        )
        val response = rest.postForEntity(url(), request, CreatePictureResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val pictureId = response.body!!.pictureId
        val picture = dao.findById(pictureId)
        assertTrue(picture.isPresent)
        assertEquals(request.productId, picture.get().product.id)
        assertEquals(request.url.lowercase(), picture.get().url)
        assertNotNull(picture.get().hash)

        val product = productDao.findById(request.productId).get()
        assertEquals(101L, product.thumbnail?.id)
    }

    @Test
    fun createAndResetThumbnail() {
        val request = CreatePictureRequest(
            productId = 200L,
            url = "https://img.com/ImAge-200.png",
        )
        val response = rest.postForEntity(url(), request, CreatePictureResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val pictureId = response.body!!.pictureId
        val picture = dao.findById(pictureId)
        assertTrue(picture.isPresent)
        assertEquals(request.productId, picture.get().product.id)
        assertEquals(request.url, picture.get().url)
        assertNotNull(picture.get().hash)

        val product = productDao.findById(request.productId).get()
        assertEquals(pictureId, product.thumbnail?.id)
    }

    private fun url() = "http://localhost:$port/v1/pictures"
}
