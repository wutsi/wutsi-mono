package com.wutsi.marketplace.manager.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.enums.ProductType
import com.wutsi.error.ErrorURN
import com.wutsi.marketplace.access.dto.GetStoreResponse
import com.wutsi.marketplace.manager.Fixtures
import com.wutsi.marketplace.manager.dto.CreateProductRequest
import com.wutsi.marketplace.manager.dto.CreateProductResponse
import com.wutsi.platform.core.error.ErrorResponse
import com.wutsi.regulation.RegulationEngine
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateProductControllerTest : AbstractProductControllerTest<CreateProductRequest>() {
    override fun url() = "http://localhost:$port/v1/products"

    override fun createRequest() = CreateProductRequest(
        pictureUrl = "https://img.com/1.png",
        title = "Product A",
        summary = "This is a summary",
        categoryId = 3203029,
        price = 1500,
        type = ProductType.EVENT.name,
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        val productId = PRODUCT_ID
        doReturn(com.wutsi.marketplace.access.dto.CreateProductResponse(productId)).whenever(marketplaceAccessApi)
            .createProduct(
                any(),
            )
    }

    @Test
    fun create() {
        // WHEN
        val response = rest.postForEntity(url(), request, CreateProductResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        assertEquals(PRODUCT_ID, response.body!!.productId)

        verify(marketplaceAccessApi).createProduct(
            com.wutsi.marketplace.access.dto.CreateProductRequest(
                storeId = STORE_ID,
                pictureUrl = request!!.pictureUrl,
                title = request!!.title,
                summary = request!!.summary,
                categoryId = request!!.categoryId,
                price = request!!.price,
                quantity = request!!.quantity,
                type = request!!.type,
            ),
        )

        verify(eventStream, never()).publish(any(), any())
    }

    @Test
    fun tooManyProducts() {
        // GIVEN
        store =
            Fixtures.createStore(id = STORE_ID, accountId = ACCOUNT_ID, productCount = RegulationEngine.MAX_PRODUCTS)
        doReturn(GetStoreResponse(store)).whenever(marketplaceAccessApi).getStore(any())

        // WHEN
        val ex = assertThrows<HttpClientErrorException> {
            submit()
        }

        // THEN
        assertEquals(HttpStatus.CONFLICT, ex.statusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.PRODUCT_LIMIT_REACHED.urn, response.error.code)

        verify(marketplaceAccessApi, never()).createStore(any())
        verify(eventStream, never()).publish(any(), any())
    }
}
