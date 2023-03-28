package com.wutsi.marketplace.manager.endpoint

import com.nhaarman.mockitokotlin2.verify
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AddDiscountProductControllerTest : AbstractSecuredControllerTest() {
    @LocalServerPort
    val port: Int = 0

    @Test
    public fun invoke() {
        rest.postForEntity(url(100, 101), null, Any::class.java)

        verify(marketplaceAccessApi).addDiscountProduct(100, 101)
    }

    private fun url(discountId: Long, productId: Long) =
        "http://localhost:$port/v1/discounts/$discountId/products/$productId"
}
