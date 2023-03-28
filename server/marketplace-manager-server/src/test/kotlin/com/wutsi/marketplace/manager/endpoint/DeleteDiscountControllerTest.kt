package com.wutsi.marketplace.manager.endpoint

import com.nhaarman.mockitokotlin2.verify
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DeleteDiscountControllerTest : AbstractSecuredControllerTest() {
    @LocalServerPort
    val port: Int = 0

    @Test
    public fun invoke() {
        rest.delete(url(100))

        verify(marketplaceAccessApi).deleteDiscount(100)
    }

    private fun url(id: Long) = "http://localhost:$port/v1/discounts/$id"
}
