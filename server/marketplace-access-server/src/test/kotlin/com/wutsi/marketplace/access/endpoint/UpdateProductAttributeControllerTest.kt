package com.wutsi.marketplace.access.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.enums.ProductType
import com.wutsi.marketplace.access.dao.ProductRepository
import com.wutsi.marketplace.access.dto.UpdateProductAttributeRequest
import com.wutsi.marketplace.access.error.ErrorURN
import com.wutsi.platform.core.error.ErrorResponse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals
import kotlin.test.assertNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/UpdateProductAttributeController.sql"])
class UpdateProductAttributeControllerTest {
    companion object {
        const val PRODUCT_ID = 100L
    }

    @LocalServerPort
    val port: Int = 0

    private val rest = RestTemplate()

    @Autowired
    private lateinit var dao: ProductRepository

    @Test
    fun title() {
        val request = UpdateProductAttributeRequest("title", "THIS IS THE VALUE")
        val response = rest.postForEntity(url(), request, Any::class.java)

        assertEquals(200, response.statusCodeValue)

        val product = dao.findById(PRODUCT_ID).get()
        assertEquals(request.value, product.title)
    }

    @Test
    fun summary() {
        val request = UpdateProductAttributeRequest("summary", "THIS IS THE VALUE")
        val response = rest.postForEntity(url(), request, Any::class.java)

        assertEquals(200, response.statusCodeValue)

        val product = dao.findById(PRODUCT_ID).get()
        assertEquals(request.value, product.summary)
    }

    @Test
    fun summaryEmpty() {
        val request = UpdateProductAttributeRequest("summary", "")
        val response = rest.postForEntity(url(), request, Any::class.java)

        assertEquals(200, response.statusCodeValue)

        val product = dao.findById(PRODUCT_ID).get()
        assertNull(product.summary)
    }

    @Test
    fun description() {
        val request = UpdateProductAttributeRequest("description", "THIS IS THE VALUE")
        val response = rest.postForEntity(url(), request, Any::class.java)

        assertEquals(200, response.statusCodeValue)

        val product = dao.findById(PRODUCT_ID).get()
        assertEquals(request.value, product.description)
    }

    @Test
    fun price() {
        val request = UpdateProductAttributeRequest("price", "10000")
        val response = rest.postForEntity(url(), request, Any::class.java)

        assertEquals(200, response.statusCodeValue)

        val product = dao.findById(PRODUCT_ID).get()
        assertEquals(request.value?.toLong(), product.price)
    }

    @Test
    fun priceNull() {
        val request = UpdateProductAttributeRequest("price", null)
        val response = rest.postForEntity(url(), request, Any::class.java)

        assertEquals(200, response.statusCodeValue)

        val product = dao.findById(PRODUCT_ID).get()
        assertNull(product.price)
    }

    @Test
    fun priceEmpty() {
        val request = UpdateProductAttributeRequest("price", "")
        val response = rest.postForEntity(url(), request, Any::class.java)

        assertEquals(200, response.statusCodeValue)

        val product = dao.findById(PRODUCT_ID).get()
        assertNull(product.price)
    }

    @Test
    fun thumbnailId() {
        val request = UpdateProductAttributeRequest("thumbnail-id", "102")
        val response = rest.postForEntity(url(), request, Any::class.java)

        assertEquals(200, response.statusCodeValue)

        val product = dao.findById(PRODUCT_ID).get()
        assertEquals(request.value?.toLong(), product.thumbnail?.id)
    }

    @Test
    fun thumbnailIdInvalid() {
        val request = UpdateProductAttributeRequest("thumbnail-id", "99999")
        val ex = assertThrows<HttpStatusCodeException> {
            rest.postForEntity(url(), request, Any::class.java)
        }

        assertEquals(404, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.PICTURE_NOT_FOUND.urn, response.error.code)
    }

    @Test
    fun quantity() {
        val request = UpdateProductAttributeRequest("quantity", "10")
        val response = rest.postForEntity(url(), request, Any::class.java)

        assertEquals(200, response.statusCodeValue)

        val product = dao.findById(PRODUCT_ID).get()
        assertEquals(request.value?.toInt(), product.quantity)
    }

    @Test
    fun categoryId() {
        val request = UpdateProductAttributeRequest("category-id", "1100")
        val response = rest.postForEntity(url(), request, Any::class.java)

        assertEquals(200, response.statusCodeValue)

        val product = dao.findById(PRODUCT_ID).get()
        assertEquals(request.value?.toLong(), product.category?.id)
    }

    @Test
    fun categoryIdInvalid() {
        val request = UpdateProductAttributeRequest("category-id", "99999")
        val ex = assertThrows<HttpStatusCodeException> {
            rest.postForEntity(url(), request, Any::class.java)
        }

        assertEquals(404, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.CATEGORY_NOT_FOUND.urn, response.error.code)
    }

    @Test
    fun type() {
        val request = UpdateProductAttributeRequest("type", ProductType.EVENT.name)
        val response = rest.postForEntity(url(), request, Any::class.java)

        assertEquals(200, response.statusCodeValue)

        val product = dao.findById(PRODUCT_ID).get()
        assertEquals(request.value, product.type.name)
    }

    @Test
    fun notFound() {
        val request = UpdateProductAttributeRequest("price", "15000")
        val ex = assertThrows<HttpStatusCodeException> {
            rest.postForEntity(url(99999), request, Any::class.java)
        }

        assertEquals(404, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.PRODUCT_NOT_FOUND.urn, response.error.code)
    }

    @Test
    fun deleted() {
        val request = UpdateProductAttributeRequest("price", "15000")
        val ex = assertThrows<HttpStatusCodeException> {
            rest.postForEntity(url(900), request, Any::class.java)
        }

        assertEquals(404, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.PRODUCT_NOT_FOUND.urn, response.error.code)
    }

    @Test
    fun badAttribute() {
        val request = UpdateProductAttributeRequest("xx", "15000")
        val ex = assertThrows<HttpStatusCodeException> {
            rest.postForEntity(url(100), request, Any::class.java)
        }

        assertEquals(400, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.ATTRIBUTE_NOT_VALID.urn, response.error.code)
    }

    private fun url(productId: Long = 100L) =
        "http://localhost:$port/v1/products/$productId/attributes"
}
