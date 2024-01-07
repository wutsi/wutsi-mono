package com.wutsi.blog.app.page.payment

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.transaction.dto.PaymentMethodType
import com.wutsi.blog.transaction.dto.SearchTransactionResponse
import com.wutsi.blog.transaction.dto.TransactionSummary
import com.wutsi.blog.transaction.dto.TransactionType
import com.wutsi.blog.user.dto.SearchUserResponse
import com.wutsi.blog.user.dto.UserSummary
import com.wutsi.platform.payment.core.ErrorCode
import com.wutsi.platform.payment.core.Status
import org.junit.jupiter.api.BeforeEach
import java.util.Date
import kotlin.test.Test

class TransactionControllerTest : SeleniumTestSupport() {
    companion object {
        const val BLOG_ID = 100L
        const val WALLET_ID = "111"
    }

    val batch1 = (0..TransactionController.LIMIT).map {
        createTransactionSummary(amount = (1 + it) * 10L)
    }
    val batch2 = listOf(
        createTransactionSummary(amount = 1000L),
        createTransactionSummary(amount = 1400L),
        createTransactionSummary(amount = 2000L, error = ErrorCode.DECLINED, status = Status.FAILED),
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        val user = setupLoggedInUser(userId = BLOG_ID, walletId = WALLET_ID)

        doReturn(
            SearchUserResponse(
                listOf(
                    UserSummary(
                        id = BLOG_ID,
                        name = user.name,
                    )
                )
            )
        ).whenever(userBackend).search(any())
    }

    @Test
    fun index() {
        doReturn(SearchTransactionResponse(batch1))
            .whenever(transactionBackend).search(any())

        navigate(url("/me/transactions"))
        assertCurrentPageIs(PageName.TRANSACTIONS)
        assertElementPresent("#transaction-load-more")
    }

    @Test
    fun loadMore() {
        doReturn(SearchTransactionResponse(batch1))
            .whenever(transactionBackend).search(any())

        navigate(url("/me/transactions"))
        assertCurrentPageIs(PageName.TRANSACTIONS)

        doReturn(SearchTransactionResponse(batch2))
            .whenever(transactionBackend).search(any())
        scrollToBottom()
        click("#transaction-load-more", 1000)
        assertElementNotPresent("#transaction-load-more")
    }

    private fun createTransactionSummary(
        type: TransactionType = TransactionType.DONATION,
        amount: Long = 1000L,
        fees: Long = 10,
        error: ErrorCode? = null,
        status: Status = Status.SUCCESSFUL,
    ) = TransactionSummary(
        type = type,
        currency = "XFA",
        userId = BLOG_ID,
        errorCode = error?.name,
        amount = amount,
        fees = fees,
        net = amount - fees,
        walletId = WALLET_ID,
        paymentMethodOwner = "Ray Sponsible",
        paymentMethodType = PaymentMethodType.MOBILE_MONEY,
        creationDateTime = Date(),
        status = status,
    )
}
