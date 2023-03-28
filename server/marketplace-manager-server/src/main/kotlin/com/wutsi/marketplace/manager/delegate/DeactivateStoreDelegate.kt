package com.wutsi.marketplace.manager.delegate

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.checkout.manager.util.SecurityUtil
import com.wutsi.enums.StoreStatus
import com.wutsi.marketplace.access.MarketplaceAccessApi
import com.wutsi.marketplace.access.dto.UpdateStoreStatusRequest
import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.access.dto.Account
import com.wutsi.membership.access.dto.UpdateAccountAttributeRequest
import com.wutsi.platform.core.stream.Event
import com.wutsi.platform.core.stream.EventStream
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class DeactivateStoreDelegate(
    private val marketplaceAccessApi: MarketplaceAccessApi,
    private val membershipAccessApi: MembershipAccessApi,
    private val eventStream: EventStream,
    private val objectMapper: ObjectMapper,
) {
    companion object {
        const val EVENT_RESET_ACCOUNT_STORE = "urn:wutsi:event:marketplace-manager:reset-account-store"
    }

    fun invoke() {
        val accountId = SecurityUtil.getAccountId()
        val account = getAccount(accountId)
        if (account.storeId != null) {
            deactivateStore(account)
            resetAccountStore(accountId)
        }
    }

    @EventListener
    fun onEvent(event: Event) {
        when (event.type) {
            EVENT_RESET_ACCOUNT_STORE ->
                doResetAccountStore(accountId = toAccountId(event))
            else -> {}
        }
    }

    protected fun toAccountId(event: Event) =
        objectMapper.readValue(event.payload, DeactivateStoreEventPayload::class.java).accountId

    private fun deactivateStore(account: Account) =
        marketplaceAccessApi.updateStoreStatus(
            id = account.storeId!!,
            request = UpdateStoreStatusRequest(
                status = StoreStatus.INACTIVE.name,
            ),
        )

    private fun resetAccountStore(accountId: Long) {
        eventStream.enqueue(EVENT_RESET_ACCOUNT_STORE, DeactivateStoreEventPayload(accountId))
    }

    private fun doResetAccountStore(accountId: Long) {
        membershipAccessApi.updateAccountAttribute(
            id = accountId,
            request = UpdateAccountAttributeRequest("store-id", null),
        )
    }

    private fun getAccount(accountId: Long): Account =
        membershipAccessApi.getAccount(accountId).account
}

data class DeactivateStoreEventPayload(val accountId: Long = -1)
