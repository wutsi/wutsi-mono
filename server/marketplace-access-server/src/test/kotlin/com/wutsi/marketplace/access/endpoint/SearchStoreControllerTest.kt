package com.wutsi.marketplace.access.endpoint

import com.wutsi.marketplace.access.dto.SearchStoreRequest
import com.wutsi.marketplace.access.dto.SearchStoreResponse
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/SearchStoreController.sql"])
class SearchStoreControllerTest {
    @LocalServerPort
    val port: Int = 0

    private val rest = RestTemplate()

    @Test
    fun all() {
        // WHEN
        val request = SearchStoreRequest()
        val response = rest.postForEntity(url(), request, SearchStoreResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val storeIds = response.body!!.stores.map { it.id }
        assertEquals(4, storeIds.size)
        assertEquals(listOf(100L, 200L, 300L, 400L), storeIds)
    }

    @Test
    fun storeIds() {
        // WHEN
        val request = SearchStoreRequest(
            storeIds = listOf(100L, 199L),
        )
        val response = rest.postForEntity(url(), request, SearchStoreResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val storeIds = response.body!!.stores.map { it.id }
        assertEquals(1, storeIds.size)
        assertEquals(listOf(100L), storeIds)
    }

    @Test
    fun businessId() {
        // WHEN
        val request = SearchStoreRequest(
            businessId = 3L,
        )
        val response = rest.postForEntity(url(), request, SearchStoreResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val storeIds = response.body!!.stores.map { it.id }
        assertEquals(1, storeIds.size)
        assertEquals(listOf(400L), storeIds)
    }

    private fun url() = "http://localhost:$port/v1/stores/search"
}
