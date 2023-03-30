package com.wutsi.marketplace.manager.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.marketplace.manager.service.WelcomeEmailMailer
import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.access.dto.UpdateAccountAttributeRequest
import com.wutsi.platform.core.stream.Event
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
public class EventHander(
    private val objectMapper: ObjectMapper,
    private val membershipAccessApi: MembershipAccessApi,
    private val welcomeEmailMailer: WelcomeEmailMailer,
) {
    companion object {
        const val EVENT_SET_ACCOUNT_STORE = "urn:wutsi:event:marketplace-manager:set-account-store"
        const val EVENT_SET_ACCOUNT_FUNDRAISING = "urn:wutsi:event:marketplace-manager:set-account-fundraising"
        const val EVENT_SEND_WELCOME_EMAIL = "urn:wutsi:event:marketplace-manager:send-welcome-email"
        const val EVENT_RESET_ACCOUNT_STORE = "urn:wutsi:event:marketplace-manager:reset-account-store"
    }

    @EventListener
    fun onEvent(event: Event) {
        when (event.type) {
            EVENT_SET_ACCOUNT_STORE -> doSetAccountStore(event)
            EVENT_SET_ACCOUNT_FUNDRAISING -> doSetAccountFundraising(event)
            EVENT_SEND_WELCOME_EMAIL -> doSendWelcomeEmail(event)
            EVENT_RESET_ACCOUNT_STORE -> doResetAccountStore(event)
        }
    }

    private fun doSetAccountStore(event: Event) {
        val payload = objectMapper.readValue(event.payload, CreateStoreEventPayload::class.java)
        membershipAccessApi.updateAccountAttribute(
            id = payload.accountId,
            request = UpdateAccountAttributeRequest("store-id", payload.storeId.toString()),
        )
    }

    private fun doResetAccountStore(event: Event) {
        val payload = objectMapper.readValue(event.payload, ResetAccountStoreEventPayload::class.java)
        membershipAccessApi.updateAccountAttribute(
            id = payload.accountId,
            request = UpdateAccountAttributeRequest("store-id", null),
        )
    }

    private fun doSetAccountFundraising(event: Event) {
        val payload = objectMapper.readValue(event.payload, CreateFundraisingEventPayload::class.java)
        membershipAccessApi.updateAccountAttribute(
            id = payload.accountId,
            request = UpdateAccountAttributeRequest("fundraising-id", payload.fundraisingId.toString()),
        )
    }

    private fun doSendWelcomeEmail(event: Event) {
        val payload = objectMapper.readValue(event.payload, CreateStoreEventPayload::class.java)
        welcomeEmailMailer.send(payload.accountId)
    }
}
