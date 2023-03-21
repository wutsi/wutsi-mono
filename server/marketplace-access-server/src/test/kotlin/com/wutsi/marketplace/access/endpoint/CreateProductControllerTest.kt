package com.wutsi.marketplace.access.endpoint

import com.wutsi.enums.ProductStatus
import com.wutsi.enums.ProductType
import com.wutsi.marketplace.access.dao.PictureRepository
import com.wutsi.marketplace.access.dao.ProductRepository
import com.wutsi.marketplace.access.dao.StoreRepository
import com.wutsi.marketplace.access.dto.CreateProductRequest
import com.wutsi.marketplace.access.dto.CreateProductResponse
import org.apache.commons.codec.digest.DigestUtils
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateProductControllerTest {
    @LocalServerPort
    val port: Int = 0

    @Autowired
    private lateinit var dao: ProductRepository

    @Autowired
    private lateinit var pictureDao: PictureRepository

    @Autowired
    private lateinit var storeDao: StoreRepository

    private val rest = RestTemplate()

    @Test
    @Sql(value = ["/db/clean.sql", "/db/CreateProductController.sql"])
    fun create() {
        // WHEN
        val request = CreateProductRequest(
            storeId = 1L,
            pictureUrl = "httpS://img.com/the-product.png",
            categoryId = 1110L,
            title = "Ze product",
            summary = "This is the summary",
            price = 15000L,
            quantity = 100,
            type = ProductType.DIGITAL_DOWNLOAD.name,
        )
        val response = rest.postForEntity(url(), request, CreateProductResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val productId = response.body!!.productId
        val product = dao.findById(productId)
        assertTrue(product.isPresent)
        assertEquals(request.storeId, product.get().store.id)
        assertEquals(request.categoryId, product.get().category?.id)
        assertEquals(request.title, product.get().title)
        assertEquals(request.summary, product.get().summary)
        assertEquals(request.price, product.get().price)
        assertEquals(request.quantity, product.get().quantity)
        assertEquals("XAF", product.get().currency)
        assertEquals(ProductStatus.DRAFT, product.get().status)
        assertNotNull(product.get().created)
        assertNotNull(product.get().updated)
        assertNull(product.get().deleted)
        assertNull(product.get().published)
        assertNotNull(product.get().thumbnail)
        assertEquals(ProductType.DIGITAL_DOWNLOAD, product.get().type)

        val thumbnail = pictureDao.findById(product.get().thumbnail!!.id)
        assertTrue(thumbnail.isPresent)
        assertEquals(request.pictureUrl, thumbnail.get().url)
        assertEquals(DigestUtils.md5Hex(request.pictureUrl?.lowercase()), thumbnail.get().hash)

        val store = storeDao.findById(request.storeId)
        assertEquals(1, store.get().productCount)
        assertEquals(0, store.get().publishedProductCount)
    }

    @Test
    @Sql(value = ["/db/clean.sql", "/db/CreateProductController.sql"])
    fun createWithoutPicture() {
        // WHEN
        val request = CreateProductRequest(
            storeId = 2L,
            categoryId = 1110L,
            title = "Ze product",
            summary = "This is the summary",
            price = 15000L,
        )
        val response = rest.postForEntity(url(), request, CreateProductResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val productId = response.body!!.productId
        val product = dao.findById(productId)
        assertTrue(product.isPresent)
        assertEquals(request.storeId, product.get().store.id)
        assertEquals(request.categoryId, product.get().category?.id)
        assertEquals(request.title, product.get().title)
        assertEquals(request.summary, product.get().summary)
        assertEquals(request.price, product.get().price)
        assertEquals("XAF", product.get().currency)
        assertEquals(ProductStatus.DRAFT, product.get().status)
        assertNotNull(product.get().created)
        assertNotNull(product.get().updated)
        assertNull(product.get().deleted)
        assertNull(product.get().published)
        assertNull(product.get().thumbnail)

        val store = storeDao.findById(request.storeId)
        assertEquals(4, store.get().productCount)
        assertEquals(1, store.get().publishedProductCount)
    }

    private fun url() = "http://localhost:$port/v1/products"
}
