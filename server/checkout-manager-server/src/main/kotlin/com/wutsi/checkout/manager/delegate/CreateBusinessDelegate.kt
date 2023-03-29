package com.wutsi.checkout.manager.delegate

import com.wutsi.checkout.access.CheckoutAccessApi
import com.wutsi.checkout.manager.dto.CreateBusinessRequest
import com.wutsi.checkout.manager.event.EventHander.Companion.EVENT_SET_ACCOUNT_BUSINESS
import com.wutsi.checkout.manager.event.SetAccountBusinessEventPayload
import com.wutsi.checkout.manager.util.SecurityUtil
import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.access.dto.Account
import com.wutsi.membership.access.dto.EnableBusinessRequest
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.regulation.RegulationEngine
import com.wutsi.regulation.RuleSet
import com.wutsi.regulation.rule.AccountShouldBeActiveRule
import com.wutsi.regulation.rule.CountryShouldSupportBusinessAccountRule
import org.springframework.stereotype.Service

@Service
public class CreateBusinessDelegate(
    private val checkoutAccessApi: CheckoutAccessApi,
    private val membershipAccessApi: MembershipAccessApi,
    private val eventStream: EventStream,
    private val regulationEngine: RegulationEngine,
) {
    public fun invoke(request: CreateBusinessRequest) {
        val account = getCurrentAccount(SecurityUtil.getAccountId())
        validate(account)

        val businessId = createBusiness(account)
        enableBusinessAccount(account, request)
        setAccountBusinessId(account.id, businessId)
    }

    private fun getCurrentAccount(accountId: Long): Account =
        membershipAccessApi.getAccount(accountId).account

    private fun validate(account: Account) =
        RuleSet(
            listOf(
                AccountShouldBeActiveRule(account),
                CountryShouldSupportBusinessAccountRule(account, regulationEngine),
            ),
        ).check()

    private fun enableBusinessAccount(account: Account, request: CreateBusinessRequest) =
        membershipAccessApi.enableBusiness(
            id = account.id,
            request = EnableBusinessRequest(
                displayName = request.displayName,
                country = account.country,
                cityId = request.cityId,
                categoryId = request.categoryId,
                biography = request.biography,
                whatsapp = request.whatsapp,
                email = request.email,
            ),
        )

    private fun createBusiness(account: Account): Long =
        checkoutAccessApi.createBusiness(
            request = com.wutsi.checkout.access.dto.CreateBusinessRequest(
                accountId = account.id,
                country = account.country,
                currency = regulationEngine.country(account.country).currency,
            ),
        ).businessId

    private fun setAccountBusinessId(accountId: Long, businessId: Long) =
        eventStream.enqueue(EVENT_SET_ACCOUNT_BUSINESS, SetAccountBusinessEventPayload(accountId, businessId))
}
