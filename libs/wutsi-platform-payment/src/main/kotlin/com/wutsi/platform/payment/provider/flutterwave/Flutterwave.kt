package com.wutsi.platform.payment.provider.flutterwave

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.platform.payment.Gateway
import com.wutsi.platform.payment.GatewayType
import com.wutsi.platform.payment.PaymentException
import com.wutsi.platform.payment.core.Error
import com.wutsi.platform.payment.core.ErrorCode
import com.wutsi.platform.payment.core.Http
import com.wutsi.platform.payment.core.HttpException
import com.wutsi.platform.payment.core.Money
import com.wutsi.platform.payment.core.Status
import com.wutsi.platform.payment.model.CapturePaymentResponse
import com.wutsi.platform.payment.model.CreatePaymentRequest
import com.wutsi.platform.payment.model.CreatePaymentResponse
import com.wutsi.platform.payment.model.CreateTransferRequest
import com.wutsi.platform.payment.model.CreateTransferResponse
import com.wutsi.platform.payment.model.GetPaymentResponse
import com.wutsi.platform.payment.model.GetTransferResponse
import com.wutsi.platform.payment.model.Party
import com.wutsi.platform.payment.provider.flutterwave.Flutterwave.Companion.toPaymentException
import com.wutsi.platform.payment.provider.flutterwave.model.FWChargeRequest
import com.wutsi.platform.payment.provider.flutterwave.model.FWResponse
import com.wutsi.platform.payment.provider.flutterwave.model.FWTransferRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.TimeZone
import java.util.UUID

