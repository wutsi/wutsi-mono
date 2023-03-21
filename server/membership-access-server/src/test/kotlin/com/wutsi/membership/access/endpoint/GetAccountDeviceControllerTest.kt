package com.wutsi.membership.access.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.membership.access.dto.GetAccountDeviceResponse
import com.wutsi.membership.access.dto.GetCategoryResponse
import com.wutsi.membership.access.error.ErrorURN
import com.wutsi.platform.core.error.ErrorResponse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/GetAccountDeviceController.sql"])
public class GetAccountDeviceControllerTest {
    @LocalServerPort
    public val port: Int = 0

    protected val rest = RestTemplate()

    @Test
    public fun get() {
        val response = rest.getForEntity(url(100), GetAccountDeviceResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val device = response.body!!.device
        assertEquals("this-is-token", device.token)
        assertEquals("Tabblet", device.type)
        assertEquals("15.4.1", device.osVersion)
        assertEquals("iOS", device.osName)
        assertEquals("AirTab", device.model)
        assertNotNull(device.created)
        assertNotNull(device.updated)
    }

    @Test
    public fun notFound() {
        val ex = assertThrows<HttpClientErrorException> {
            rest.getForEntity(url(9999), GetCategoryResponse::class.java)
        }

        // THEN
        assertEquals(HttpStatus.NOT_FOUND, ex.statusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.DEVICE_NOT_FOUND.urn, response.error.code)
    }

    fun url(id: Long) = "http://localhost:$port/v1/accounts/$id/device"
}
