package com.wutsi.blog.transaction.endpoint

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.transaction.dto.SearchTransactionRequest
import com.wutsi.blog.transaction.dto.SearchTransactionResponse
import com.wutsi.platform.payment.GatewayType
import com.wutsi.platform.payment.core.Status
import com.wutsi.platform.payment.provider.flutterwave.Flutterwave
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(value = ["/db/clean.sql", "/db/transaction/SearchTransactionQuery.sql"])
class SearchTransactionQueryTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @MockBean
    private lateinit var flutterwave: Flutterwave

    @BeforeEach
    fun setUp() {
        doReturn(GatewayType.FLUTTERWAVE).whenever(flutterwave).getType()
    }

    @Test
    fun searchByWalletAndStatus() {
        // WHEN
        val request = SearchTransactionRequest(
            walletId = "1",
            statuses = listOf(Status.SUCCESSFUL),
        )
        val result =
            rest.postForEntity("/v1/transactions/queries/search", request, SearchTransactionResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, result.statusCode)

        val tx = result.body!!.transactions
        assertEquals(2, tx.size)
        assertEquals("102", tx[0].id)
        assertEquals("101", tx[1].id)
    }

    @Test
    fun searchByUserId() {
        // WHEN
        val request = SearchTransactionRequest(
            userId = 3
        )
        val result =
            rest.postForEntity("/v1/transactions/queries/search", request, SearchTransactionResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, result.statusCode)

        val txs = result.body!!.transactions
        assertEquals(2, txs.size)
        txs.forEach {
            assertEquals(3L, it.userId)
        }
    }

    @Test
    fun searchByProductId() {
        // WHEN
        val request = SearchTransactionRequest(
            productIds = listOf(101L)
        )
        val result =
            rest.postForEntity("/v1/transactions/queries/search", request, SearchTransactionResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, result.statusCode)

        val txs = result.body!!.transactions
        assertEquals(3, txs.size)
        txs.forEach {
            assertEquals(101L, it.productId)
        }
    }

    @Test
    fun searchByStoreId() {
        // WHEN
        val request = SearchTransactionRequest(
            storeId = "1"
        )
        val result =
            rest.postForEntity("/v1/transactions/queries/search", request, SearchTransactionResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, result.statusCode)

        val txs = result.body!!.transactions
        assertEquals(2, txs.size)
        txs.forEach {
            assertEquals(request.storeId, it.storeId)
        }
    }
}
