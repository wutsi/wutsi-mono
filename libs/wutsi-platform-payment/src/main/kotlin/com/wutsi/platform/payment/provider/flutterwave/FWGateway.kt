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
import com.wutsi.platform.payment.model.BankAccount
import com.wutsi.platform.payment.model.CreatePaymentRequest
import com.wutsi.platform.payment.model.CreatePaymentResponse
import com.wutsi.platform.payment.model.CreateTransferRequest
import com.wutsi.platform.payment.model.CreateTransferResponse
import com.wutsi.platform.payment.model.GetPaymentResponse
import com.wutsi.platform.payment.model.GetTransferResponse
import com.wutsi.platform.payment.model.Party
import com.wutsi.platform.payment.provider.flutterwave.FWGateway.Companion.toPaymentException
import com.wutsi.platform.payment.provider.flutterwave.model.FWBank
import com.wutsi.platform.payment.provider.flutterwave.model.FWChargeRequest
import com.wutsi.platform.payment.provider.flutterwave.model.FWGetBankListResponse
import com.wutsi.platform.payment.provider.flutterwave.model.FWResponse
import com.wutsi.platform.payment.provider.flutterwave.model.FWTransferRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

open class FWGateway(
    private val http: Http,
    private val secretKey: String,
    private val testMode: Boolean,
    private val encryptor: FWEncryptor,
) : Gateway {
    companion object {
        const val BASE_URI = "https://api.flutterwave.com/v3"
        const val MAX_RETRIES = 3
        const val RETRY_DELAY_MILLIS = 1000L
        const val TEST_MODE_BANK = "044"
        val LOGGER: Logger = LoggerFactory.getLogger(FWGateway::class.java)
        val bankMap: MutableMap<String, List<FWBank>?> = mutableMapOf() // Banks indexed by country

        fun toPaymentException(response: FWResponse, ex: Throwable? = null) = PaymentException(
            error = Error(
                transactionId = response.data?.id?.toString() ?: "",
                code = toErrorCode(response),
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
        private fun toErrorCode(response: FWResponse): ErrorCode =
            when (response.status.lowercase()) {
                "error" -> toErrorCode(response.message)
                else -> toErrorCode(response.data?.processor_response)
            }

        private fun toErrorCode(error: String?): ErrorCode = when (error) {
            "DECLINED" -> ErrorCode.DECLINED
            "INSUFFICIENT_FUNDS" -> ErrorCode.NOT_ENOUGH_FUNDS
            "ABORTED" -> ErrorCode.ABORTED
            "CANCELLED" -> ErrorCode.CANCELLED
            "SYSTEM_ERROR" -> ErrorCode.INTERNAL_PROCESSING_ERROR
            "AUTHENTICATION_FAILED" -> ErrorCode.AUTHENTICATION_FAILED
            "Transaction has been flagged as fraudulent" -> ErrorCode.FRAUDULENT
            "email is required" -> ErrorCode.EMAIL_MISSING
            "Validation error: Invalid email address." -> ErrorCode.INVALID_EMAIL
            "You have exceeded your daily limit" -> ErrorCode.PAYER_LIMIT_REACHED
            "Insufficient Funds or User Failed to Validate" -> ErrorCode.NOT_ENOUGH_FUNDS
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
                phone_number = toPhoneNumber(request.payer.phoneNumber, request.bankAccount),
                country = request.payer.country,
                fullname = request.creditCard?.owner ?: request.bankAccount?.owner ?: request.payer.fullName,
                device_fingerprint = request.deviceId,
                card_number = request.creditCard?.number,
                cvv = request.creditCard?.cvv,
                expiry_month = to2Digits(request.creditCard?.expiryMonth),
                expiry_year = to2Digits(request.creditCard?.expiryYear),
                preauthorize = true,
            )

            val type = toChargeType(request)
            val response = http.post(
                referenceId = request.externalId,
                uri = "$BASE_URI/charges?type=$type",
                requestPayload = if (type == "card") encryptor.encrypt(fwRequest) else fwRequest,
                responseType = FWResponse::class.java,
                headers = toHeaders(),
            )!!

            val status = toStatus(response)
            if (status == Status.FAILED) {
                throw toPaymentException(response)
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
                throw toPaymentException(response)
            } else {
                return GetPaymentResponse(
                    amount = Money(
                        value = data?.amount ?: 0.0,
                        currency = data?.currency ?: "",
                    ),
                    status = toStatus(response),
                    description = data?.narration ?: "",
                    payer = Party(
                        fullName = data?.customer?.name ?: "",
                        phoneNumber = data?.customer?.phone_number ?: "",
                        email = data?.customer?.email,
                    ),
                    fees = Money(data?.app_fee ?: 0.0, data?.currency ?: ""),
                    externalId = data?.tx_ref ?: "",
                    financialTransactionId = data?.flw_ref,
                )
            }
        }

    override fun createTransfer(request: CreateTransferRequest): CreateTransferResponse =
        fwRetryable {
            val payload = FWTransferRequest(
                amount = toAmount(request.amount),
                currency = request.amount.currency,
                account_number = toPhoneNumber(request.payee.phoneNumber, request.bankAccount),
                beneficiary_name = request.bankAccount?.owner ?: request.payee.fullName,
                account_bank = toAccountBank(request.amount.currency, request.externalId, request.bankAccount),
                narration = request.description,
                reference = request.externalId,
                email = request.payee.email ?: "",
                meta = mapOf(
                    "mobile_number" to request.sender?.phoneNumber,
                    "email" to request.sender?.email,
                    "sender" to request.sender?.fullName,
                    "beneficiary_country" to (request.bankAccount?.country ?: request.payee.country),
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
                throw toPaymentException(response)
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
                throw toPaymentException(response)
            } else {
                return GetTransferResponse(
                    amount = Money(
                        value = data?.amount ?: 0.0,
                        currency = data?.currency ?: "",
                    ),
                    status = toStatus(response),
                    description = data?.narration ?: "",
                    payee = Party(
                        fullName = data?.full_name ?: "",
                        phoneNumber = data?.account_number ?: "",
                    ),
                    fees = Money(data?.fee ?: 0.0, data?.currency ?: ""),
                    externalId = data?.reference ?: "",
                )
            }
        }

    private fun toHeaders() = mapOf(
        "Authorization" to "Bearer $secretKey",
        "Content-Type" to "application/json",
    )

    private fun toAmount(money: Money): String =
        money.value.toInt().toString()

    private fun toPhoneNumber(number: String, bankAccount: BankAccount?): String {
        return if (bankAccount != null) {
            bankAccount.number
        } else if (number.startsWith("+")) {
            number.substring(1)
        } else {
            number
        }
    }

    private fun toAccountBank(currency: String, referenceId: String, bankAccount: BankAccount?): String {
        if (bankAccount != null) {
            if (testMode) {
                // See https://developer.flutterwave.com/docs/integration-guides/testing-helpers
                return TEST_MODE_BANK
            }

            val banks = getBanks(bankAccount.country, referenceId)
            val bank = banks.find { it.name.equals(bankAccount.bankName, true) }

            return bank?.code ?: throw PaymentException(Error(code = ErrorCode.INVALID_BANK))
        } else {
            return when (currency) {
                "XAF" -> "FMM"
                "XOF" -> "FMM"
                else -> throw PaymentException(Error(code = ErrorCode.INVALID_CURRENCY))
            }
        }
    }

    private fun getBanks(country: String, referenceId: String): List<FWBank> {
        val key = country.uppercase()
        var banks = bankMap[key]
        if (banks == null) {
            banks = http.get(
                referenceId = referenceId,
                uri = "$BASE_URI/banks/$key",
                responseType = FWGetBankListResponse::class.java,
                headers = toHeaders(),
            )!!.data
            bankMap[key] = banks
        }
        return banks
    }

    private fun toChargeType(request: CreatePaymentRequest): String =
        if (request.creditCard != null) {
            "card"
        } else if (request.bankAccount != null) {
            "bank_transfer"
        } else {
            when (request.amount.currency) {
                "XAF" -> "mobile_money_franco"
                "XOF" -> "mobile_money_franco"
                else -> throw PaymentException(Error(code = ErrorCode.INVALID_CURRENCY))
            }
        }

    private fun toStatus(response: FWResponse): Status = when (response.status.lowercase()) {
        "error" -> Status.FAILED
        "success" -> when (response.data?.status?.lowercase()) {
            "new" -> Status.PENDING
            "pending" -> Status.PENDING
            "successful" -> Status.SUCCESSFUL
            "failed" -> Status.FAILED
            else -> throw IllegalStateException("Status not supported: ${response.data?.status}")
        }
        else -> throw IllegalStateException("Status not supported: ${response.status}")
    }

    private fun to2Digits(value: Int?): String? =
        if (value == null) {
            null
        } else if (value < 10) {
            "0$value"
        } else {
            (value % 100).toString()
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
            FWGateway.LOGGER.warn("$retry - request failed...", ex)
            if (retry++ >= FWGateway.MAX_RETRIES) {
                throw ex
            } else {
                Thread.sleep(FWGateway.RETRY_DELAY_MILLIS) // Pause before re-try
            }
        } catch (ex: HttpException) {
            try {
                val response = ObjectMapper().readValue(ex.bodyString, FWResponse::class.java)
                throw toPaymentException(response, ex)
            } catch (ex1: JsonParseException) {
                throw PaymentException(
                    error = Error(code = ErrorCode.UNEXPECTED_ERROR),
                    cause = ex1,
                )
            }
        }
    }
}
