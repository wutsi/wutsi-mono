package com.wutsi.marketplace.access.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.enums.StoreStatus
import com.wutsi.marketplace.access.dto.GetStoreResponse
import com.wutsi.marketplace.access.error.ErrorURN
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
@Sql(value = ["/db/clean.sql", "/db/GetStoreController.sql"])
class GetStoreControllerTest {
    @LocalServerPort
    val port: Int = 0

    private val rest = RestTemplate()

    @Test
    fun get() {
        val response = rest.getForEntity(url(100), GetStoreResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val store = response.body!!.store
        assertEquals(100L, store.id)
        assertEquals(100L, store.accountId)
        assertEquals(333L, store.businessId)
        assertEquals(10, store.productCount)
        assertEquals(5, store.publishedProductCount)
        assertEquals(StoreStatus.INACTIVE.name, store.status)
        assertNotNull(store.created)
        assertNotNull(store.updated)
        assertNotNull(store.deactivated)
        assertEquals(true, store.cancellationPolicy.accepted)
        assertEquals(3, store.cancellationPolicy.window)
        assertEquals("Hurry up", store.cancellationPolicy.message)
        assertEquals(true, store.returnPolicy.accepted)
        assertEquals(5, store.returnPolicy.contactWindow)
        assertEquals(10, store.returnPolicy.shipBackWindow)
        assertEquals("Yo!", store.returnPolicy.message)
    }

    @Test
    fun notFound() {
        val ex = assertThrows<HttpClientErrorException> {
            rest.getForEntity(url(99999), GetStoreResponse::class.java)
        }

        // THEN
        assertEquals(HttpStatus.NOT_FOUND, ex.statusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.STORE_NOT_FOUND.urn, response.error.code)
    }

    private fun url(id: Long) = "http://localhost:$port/v1/stores/$id"
}
