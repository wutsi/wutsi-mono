package com.wutsi.marketplace.manager.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.marketplace.access.dto.GetDiscountResponse
import com.wutsi.marketplace.manager.Fixtures
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetDiscountControllerTest : AbstractSecuredControllerTest() {
    @LocalServerPort
    val port: Int = 0

    @Test
    fun invoke() {
        // GIVEN
        val discount = Fixtures.createDiscount()
        doReturn(GetDiscountResponse(discount)).whenever(marketplaceAccessApi).getDiscount(any())

        // WHEN
        val response = rest.getForEntity(url(discount.id), GetDiscountResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val result = response.body!!.discount
        assertEquals(discount.id, result.id)
        assertEquals(discount.name, result.name)
        assertEquals(discount.storeId, result.storeId)
        assertEquals(
            discount.starts!!.toInstant().toEpochMilli() / 1000,
            result.starts!!.toInstant().toEpochMilli() / 1000,
        )
        assertEquals(discount.ends!!.toInstant().toEpochMilli() / 1000, result.ends!!.toInstant().toEpochMilli() / 1000)
        assertEquals(discount.allProducts, result.allProducts)
        assertEquals(discount.productIds, result.productIds)
    }

    private fun url(id: Long) = "http://localhost:$port/v1/discounts/$id"
}
