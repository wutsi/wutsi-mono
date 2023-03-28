package com.wutsi.marketplace.manager.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.marketplace.access.dto.GetStoreResponse
import com.wutsi.marketplace.manager.Fixtures
import com.wutsi.marketplace.manager.dto.CreateDiscountRequest
import com.wutsi.marketplace.manager.dto.CreateDiscountResponse
import com.wutsi.membership.access.dto.GetAccountResponse
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CreateDiscountControllerTest : AbstractSecuredControllerTest() {
    @LocalServerPort
    val port: Int = 0

    private fun url() = "http://localhost:$port/v1/discounts"

    @Test
    public fun invoke() {
        // GIVEN
        val account = Fixtures.createAccount(id = ACCOUNT_ID, business = true, storeId = STORE_ID)
        doReturn(GetAccountResponse(account)).whenever(membershipAccessApi).getAccount(any())

        val store = Fixtures.createStore(id = STORE_ID, accountId = ACCOUNT_ID)
        doReturn(GetStoreResponse(store)).whenever(marketplaceAccessApi).getStore(any())

        doReturn(com.wutsi.marketplace.access.dto.CreateDiscountResponse(111))
            .whenever(marketplaceAccessApi)
            .createDiscount(
                any(),
            )

        // WHEN
        val request = CreateDiscountRequest(
            starts = OffsetDateTime.now(ZoneOffset.UTC),
            ends = null,
            rate = 10,
            allProducts = true,
            type = "SALES",
            name = "FOO",
        )
        val response = rest.postForEntity(url(), request, CreateDiscountResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        verify(marketplaceAccessApi).createDiscount(
            request = com.wutsi.marketplace.access.dto.CreateDiscountRequest(
                storeId = account.storeId!!,
                name = request.name,
                type = request.type,
                starts = request.starts,
                ends = request.ends,
                rate = request.rate,
                allProducts = request.allProducts,
            ),
        )
    }
}
