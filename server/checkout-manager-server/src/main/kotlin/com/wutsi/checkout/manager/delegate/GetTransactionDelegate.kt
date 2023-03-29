package com.wutsi.checkout.manager.delegate

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.checkout.access.CheckoutAccessApi
import com.wutsi.checkout.manager.dto.GetTransactionResponse
import com.wutsi.checkout.manager.dto.Transaction
import com.wutsi.checkout.manager.event.EventHander
import com.wutsi.checkout.manager.event.TransactionEventPayload
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.platform.payment.core.Status
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class GetTransactionDelegate(
    private val checkoutAccessApi: CheckoutAccessApi,
    private val eventStream: EventStream,
    private val objectMapper: ObjectMapper,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(GetTransactionDelegate::class.java)
    }

    fun invoke(id: String, sync: Boolean?): GetTransactionResponse {
        // Sync the transaction
        if (sync == true) {
            try {
                val response = checkoutAccessApi.syncTransactionStatus(id)
                if (response.status == Status.SUCCESSFUL.name) {
                    eventStream.enqueue(EventHander.EVENT_HANDLE_SUCCESSFUL_TRANSACTION, TransactionEventPayload(id))
                }
            } catch (ex: Exception) {
                LOGGER.warn("Unable to sync the transaction", ex)
            }
        }

        // Return the transaction
        val transaction = checkoutAccessApi.getTransaction(id).transaction
        return GetTransactionResponse(
            transaction = objectMapper.readValue(
                objectMapper.writeValueAsString(transaction),
                Transaction::class.java,
            ),
        )
    }
}
