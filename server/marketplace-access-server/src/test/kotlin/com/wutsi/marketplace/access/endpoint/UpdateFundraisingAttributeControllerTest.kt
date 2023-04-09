package com.wutsi.marketplace.access.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.marketplace.access.dao.FundraisingRepository
import com.wutsi.marketplace.access.dto.UpdateFundraisingAttributeRequest
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/UpdateFundraisingAttributeController.sql"])
public class UpdateFundraisingAttributeControllerTest {
    @LocalServerPort
    public val port: Int = 0

    val rest = RestTemplate()

    @Autowired
    private lateinit var dao: FundraisingRepository

    private fun url(fundraisingId: Long) =
        "http://localhost:$port/v1/fundraisings/$fundraisingId/attributes"

    @Test
    public fun description() {
        val request = UpdateFundraisingAttributeRequest("description", "THIS IS THE VALUE")
        val response = rest.postForEntity(url(100L), request, Any::class.java)

        assertEquals(200, response.statusCodeValue)

        val product = dao.findById(100L).get()
        assertEquals(request.value, product.description)
    }

    @Test
    public fun videUrl() {
        val request = UpdateFundraisingAttributeRequest("video-url", "https://www.youtube.com/13243")
        val response = rest.postForEntity(url(100L), request, Any::class.java)

        assertEquals(200, response.statusCodeValue)

        val product = dao.findById(100L).get()
        assertEquals(request.value, product.videoUrl)
    }

    @Test
    public fun amount() {
        val request = UpdateFundraisingAttributeRequest("amount", "5000")
        val response = rest.postForEntity(url(100L), request, Any::class.java)

        assertEquals(200, response.statusCodeValue)

        val product = dao.findById(100L).get()
        assertEquals(request.value?.toLong(), product.amount)
    }

    @Test
    fun badAttribute() {
        val request = UpdateFundraisingAttributeRequest("xx", "15000")
        val ex = assertThrows<HttpStatusCodeException> {
            rest.postForEntity(url(100), request, Any::class.java)
        }

        assertEquals(400, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.ATTRIBUTE_NOT_VALID.urn, response.error.code)
    }

    @Test
    fun notFound() {
        val request = UpdateFundraisingAttributeRequest("price", "15000")
        val ex = assertThrows<HttpStatusCodeException> {
            rest.postForEntity(url(99999), request, Any::class.java)
        }

        assertEquals(404, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.FUNDRAISING_NOT_FOUND.urn, response.error.code)
    }
}
