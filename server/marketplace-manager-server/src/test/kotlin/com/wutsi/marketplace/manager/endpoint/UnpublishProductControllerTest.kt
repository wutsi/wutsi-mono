package com.wutsi.marketplace.manager.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.enums.ProductStatus
import com.wutsi.marketplace.access.dto.UpdateProductStatusRequest
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UnpublishProductControllerTest : AbstractProductControllerTest<Long>() {
    override fun url() = "http://localhost:$port/v1/products/$PRODUCT_ID/unpublish"
    override fun createRequest(): Long? = null

    @Test
    public fun invoke() {
        // WHEN
        val response = rest.postForEntity(url(), request, Any::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        verify(marketplaceAccessApi).updateProductStatus(
            PRODUCT_ID,
            UpdateProductStatusRequest(
                status = ProductStatus.DRAFT.name,
            ),
        )

        verify(eventStream, never()).publish(any(), any())
    }
}
