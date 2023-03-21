package com.wutsi.marketplace.access.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.marketplace.access.dao.DiscountRepository
import com.wutsi.marketplace.access.dto.UpdateProductAttributeRequest
import com.wutsi.marketplace.access.error.ErrorURN
import com.wutsi.platform.core.error.ErrorResponse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.RestTemplate
import java.text.SimpleDateFormat
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/UpdateDiscountAttributeController.sql"])
public class UpdateDiscountAttributeControllerTest {
    @LocalServerPort
    public val port: Int = 0

    private val rest = RestTemplate()

    @Autowired
    private lateinit var dao: DiscountRepository

    private val discountId: Long = 100L

    @Test
    fun name() {
        val request = UpdateProductAttributeRequest(
            name = "name",
            value = "FINxxx",
        )
        val response = rest.postForEntity(url(), request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val discount = dao.findById(discountId).get()
        assertEquals(request.value, discount.name)
    }

    @Test
    fun rate() {
        val request = UpdateProductAttributeRequest(
            name = "rate",
            value = "5",
        )
        val response = rest.postForEntity(url(), request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val discount = dao.findById(discountId).get()
        assertEquals(request.value?.toInt(), discount.rate)
    }

    @Test
    fun starts() {
        val request = UpdateProductAttributeRequest(
            name = "starts",
            value = "2020-01-01 06:00:00",
        )
        val response = rest.postForEntity(url(), request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val discount = dao.findById(discountId).get()
        assertEquals(request.value, SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(discount.starts))
    }

    @Test
    fun startsEmpty() {
        val request = UpdateProductAttributeRequest(
            name = "starts",
            value = "",
        )
        val response = rest.postForEntity(url(), request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val discount = dao.findById(discountId).get()
        assertNull(discount.starts)
    }

    @Test
    fun startsNull() {
        val request = UpdateProductAttributeRequest(
            name = "starts",
            value = null,
        )
        val response = rest.postForEntity(url(), request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val discount = dao.findById(discountId).get()
        assertNull(discount.starts)
    }

    @Test
    fun ends() {
        val request = UpdateProductAttributeRequest(
            name = "ends",
            value = "2020-01-01 06:00:00",
        )
        val response = rest.postForEntity(url(), request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val discount = dao.findById(discountId).get()
        assertEquals(request.value, SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(discount.ends))
    }

    @Test
    fun endsEmpty() {
        val request = UpdateProductAttributeRequest(
            name = "ends",
            value = "",
        )
        val response = rest.postForEntity(url(), request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val discount = dao.findById(discountId).get()
        assertNull(discount.ends)
    }

    @Test
    fun endsNull() {
        val request = UpdateProductAttributeRequest(
            name = "ends",
            value = null,
        )
        val response = rest.postForEntity(url(), request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val discount = dao.findById(discountId).get()
        assertNull(discount.ends)
    }

    @Test
    fun allProducts() {
        val request = UpdateProductAttributeRequest(
            name = "all-products",
            value = "true",
        )
        val response = rest.postForEntity(url(), request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val discount = dao.findById(discountId).get()
        assertTrue(discount.allProducts)
    }

    @Test
    fun badAttribute() {
        val request = UpdateProductAttributeRequest("xx", "15000")
        val ex = assertThrows<HttpStatusCodeException> {
            rest.postForEntity(url(), request, Any::class.java)
        }

        assertEquals(400, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.ATTRIBUTE_NOT_VALID.urn, response.error.code)
    }

    private fun url() = "http://localhost:$port/v1/discounts/$discountId/attributes"
}
