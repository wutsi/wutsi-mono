package com.wutsi.marketplace.manager.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.marketplace.access.dto.UpdateProductAttributeRequest
import com.wutsi.marketplace.manager.dto.ProductAttribute
import com.wutsi.marketplace.manager.dto.UpdateProductAttributeListRequest
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UpdateProductAttributeControllerTest : AbstractProductControllerTest<UpdateProductAttributeListRequest>() {
    override fun url() = "http://localhost:$port/v1/products/attributes"

    override fun createRequest() = UpdateProductAttributeListRequest(
        productId = PRODUCT_ID,
        attributes = listOf(
            ProductAttribute(
                name = "title",
                value = "Hello world",
            ),
            ProductAttribute(
                name = "price",
                value = "10000",
            ),
        ),
    )

    @Test
    fun update() {
        // WHEN
        val response =
            rest.postForEntity(url(), request, Any::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val req = argumentCaptor<UpdateProductAttributeRequest>()
        verify(marketplaceAccessApi, times(2)).updateProductAttribute(
            eq(PRODUCT_ID),
            req.capture(),
        )

        assertEquals(request!!.attributes[0].name, req.firstValue.name)
        assertEquals(request!!.attributes[0].value, req.firstValue.value)
        assertEquals(request!!.attributes[1].name, req.secondValue.name)
        assertEquals(request!!.attributes[1].value, req.secondValue.value)

        verify(eventStream, never()).publish(any(), any())
    }
}
