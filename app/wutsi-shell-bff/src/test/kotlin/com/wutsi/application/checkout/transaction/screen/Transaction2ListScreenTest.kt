package com.wutsi.application.checkout.transaction.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Fixtures
import com.wutsi.application.Page
import com.wutsi.checkout.manager.dto.SearchTransactionResponse
import com.wutsi.enums.TransactionType
import com.wutsi.membership.manager.dto.GetMemberResponse
import com.wutsi.platform.payment.core.Status
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

internal class Transaction2ListScreenTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private val txs = listOf(
        Fixtures.createTransactionSummary(
            "100",
            type = TransactionType.CHARGE,
            status = Status.SUCCESSFUL,
            orderId = "1111",
        ),
        Fixtures.createTransactionSummary(
            "200",
            type = TransactionType.CHARGE,
            status = Status.SUCCESSFUL,
            orderId = "2222",
        ),
        Fixtures.createTransactionSummary(
            "300",
            type = TransactionType.CASHOUT,
            status = Status.FAILED,
        ),
    )

    private fun url() = "http://localhost:$port${Page.getTransactionListUrl()}"

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(SearchTransactionResponse(txs)).whenever(checkoutManagerApi).searchTransaction(any())
    }

    @Test
    fun business() {
        // GIVEN
        val member = Fixtures.createMember(id = MEMBER_ID, business = true, businessId = 111)
        doReturn(GetMemberResponse(member)).whenever(membershipManagerApi).getMember(any())

        // THEN/WHEN
        assertEndpointEquals("/checkout/transaction/screens/list-business.json", url())
    }

    @Test
    fun personal() {
        // GIVEN
        val member = Fixtures.createMember(id = MEMBER_ID, business = false, businessId = null)
        doReturn(GetMemberResponse(member)).whenever(membershipManagerApi).getMember(any())

        // THEN/WHEN
        assertEndpointEquals("/checkout/transaction/screens/list-personal.json", url())
    }
}
