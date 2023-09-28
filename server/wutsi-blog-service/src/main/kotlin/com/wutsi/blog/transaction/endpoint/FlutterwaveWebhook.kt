package com.wutsi.blog.transaction.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.event.EventType.SUBMIT_TRANSACTION_NOTIFICATION_COMMAND
import com.wutsi.blog.transaction.dto.SubmitTransactionNotificationCommand
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.platform.payment.provider.flutterwave.model.FWWebhookRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * See https://developer.flutterwave.com/docs/integration-guides/webhooks
 */
@RestController
@RequestMapping("/webhooks/flutterwave")
class FlutterwaveWebhook(
    private val logger: KVLogger,
    private val objectMapper: ObjectMapper,
    private val eventStream: EventStream,
    @Value("\${wutsi.platform.payment.flutterwave.secret-hash}") private val secretHash: String,
) {
    @PostMapping
    fun notify(
        @RequestBody request: FWWebhookRequest,
        @RequestHeader(name = "verif-hash", required = false) verifHash: String? = null,
    ) {
        log(request, verifHash)

        // Verify the hash
        if (secretHash != verifHash) {
            logger.add("validation_error", "bad_secret_hash")
            return // This is not coming from Flutterwave - silently ignore it
        }

        // Handle the request
        handleNotification(request)
    }

    private fun handleNotification(request: FWWebhookRequest) {
        val transactionId = request.data.tx_ref ?: request.data.reference
        if (transactionId != null) {
            eventStream.enqueue(
                SUBMIT_TRANSACTION_NOTIFICATION_COMMAND,
                payload = SubmitTransactionNotificationCommand(
                    transactionId = transactionId,
                    message = objectMapper.writeValueAsString(request),
                ),
            )
        }
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
        logger.add("request_data_reference", request.data.reference)
        logger.add("request_data_bank_code", request.data.bank_code)
        logger.add("request_data_bank_name", request.data.bank_name)
        logger.add("request_data_full_name", request.data.full_name)
        logger.add("request_data_account_number", request.data.account_number)
        logger.add("request_data_complete_message", request.data.complete_message)
        logger.add("request_data_created_at", request.data.created_at)
        logger.add("request_data_charge_type", request.data.charge_type)
        logger.add("request_data_fraud_status", request.data.fraud_status)

        verifHash?.let { logger.add("header_verif_hash", "******") }
    }
}
