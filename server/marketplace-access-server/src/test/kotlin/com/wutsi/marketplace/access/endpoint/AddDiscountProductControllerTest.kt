package com.wutsi.marketplace.access.endpoint

import com.wutsi.marketplace.access.dto.GetDiscountResponse
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/AddDiscountProductController.sql"])
public class AddDiscountProductControllerTest {
    @LocalServerPort
    public val port: Int = 0

    private val rest = RestTemplate()

    @Test
    public fun invoke() {
        val response = rest.postForEntity(url(100, 101), null, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val discount = rest.getForEntity(
            "http://localhost:$port/v1/discounts/100",
            GetDiscountResponse::class.java,
        ).body!!.discount
        assertEquals(listOf(101L), discount.productIds)
    }

    private fun url(discountId: Long, productId: Long) =
        "http://localhost:$port/v1/discounts/$discountId/products/$productId"
}
