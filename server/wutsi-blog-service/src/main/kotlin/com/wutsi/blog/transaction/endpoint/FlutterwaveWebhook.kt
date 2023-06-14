package com.wutsi.blog.transaction.endpoint

import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.platform.payment.provider.flutterwave.model.FWWebhookRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader

/**
 * See https://developer.flutterwave.com/docs/integration-guides/webhooks
 */
//@RestController
//@RequestMapping("/flutterwave")
public class FlutterwaveController(
    private val logger: KVLogger,
    private val eventStream: EventStream,
    @Value("\${wutsi.flutterwave.secret-hash}") private val secretHash: String,
) {
    @PostMapping("/webhook")
    public fun invoke(
        @RequestBody request: FWWebhookRequest,
        @RequestHeader(name = "verif-hash", required = false) verifHash: String? = null,
    ) {
        log(request, verifHash)

        // Verify the hash
        if (secretHash != verifHash) {
            logger.add("hash-valid", false)
            return // This is not coming from Flutterwave - silently ignore it
        }
        logger.add("hash-valid", true)

        // Handle the request
        handleNotification(request)
    }

    private fun handleNotification(request: FWWebhookRequest) {
//        val transactionId = request.data.tx_ref
//        if (transactionId != null) {
//            eventStream.enqueue(EVENT_HANDLE_SUCCESSFUL_TRANSACTION, TransactionEventPayload(transactionId))
//        }
    }

    private fun log(request: FWWebhookRequest, verifHash: String?) {
        logger.add("request_event", request.event)
        logger.add("request_data_id", request.data.id)
        logger.add("request_data_status", request.data.status)
        logger.add("request_data_flw_ref", request.data.flw_ref)
        logger.add("request_data_tx_ref", request.data.tx_ref)
        logger.add("request_data_app_fee", request.data.app_fee)
        logger.add("request_data_fee", request.data.fee)
        logger.add("request_data_amount", request.data.amount)
        logger.add("request_data_currency", request.data.currency)

        verifHash?.let { logger.add("header_verif_hash", "******") }
    }
}
