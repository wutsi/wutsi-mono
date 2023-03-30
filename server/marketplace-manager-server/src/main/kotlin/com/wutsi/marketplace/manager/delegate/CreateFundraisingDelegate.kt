package com.wutsi.marketplace.manager.delegate

import com.wutsi.checkout.manager.util.SecurityUtil
import com.wutsi.marketplace.access.MarketplaceAccessApi
import com.wutsi.marketplace.manager.event.CreateFundraisingEventPayload
import com.wutsi.marketplace.manager.event.EventHander
import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.access.dto.Account
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.regulation.RegulationEngine
import com.wutsi.regulation.RuleSet
import com.wutsi.regulation.rule.AccountShouldBeActiveRule
import com.wutsi.regulation.rule.AccountShouldBeBusinessRule
import com.wutsi.regulation.rule.CountryShouldSupportFundraisingRule
import org.springframework.stereotype.Service

@Service
public class CreateFundraisingDelegate(
    private val marketplaceAccessApi: MarketplaceAccessApi,
    private val membershipAccessApi: MembershipAccessApi,
    private val regulationEngine: RegulationEngine,
    private val eventStream: EventStream,
) {
    fun invoke() {
        val account = membershipAccessApi.getAccount(SecurityUtil.getAccountId()).account

        validate(account)
        val fundraisingId = createFundraising(account)
        setAccountFundraising(account.id, fundraisingId)
    }

    private fun validate(account: Account) =
        RuleSet(
            listOf(
                AccountShouldBeActiveRule(account),
                AccountShouldBeBusinessRule(account),
                CountryShouldSupportFundraisingRule(account, regulationEngine),
            ),
        ).check()

    private fun createFundraising(account: Account): Long =
        marketplaceAccessApi.createFundraising(
            request = com.wutsi.marketplace.access.dto.CreateFundraisingRequest(
                accountId = account.id,
                businessId = account.businessId!!,
                currency = regulationEngine.country(account.country).currency,
            ),
        ).fundraisingId

    private fun setAccountFundraising(accountId: Long, fundraisingId: Long) =
        eventStream.enqueue(
            EventHander.EVENT_SET_ACCOUNT_FUNDRAISING,
            CreateFundraisingEventPayload(accountId, fundraisingId),
        )
}
