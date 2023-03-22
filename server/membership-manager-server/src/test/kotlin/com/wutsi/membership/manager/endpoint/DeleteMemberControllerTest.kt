package com.wutsi.membership.manager.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.checkout.access.dto.PaymentMethodSummary
import com.wutsi.checkout.access.dto.SearchPaymentMethodResponse
import com.wutsi.checkout.access.dto.UpdatePaymentMethodStatusRequest
import com.wutsi.enums.AccountStatus
import com.wutsi.enums.PaymentMethodStatus
import com.wutsi.membership.access.dto.GetAccountResponse
import com.wutsi.membership.access.dto.UpdateAccountStatusRequest
import com.wutsi.membership.manager.Fixtures
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import java.util.UUID

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DeleteMemberControllerTest : AbstractSecuredController2Test() {
    @LocalServerPort
    val port: Int = 0

    @Test
    fun suspend() {
        // GIVEN
        val account = Fixtures.createAccount()
        doReturn(GetAccountResponse(account)).whenever(membershipAccess).getAccount(any())

        val token = UUID.randomUUID().toString()
        doReturn(
            SearchPaymentMethodResponse(
                paymentMethods = listOf(
                    PaymentMethodSummary(token = token),
                ),
            ),
        ).whenever(checkoutAccessApi).searchPaymentMethod(any())

        // WHEN
        rest.delete(url())

        // THEN
        verify(securityManagerApi).deletePassword()

        Thread.sleep(10000) // Wait for async processing
        verify(membershipAccess).updateAccountStatus(
            ACCOUNT_ID,
            UpdateAccountStatusRequest(
                status = AccountStatus.INACTIVE.name,
            ),
        )

        verify(checkoutAccessApi).updatePaymentMethodStatus(
            token,
            UpdatePaymentMethodStatusRequest((PaymentMethodStatus.INACTIVE.name)),
        )
    }

    private fun url() = "http://localhost:$port/v1/members"
}
