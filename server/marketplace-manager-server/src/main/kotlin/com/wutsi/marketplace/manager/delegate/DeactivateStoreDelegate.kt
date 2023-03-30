package com.wutsi.marketplace.manager.delegate

import com.wutsi.checkout.manager.util.SecurityUtil
import com.wutsi.enums.StoreStatus
import com.wutsi.marketplace.access.MarketplaceAccessApi
import com.wutsi.marketplace.access.dto.UpdateStoreStatusRequest
import com.wutsi.marketplace.manager.event.EventHander.Companion.EVENT_RESET_ACCOUNT_STORE
import com.wutsi.marketplace.manager.event.ResetAccountStoreEventPayload
import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.access.dto.Account
import com.wutsi.platform.core.stream.EventStream
import org.springframework.stereotype.Service

@Service
class DeactivateStoreDelegate(
    private val marketplaceAccessApi: MarketplaceAccessApi,
    private val membershipAccessApi: MembershipAccessApi,
    private val eventStream: EventStream,
) {
    fun invoke() {
        val accountId = SecurityUtil.getAccountId()
        val account = membershipAccessApi.getAccount(accountId).account
        if (account.storeId != null) {
            deactivateStore(account)
            resetAccountStore(account)
        }
    }

    private fun deactivateStore(account: Account) =
        marketplaceAccessApi.updateStoreStatus(
            id = account.storeId!!,
            request = UpdateStoreStatusRequest(
                status = StoreStatus.INACTIVE.name,
            ),
        )

    private fun resetAccountStore(account: Account) =
        eventStream.enqueue(EVENT_RESET_ACCOUNT_STORE, ResetAccountStoreEventPayload(account.storeId!!))
}
