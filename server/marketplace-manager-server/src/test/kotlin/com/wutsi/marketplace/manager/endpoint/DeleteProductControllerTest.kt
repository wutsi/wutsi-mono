package com.wutsi.marketplace.manager.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.error.ErrorURN
import com.wutsi.marketplace.access.dto.GetProductResponse
import com.wutsi.marketplace.manager.Fixtures
import com.wutsi.membership.access.dto.GetAccountResponse
import com.wutsi.platform.core.error.ErrorResponse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DeleteProductControllerTest : AbstractSecuredControllerTest() {
    @LocalServerPort
    val port: Int = 0

    private fun url() = "http://localhost:$port/v1/products/$PRODUCT_ID"

    @Test
    fun delete() {
        // GIVEN
        val account = Fixtures.createAccount(id = ACCOUNT_ID, business = true, storeId = STORE_ID)
        doReturn(GetAccountResponse(account)).whenever(membershipAccessApi).getAccount(any())

        val product = Fixtures.createProduct(id = PRODUCT_ID, storeId = STORE_ID)
        doReturn(GetProductResponse(product)).whenever(marketplaceAccessApi).getProduct(any())

        // WHEN
        rest.delete(url())

        // THEN
        verify(marketplaceAccessApi).deleteProduct(PRODUCT_ID)
    }

    @Test
    fun notOwner() {
        // GIVEN
        val account = Fixtures.createAccount(id = ACCOUNT_ID, business = true, storeId = STORE_ID + 1)
        doReturn(GetAccountResponse(account)).whenever(membershipAccessApi).getAccount(any())

        val product = Fixtures.createProduct(id = PRODUCT_ID, storeId = STORE_ID)
        doReturn(GetProductResponse(product)).whenever(marketplaceAccessApi).getProduct(any())

        // WHEN
        val ex = assertThrows<HttpClientErrorException> {
            rest.delete(url())
        }

        // THEN
        assertEquals(HttpStatus.FORBIDDEN, ex.statusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.PRODUCT_NOT_OWNER.urn, response.error.code)
    }
}
