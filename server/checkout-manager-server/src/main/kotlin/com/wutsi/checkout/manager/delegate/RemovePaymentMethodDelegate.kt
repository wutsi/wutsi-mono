package com.wutsi.checkout.manager.delegate

import com.wutsi.checkout.access.CheckoutAccessApi
import com.wutsi.checkout.access.dto.PaymentMethod
import com.wutsi.checkout.access.dto.UpdatePaymentMethodStatusRequest
import com.wutsi.checkout.manager.util.SecurityUtil
import com.wutsi.enums.PaymentMethodStatus
import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.access.dto.Account
import com.wutsi.regulation.RuleSet
import com.wutsi.regulation.rule.AccountShouldBeActiveRule
import com.wutsi.regulation.rule.AccountShouldBeOwnerOfPaymentMethodRule
import org.springframework.stereotype.Service

@Service
public class RemovePaymentMethodDelegate(
    private val checkoutAccessApi: CheckoutAccessApi,
    private val membershipAccessApi: MembershipAccessApi,
) {
    public fun invoke(token: String) {
        val account = membershipAccessApi.getAccount(SecurityUtil.getAccountId()).account
        val paymentMethod = checkoutAccessApi.getPaymentMethod(token).paymentMethod
        validate(account, paymentMethod)
    }

    private fun validate(account: Account, paymentMethod: PaymentMethod) =
        RuleSet(
            rules = listOf(
                AccountShouldBeActiveRule(account),
                AccountShouldBeOwnerOfPaymentMethodRule(account, paymentMethod),
            ),
        ).check()

    private fun remove(token: String) {
        checkoutAccessApi.updatePaymentMethodStatus(
            token = token,
            request = UpdatePaymentMethodStatusRequest(
                status = PaymentMethodStatus.INACTIVE.name,
            ),
        )
    }
}
