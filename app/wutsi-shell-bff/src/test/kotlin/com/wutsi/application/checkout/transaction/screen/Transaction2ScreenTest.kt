package com.wutsi.application.checkout.transaction.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Fixtures
import com.wutsi.application.Page
import com.wutsi.checkout.manager.dto.GetOrderResponse
import com.wutsi.checkout.manager.dto.GetTransactionResponse
import com.wutsi.enums.TransactionType
import com.wutsi.platform.payment.core.ErrorCode
import com.wutsi.platform.payment.core.Status
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

internal class Transaction2ScreenTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private fun url(id: String) = "http://localhost:$port${Page.getTransactionUrl()}?id=$id"

    @Test
    fun charge() {
        // GIVEN
        val tx = Fixtures.createTransaction(
            id = "111",
            type = TransactionType.CHARGE,
            status = Status.SUCCESSFUL,
            orderId = "2222",
        )
        doReturn(GetTransactionResponse(tx)).whenever(checkoutManagerApi).getTransaction(any(), anyOrNull())

        val order = Fixtures.createOrder(id = tx.orderId!!)
        doReturn(GetOrderResponse(order)).whenever(checkoutManagerApi).getOrder(any())

        // THEN/WHEN
        assertEndpointEquals("/checkout/transaction/screens/transaction-charge.json", url(tx.id))
    }

    @Test
    fun cashout() {
        // GIVEN
        val tx = Fixtures.createTransaction(
            id = "111",
            type = TransactionType.CASHOUT,
            status = Status.FAILED,
            orderId = null,
            error = ErrorCode.UNEXPECTED_ERROR,
        )
        doReturn(GetTransactionResponse(tx)).whenever(checkoutManagerApi).getTransaction(any(), anyOrNull())

        // THEN/WHEN
        assertEndpointEquals("/checkout/transaction/screens/transaction-cashout.json", url(tx.id))
    }
}
