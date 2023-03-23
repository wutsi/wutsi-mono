package com.wutsi.marketplace.manager.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.marketplace.access.dto.GetProductResponse
import com.wutsi.marketplace.manager.Fixtures
import com.wutsi.marketplace.manager.endpoint.AbstractSecuredControllerTest.Companion.PRODUCT_ID
import com.wutsi.marketplace.manager.endpoint.AbstractSecuredControllerTest.Companion.STORE_ID
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GetProductControllerTest : AbstractControllerTest() {
    @LocalServerPort
    val port: Int = 0

    private fun url() = "http://localhost:$port/v1/products/$PRODUCT_ID"

    @Test
    public fun invoke() {
        // GIVEN
        val product = Fixtures.createProduct(
            id = PRODUCT_ID,
            storeId = STORE_ID,
            pictures = listOf(Fixtures.createPictureSummary(1), Fixtures.createPictureSummary(2)),
        )
        doReturn(GetProductResponse(product)).whenever(marketplaceAccessApi).getProduct(any())

        // WHEN
        val response =
            rest.getForEntity(url(), com.wutsi.marketplace.manager.dto.GetProductResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        verify(marketplaceAccessApi).getProduct(PRODUCT_ID)

        val prod = response.body!!.product
        assertEquals(product.id, prod.id)
        assertEquals(product.store.id, prod.store.id)
        assertEquals(product.store.accountId, prod.store.accountId)
        assertEquals(product.store.currency, prod.store.currency)
        assertEquals(product.title, prod.title)
        assertEquals(product.summary, prod.summary)
        assertEquals(product.description, prod.description)
        assertEquals(product.price, prod.price)
        assertEquals(product.currency, prod.currency)
        assertEquals(product.thumbnail?.url, prod.thumbnail?.url)
        assertEquals(product.pictures.size, prod.pictures.size)

        verify(eventStream, never()).publish(any(), any())
    }
}
