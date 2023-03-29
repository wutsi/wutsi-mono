package com.wutsi.marketplace.manager.delegate

import com.wutsi.checkout.manager.util.SecurityUtil
import com.wutsi.marketplace.access.MarketplaceAccessApi
import com.wutsi.marketplace.access.dto.Discount
import com.wutsi.marketplace.manager.dto.CreateDiscountRequest
import com.wutsi.marketplace.manager.dto.CreateDiscountResponse
import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.access.dto.Account
import com.wutsi.regulation.RuleSet
import com.wutsi.regulation.rule.AccountShouldHaveStoreRule
import com.wutsi.regulation.rule.DiscountShouldHaveStartDateBeforeEndDateRule
import org.springframework.stereotype.Service

@Service
public class CreateDiscountDelegate(
    private val marketplaceAccessApi: MarketplaceAccessApi,
    private val membershipAccessApi: MembershipAccessApi,
) {
    public fun invoke(request: CreateDiscountRequest): CreateDiscountResponse {
        val account = findAccount()
        validate(account, request)
        val discountId = createDiscount(account, request)
        return CreateDiscountResponse(discountId)
    }

    private fun validate(account: Account, request: CreateDiscountRequest) =
        RuleSet(
            listOf(
                AccountShouldHaveStoreRule(account),
                DiscountShouldHaveStartDateBeforeEndDateRule(
                    Discount(
                        starts = request.starts,
                        ends = request.ends,
                        type = request.type,
                    ),
                ),
            ),
        ).check()

    private fun createDiscount(account: Account, request: CreateDiscountRequest): Long =
        marketplaceAccessApi.createDiscount(
            request = com.wutsi.marketplace.access.dto.CreateDiscountRequest(
                storeId = account.storeId!!,
                name = request.name,
                starts = request.starts,
                ends = request.ends,
                allProducts = request.allProducts,
                rate = request.rate,
                type = request.type,
            ),
        ).discountId

    private fun findAccount(): Account =
        membershipAccessApi.getAccount(SecurityUtil.getAccountId()).account
}
