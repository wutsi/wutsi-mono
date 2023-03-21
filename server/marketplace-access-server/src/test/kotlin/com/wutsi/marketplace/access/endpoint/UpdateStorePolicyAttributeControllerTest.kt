package com.wutsi.marketplace.access.endpoint

import com.wutsi.marketplace.access.dao.StoreRepository
import com.wutsi.marketplace.access.dto.UpdateStorePolicyAttributeRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/UpdateStorePolicyAttributeController.sql"])
class UpdateStorePolicyAttributeControllerTest {
    @LocalServerPort
    val port: Int = 0

    val rest = RestTemplate()

    val storeId: Long = 100L

    @Autowired
    private lateinit var dao: StoreRepository

    @Test
    fun updateCancellationAccepted() {
        updatePolicy("cancellation-accepted", "true")
        assertEquals(true, dao.findById(storeId).get().cancellationAccepted)
    }

    @Test
    fun updateCancellationWindow() {
        updatePolicy("cancellation-window", "13")
        assertEquals(13, dao.findById(storeId).get().cancellationWindow)
    }

    @Test
    fun updateCancellationMessage() {
        updatePolicy("cancellation-message", "Yo man")
        assertEquals("Yo man", dao.findById(storeId).get().cancellationMessage)
    }

    @Test
    fun updateReturnAccepted() {
        updatePolicy("return-accepted", "true")
        assertEquals(true, dao.findById(storeId).get().returnAccepted)
    }

    @Test
    fun updateReturnContactWindow() {
        updatePolicy("return-contact-window", "12")
        assertEquals(12, dao.findById(storeId).get().returnContactWindow)
    }

    @Test
    fun updateReturnShipBackWindow() {
        updatePolicy("return-ship-back-window", "30")
        assertEquals(30, dao.findById(storeId).get().returnShipBackWindow)
    }

    private fun updatePolicy(name: String, value: String) {
        val request = UpdateStorePolicyAttributeRequest(
            name = name,
            value = value,
        )
        rest.postForEntity(url(), request, Any::class.java)
    }

    private fun url() =
        "http://localhost:$port/v1/stores/$storeId/policies"
}
