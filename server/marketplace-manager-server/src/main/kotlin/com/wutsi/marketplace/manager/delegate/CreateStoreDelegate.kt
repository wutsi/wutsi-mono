package com.wutsi.marketplace.manager.delegate

import com.wutsi.checkout.manager.util.SecurityUtil
import com.wutsi.marketplace.access.MarketplaceAccessApi
import com.wutsi.marketplace.access.dto.CreateStoreRequest
import com.wutsi.marketplace.manager.event.CreateStoreEventPayload
import com.wutsi.marketplace.manager.event.EventHander.Companion.EVENT_SEND_WELCOME_EMAIL
import com.wutsi.marketplace.manager.event.EventHander.Companion.EVENT_SET_ACCOUNT_STORE
import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.access.dto.Account
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.regulation.RegulationEngine
import com.wutsi.regulation.RuleSet
import com.wutsi.regulation.rule.AccountShouldBeActiveRule
import com.wutsi.regulation.rule.AccountShouldBeBusinessRule
import com.wutsi.regulation.rule.CountryShouldSupportStoreRule
import org.springframework.stereotype.Service

@Service
class CreateStoreDelegate(
    private val regulationEngine: RegulationEngine,
    private val marketplaceAccessApi: MarketplaceAccessApi,
    private val membershipAccessApi: MembershipAccessApi,
    private val eventStream: EventStream,
) {
    fun invoke() {
        val account = membershipAccessApi.getAccount(SecurityUtil.getAccountId()).account

        validate(account)
        val storeId = createStore(account)
        setAccountStore(account.id, storeId)
        sendWelcomeEmail(account.id, storeId)
    }

    private fun validate(account: Account) =
        RuleSet(
            listOf(
                AccountShouldBeActiveRule(account),
                AccountShouldBeBusinessRule(account),
                CountryShouldSupportStoreRule(account, regulationEngine),
            ),
        ).check()

    private fun createStore(account: Account): Long =
        marketplaceAccessApi.createStore(
            request = CreateStoreRequest(
                accountId = account.id,
                businessId = account.businessId!!,
                currency = regulationEngine.country(account.country).currency,
            ),
        ).storeId

    private fun setAccountStore(accountId: Long, storeId: Long) =
        eventStream.enqueue(EVENT_SET_ACCOUNT_STORE, CreateStoreEventPayload(accountId, storeId))

    private fun sendWelcomeEmail(accountId: Long, storeId: Long) =
        eventStream.enqueue(EVENT_SEND_WELCOME_EMAIL, CreateStoreEventPayload(accountId, storeId))
}
