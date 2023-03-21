package com.wutsi.marketplace.access.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.enums.ProductStatus
import com.wutsi.marketplace.access.dao.ProductRepository
import com.wutsi.marketplace.access.dao.StoreRepository
import com.wutsi.marketplace.access.dto.UpdateProductStatusRequest
import com.wutsi.marketplace.access.error.ErrorURN
import com.wutsi.platform.core.error.ErrorResponse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/UpdateProductStatusController.sql"])
class UpdateProductStatusControllerTest {
    @LocalServerPort
    val port: Int = 0

    val rest = RestTemplate()

    @Autowired
    private lateinit var dao: ProductRepository

    @Autowired
    private lateinit var storeDao: StoreRepository

    @Test
    fun publish() {
        val request = UpdateProductStatusRequest(
            status = ProductStatus.PUBLISHED.name,
        )
        val response = rest.postForEntity(url(100), request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val product = dao.findById(100).get()
        assertEquals(ProductStatus.PUBLISHED, product.status)
        assertNotNull(product.published)

        val store = storeDao.findById(product.store.id).get()
        assertEquals(2, store.productCount)
        assertEquals(2, store.publishedProductCount)
    }

    @Test
    fun unpublish() {
        val request = UpdateProductStatusRequest(
            status = ProductStatus.DRAFT.name,
        )
        val response = rest.postForEntity(url(200), request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val product = dao.findById(200).get()
        assertEquals(ProductStatus.DRAFT, product.status)
        assertNull(product.published)

        val store = storeDao.findById(product.store.id).get()
        assertEquals(4, store.productCount)
        assertEquals(1, store.publishedProductCount)
    }

    @Test
    fun badStatus() {
        val request = UpdateProductStatusRequest(
            status = ProductStatus.UNKNOWN.name,
        )
        val ex = assertThrows<HttpClientErrorException> {
            rest.postForEntity(url(100), request, Any::class.java)
        }

        // THEN
        assertEquals(HttpStatus.BAD_REQUEST, ex.statusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.STATUS_NOT_VALID.urn, response.error.code)
    }

    private fun url(productId: Long = 100L) =
        "http://localhost:$port/v1/products/$productId/status"
}
