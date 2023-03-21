package com.wutsi.marketplace.access.endpoint

import com.wutsi.enums.StoreStatus
import com.wutsi.marketplace.access.dao.StoreRepository
import com.wutsi.marketplace.access.dto.CreateStoreRequest
import com.wutsi.marketplace.access.dto.CreateStoreResponse
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
@Sql(value = ["/db/clean.sql", "/db/CreateStoreController.sql"])
class CreateStoreControllerTest {
    @LocalServerPort
    val port: Int = 0

    private val rest = RestTemplate()

    @Autowired
    private lateinit var dao: StoreRepository

    @Test
    fun create() {
        val request = CreateStoreRequest(
            accountId = 555,
            businessId = 333,
            currency = "USD",
        )
        val response = rest.postForEntity(url(), request, CreateStoreResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val store = dao.findById(response.body!!.storeId)
        assertTrue(store.isPresent)
        assertEquals(request.accountId, store.get().accountId)
        assertEquals(request.businessId, store.get().businessId)
        assertEquals(request.currency, store.get().currency)
        assertEquals(StoreStatus.ACTIVE, store.get().status)
        assertEquals(0, store.get().productCount)
        assertEquals(0, store.get().publishedProductCount)
        assertNotNull(store.get().created)
    }

    @Test
    fun duplicate() {
        val request = CreateStoreRequest(
            accountId = 1,
            currency = "USD",
        )
        val response = rest.postForEntity(url(), request, CreateStoreResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        assertEquals(100L, response.body!!.storeId)
    }

    private fun url() = "http://localhost:$port/v1/stores"
}
