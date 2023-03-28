package com.wutsi.marketplace.manager.endpoint

import com.nhaarman.mockitokotlin2.verify
import com.wutsi.marketplace.manager.dto.UpdateDiscountAttributeRequest
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UpdateDiscountAttributeControllerTest : AbstractSecuredControllerTest() {
    @LocalServerPort
    val port: Int = 0

    @Test
    public fun invoke() {
        val request = UpdateDiscountAttributeRequest(
            name = "ffo",
            value = "bar",
        )
        rest.postForEntity(url(100), request, Any::class.java)

        verify(marketplaceAccessApi).updateDiscountAttribute(
            100,
            com.wutsi.marketplace.access.dto.UpdateDiscountAttributeRequest(
                name = request.name,
                value = request.value,
            ),
        )
    }

    fun url(id: Long) = "http://localhost:$port/v1/discounts/$id/attributes"
}
