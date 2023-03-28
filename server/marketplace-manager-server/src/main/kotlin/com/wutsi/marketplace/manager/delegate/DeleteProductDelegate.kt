package com.wutsi.marketplace.manager.delegate

import com.wutsi.checkout.manager.util.SecurityUtil
import com.wutsi.marketplace.access.MarketplaceAccessApi
import com.wutsi.marketplace.access.dto.Product
import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.access.dto.Account
import com.wutsi.workflow.RuleSet
import com.wutsi.workflow.rule.account.AccountShouldBeOwnerOfProductRule
import org.springframework.stereotype.Service

@Service
public class DeleteProductDelegate(
    private val marketplaceAccessApi: MarketplaceAccessApi,
    private val membershipAccessApi: MembershipAccessApi,
) {
    public fun invoke(id: Long) {
        val accoumt = findAccount(SecurityUtil.getAccountId())
        val product = findProduct(id)
        validate(accoumt, product)
        delete(id)
    }

    private fun validate(account: Account, product: Product) =
        RuleSet(
            listOf(
                AccountShouldBeOwnerOfProductRule(account, product),
            ),
        ).check()

    private fun delete(id: Long) {
        marketplaceAccessApi.deleteProduct(id)
    }

    protected fun findProduct(productId: Long): Product =
        marketplaceAccessApi.getProduct(productId).product

    protected fun findAccount(accountId: Long): Account =
        membershipAccessApi.getAccount(accountId).account
}
