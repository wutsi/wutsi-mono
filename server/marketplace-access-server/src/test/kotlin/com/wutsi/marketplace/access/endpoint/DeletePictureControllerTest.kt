package com.wutsi.marketplace.access.endpoint

import com.wutsi.marketplace.access.dao.PictureRepository
import com.wutsi.marketplace.access.dao.ProductRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/DeletePictureController.sql"])
class DeletePictureControllerTest {
    @LocalServerPort
    val port: Int = 0

    private val rest = RestTemplate()

    @Autowired
    private lateinit var dao: PictureRepository

    @Autowired
    private lateinit var productDao: ProductRepository

    @Test
    fun delete() {
        rest.delete(url(102))

        val picture = dao.findById(102L).get()
        assertTrue(picture.isDeleted)
        assertNotNull(picture.isDeleted)

        val product = productDao.findById(picture.product.id).get()
        assertEquals(101L, product.thumbnail?.id)
    }

    @Test
    fun deleteAndResetThumbnail() {
        rest.delete(url(201))

        val picture = dao.findById(201L).get()
        assertTrue(picture.isDeleted)
        assertNotNull(picture.isDeleted)

        val product = productDao.findById(picture.product.id).get()
        assertNull(product.thumbnail)
    }

    @Test
    fun deleteAndUpdateThumbnail() {
        rest.delete(url(301))

        val picture = dao.findById(301L).get()
        assertTrue(picture.isDeleted)
        assertNotNull(picture.isDeleted)

        val product = productDao.findById(picture.product.id).get()
        assertEquals(302, product.thumbnail?.id)
    }

    private fun url(id: Long) = "http://localhost:$port/v1/pictures/$id"
}