open class Flutterwave(
    private val http: Http,
    private val secretKey: String,
    private val testMode: Boolean,
) : Gateway {
    companion object {
        const val BANK_FMM = "FMM"
        const val BASE_URI = "https://api.flutterwave.com/v3"
        const val META_WALLET_ID = "wutsi_wallet_id"
        const val META_PAYER_ID = "wutsi_payer_id"
        const val META_PAYEE_ID = "wutsi_payee_id"
        const val MAX_RETRIES = 3
        const val RETRY_DELAY_MILLIS = 1000L
        const val TEST_MODE_BANK = "044"
        val LOGGER: Logger = LoggerFactory.getLogger(Flutterwave::class.java)

        fun toPaymentException(response: FWResponse, type: String, ex: Throwable? = null) = PaymentException(
            error = Error(
                transactionId = response.data?.id?.toString() ?: "",
                code = toErrorCode(response, type),
                supplierErrorCode = response.code,
                message = toErrorMessage(response),
                errorId = response.error_id,
            ),
            ex,
        )

        private fun toErrorMessage(response: FWResponse): String? =
            when (response.status.lowercase()) {
                "error" -> response.message
                else -> response.data?.processor_response
            }

        /**
         * See https://developer.flutterwave.com/docs/integration-guides/errors/
         */
        private fun toErrorCode(response: FWResponse, type: String): ErrorCode =
            when (response.status.lowercase()) {
                "error" -> toErrorCode(response.message, type)
                else -> toErrorCode(response.data?.processor_response, type)
            }

        private fun toErrorCode(error: String?, type: String?): ErrorCode = when (error) {
            "DECLINED" -> ErrorCode.DECLINED

            "INSUFFICIENT_FUNDS",
            "Transaction Failed, Reason: NOT_ENOUGH_FUNDS",
            "Transaction Failed Reason: NOT_ENOUGH_FUNDS",
            "Insufficient Credit",
            "Insufficient Funds or User Failed to Validate",
            "Payment could not be done due to insufficient funds",
            "The balance is insufficient for the transaction.",
            -> ErrorCode.NOT_ENOUGH_FUNDS

            "ABORTED" -> ErrorCode.ABORTED
            "CANCELLED" -> ErrorCode.CANCELLED
            "SYSTEM_ERROR" -> ErrorCode.INTERNAL_PROCESSING_ERROR

            "AUTHENTICATION_FAILED",
            "Some mandatory parameters are missing : Authorization code (OTP / Transaction code) must be provided",
            -> ErrorCode.AUTHENTICATION_FAILED

            "Transaction has been flagged as fraudulent" -> ErrorCode.FRAUDULENT
            "email is required" -> ErrorCode.EMAIL_MISSING
            "Validation error: Invalid email address." -> ErrorCode.INVALID_EMAIL
            "You have exceeded your daily limit" -> ErrorCode.PAYER_LIMIT_REACHED
            "Insufficient Fund" -> ErrorCode.NOT_ENOUGH_FUNDS
            "Invalid or Unknown Mobile Network" -> ErrorCode.MOBILE_NETWORK_NOT_SUPPORTED

            "Account does not exist",
            "Invalid account",
            -> when (type?.uppercase()) {
                "PAYMENT" -> ErrorCode.PAYER_NOT_FOUND
                "TRANSFER" -> ErrorCode.PAYEE_NOT_FOUND
                else -> ErrorCode.UNEXPECTED_ERROR
            }

            "The customer's authentication failed. The customer should check their details before retrying the transaction." -> ErrorCode.AUTHENTICATION_FAILED
            else -> ErrorCode.UNEXPECTED_ERROR
        }
    }

    open fun health() {
        fwRetryable { doHealth() }
    }

    private fun doHealth() {
        val from = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        http.get(
            referenceId = UUID.randomUUID().toString(),
            uri = "$BASE_URI/transactions?from=$from",
            responseType = Map::class.java,
            headers = toHeaders(),
        )
    }

    override fun getType() = GatewayType.FLUTTERWAVE

    override fun createPayment(request: CreatePaymentRequest): CreatePaymentResponse =
        fwRetryable {
            val fwRequest = FWChargeRequest(
                amount = toAmount(request.amount),
                currency = request.amount.currency,
                email = request.payer.email ?: "",
                tx_ref = request.externalId,
                phone_number = normalizePhoneNumber(request.payer.phoneNumber),
                country = request.payer.country?.uppercase(),
                fullname = request.payer.fullName,
                device_fingerprint = request.deviceId,
                preauthorize = true,
                meta = mapOf(
                    META_WALLET_ID to request.walletId,
                    META_PAYER_ID to request.payer.id,
                ),
            )

            val type = toChargeType(request)
            val response = http.post(
                referenceId = request.externalId,
                uri = "$BASE_URI/charges?type=$type",
                requestPayload = fwRequest,
                responseType = FWResponse::class.java,
                headers = toHeaders(),
            )!!

            val status = toStatus(response)
            if (status == Status.FAILED) {
                throw toPaymentException(response, "PAYMENT")
            } else {
                val id = response.data?.id
                return CreatePaymentResponse(
                    transactionId = id?.toString() ?: "",
                    financialTransactionId = response.data?.flw_ref,
                    status = toStatus(response),
                    fees = Money(response.data?.app_fee ?: 0.0, request.amount.currency),
                )
            }
        }

    override fun capturePayment(transactionId: String): CapturePaymentResponse {
        TODO("Not supported")
    }

    override fun getPayment(transactionId: String): GetPaymentResponse =
        fwRetryable {
            val response = http.get(
                referenceId = transactionId,
                uri = "$BASE_URI/transactions/$transactionId/verify",
                responseType = FWResponse::class.java,
                headers = toHeaders(),
            )

            val status = toStatus(response!!)
            val data = response.data
            if (status == Status.FAILED) {
                throw toPaymentException(response, "PAYMENT")
            } else {
                val meta = data?.metaAsMap()
                return GetPaymentResponse(
                    amount = Money(
                        value = data?.amount ?: 0.0,
                        currency = data?.currency ?: "",
                    ),
                    status = toStatus(response),
                    description = data?.narration ?: "",
                    payer = Party(
                        id = meta?.get(META_PAYER_ID)?.toString(),
                        fullName = data?.customer?.name ?: "",
                        phoneNumber = data?.customer?.phone_number ?: "",
                        email = data?.customer?.email,
                    ),
                    fees = Money(data?.app_fee ?: 0.0, data?.currency ?: ""),
                    externalId = data?.tx_ref ?: "",
                    financialTransactionId = data?.flw_ref,
                    walletId = meta?.get(META_WALLET_ID)?.toString(),
                    creationDateTime = formatDate(data?.created_at),
                )
            }
        }

    override fun createTransfer(request: CreateTransferRequest): CreateTransferResponse =
        fwRetryable {
            val bank = toAccountBank(request.amount.currency)
            val payload = FWTransferRequest(
                amount = toAmount(request.amount),
                currency = request.amount.currency,
                account_number = normalizeAccountNumber(request.payee.phoneNumber, bank),
                beneficiary_name = request.payee.fullName,
                account_bank = bank,
                narration = request.description,
                reference = request.externalId,
                email = request.payee.email ?: "",
                meta = mapOf(
                    "mobile_number" to request.sender?.phoneNumber,
                    "email" to request.sender?.email,
                    "sender" to request.sender?.fullName,
                    "beneficiary_country" to request.payee.country,
                    "beneficiary_name" to request.payee.fullName,
                    META_WALLET_ID to request.walletId,
                    META_PAYEE_ID to request.payee.id,
                ),
            )
            val response = http.post(
                referenceId = request.externalId,
                uri = "$BASE_URI/transfers",
                requestPayload = payload,
                responseType = FWResponse::class.java,
                headers = toHeaders(),
            )!!

            val status = toStatus(response)
            if (status == Status.FAILED) {
                throw toPaymentException(response, "TRANSFER")
            } else {
                return CreateTransferResponse(
                    transactionId = response.data?.id?.toString() ?: "",
                    financialTransactionId = null,
                    status = toStatus(response),
                    fees = Money(response.data?.fee ?: 0.0, response.data?.currency ?: ""),
                )
            }
        }

    override fun getTransfer(transactionId: String): GetTransferResponse =
        fwRetryable {
            val response = http.get(
                referenceId = transactionId,
                uri = "$BASE_URI/transfers/$transactionId",
                responseType = FWResponse::class.java,
                headers = toHeaders(),
            )

            val status = toStatus(response!!)
            val data = response.data
            if (status == Status.FAILED) {
                throw toPaymentException(response, "TRANSFER")
            } else {
                val meta = data?.metaAsMap()
                return GetTransferResponse(
                    amount = Money(
                        value = data?.amount ?: 0.0,
                        currency = data?.currency ?: "",
                    ),
                    status = toStatus(response),
                    description = data?.narration ?: "",
                    payee = Party(
                        id = meta?.get(META_PAYEE_ID)?.toString(),
                        fullName = data?.full_name ?: "",
                        phoneNumber = data?.account_number ?: "",
                    ),
                    fees = Money(data?.fee ?: 0.0, data?.currency ?: ""),
                    externalId = data?.reference ?: "",
                    walletId = meta?.get(META_WALLET_ID)?.toString(),
                    creationDateTime = formatDate(data?.created_at),
                )
            }
        }

    private fun toHeaders() = mapOf(
        "Authorization" to "Bearer $secretKey",
        "Content-Type" to "application/json",
    )

    private fun toAmount(money: Money): String =
        money.value.toInt().toString()

    private fun normalizeAccountNumber(number: String, bank: String): String =
        if (bank == BANK_FMM) {
            normalizePhoneNumber(number)
        } else {
            number
        }

    private fun normalizePhoneNumber(number: String): String =
        if (number.startsWith("+")) {
            number.substring(1)
        } else {
            number
        }

    private fun toAccountBank(currency: String): String =
        when (currency) {
            "XAF" -> BANK_FMM
            "XOF" -> BANK_FMM
            else -> throw PaymentException(Error(code = ErrorCode.INVALID_CURRENCY))
        }

    private fun toChargeType(request: CreatePaymentRequest): String =
        when (request.amount.currency) {
            "XAF" -> "mobile_money_franco"
            "XOF" -> "mobile_money_franco"
            else -> throw PaymentException(Error(code = ErrorCode.INVALID_CURRENCY))
        }

    private fun toStatus(response: FWResponse): Status = when (response.status.lowercase()) {
        "error" -> Status.FAILED
        "success" -> when (response.data?.status?.lowercase()) {
            "new", "pending" -> Status.PENDING
            "successful" -> Status.SUCCESSFUL
            "failed" -> Status.FAILED
            else -> throw IllegalStateException("Status not supported: ${response.data?.status}")
        }

        else -> throw IllegalStateException("Status not supported: ${response.status}")
    }

    private fun formatDate(date: String?): Date? =
        date?.let {
            val tz: TimeZone = TimeZone.getTimeZone("UTC")
            val df: DateFormat =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") // Quoted "Z" to indicate UTC, no timezone offset
            df.timeZone = tz

            df.parse(date)
        }
}

/**
 * Inline function to wrap all FW call in a try block
 * - If IO error => the FW call will be retry (up to 3 times)
 * - If HTTP error return, the response will be parsed an a PaymentException will be thrown
 * - Otherwise, the response is returned
 */
inline fun <T> fwRetryable(bloc: () -> T): T {
    var retry = 0
    while (true) {
        try {
            return bloc()
        } catch (ex: IOException) { // On connectivity error, retry
            Flutterwave.LOGGER.warn("$retry - request failed...", ex)
            if (retry++ >= Flutterwave.MAX_RETRIES) {
                throw ex
            } else {
                Thread.sleep(Flutterwave.RETRY_DELAY_MILLIS) // Pause before re-try
            }
        } catch (ex: HttpException) {
            try {
                val response = ObjectMapper().readValue(ex.bodyString, FWResponse::class.java)
                throw toPaymentException(response, "", ex)
            } catch (ex1: JsonParseException) {
                throw PaymentException(
                    error = Error(code = ErrorCode.UNEXPECTED_ERROR),
                    cause = ex1,
                )
            }
        }
    }
}
