package com.wutsi.platform.payment.provider.paypal

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
import com.wutsi.platform.payment.provider.paypal.model.PPAuthResponse
import com.wutsi.platform.payment.provider.paypal.model.PPCreateOrderRequest
import com.wutsi.platform.payment.provider.paypal.model.PPCreateOrderResponse
import com.wutsi.platform.payment.provider.paypal.model.PPMoney
import com.wutsi.platform.payment.provider.paypal.model.PPOrderResponse
import com.wutsi.platform.payment.provider.paypal.model.PPPurchaseUnit
import java.text.SimpleDateFormat
import java.util.Base64
import java.util.UUID

open class Paypal(
    private val http: Http,
    private val clientId: String,
    private val secretKey: String,
    private val testMode: Boolean,
) : Gateway {
    companion object {
        const val SANDBOX_URI = "https://api-m.sandbox.paypal.com"
        const val PRODUCTION_URI = "https://api-m.paypal.com"
    }

    open fun health() {
        getAccessToken(UUID.randomUUID().toString())
    }

    override fun getType() = GatewayType.PAYPAL

    override fun capturePayment(transactionId: String): CapturePaymentResponse {
        try {
            val referenceId = transactionId
            val accessToken = getAccessToken(referenceId)
            val response = http.post(
                referenceId,
                url() + "/v2/checkout/orders/$transactionId/capture",
                emptyMap<String, Any>(),
                PPOrderResponse::class.java,
                toHeaders(accessToken)
            )!!
            return CapturePaymentResponse(
                transactionId = transactionId,
                status = toStatus(response.status)
            )
        } catch (ex: HttpException) {
            throw PaymentException(
                error = Error(
                    code = ErrorCode.UNEXPECTED_ERROR
                ),
                ex
            )
        }
    }

    override fun createPayment(request: CreatePaymentRequest): CreatePaymentResponse {
        try {
            val referenceId = request.externalId
            val accessToken = getAccessToken(referenceId)
            val response = http.post(
                referenceId = referenceId,
                uri = url() + "/v2/checkout/orders",
                requestPayload = PPCreateOrderRequest(
                    intent = "CAPTURE",
                    purchase_units = listOf(
                        PPPurchaseUnit(
                            reference_id = referenceId,
                            description = request.description,
                            amount = PPMoney(request.amount.value, request.amount.currency),
                        )
                    )
                ),
                responseType = PPCreateOrderResponse::class.java,
                headers = toHeaders(accessToken)
            )!!

            return CreatePaymentResponse(
                transactionId = response.id,
                status = Status.PENDING
            )
        } catch (ex: HttpException) {
            throw PaymentException(
                error = Error(
                    code = ErrorCode.UNEXPECTED_ERROR
                ),
                ex
            )
        }
    }

    override fun getPayment(transactionId: String): GetPaymentResponse {
        try {
            val accessToken = getAccessToken(transactionId)
            val response = http.get(
                transactionId,
                url() + "/v2/checkout/orders/$transactionId",
                PPOrderResponse::class.java,
                headers = toHeaders(accessToken)
            )!!

            if (response.status == "VOIDED") {
                throw PaymentException(
                    error = Error(code = ErrorCode.UNEXPECTED_ERROR)
                )
            } else {
                val fmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
                val unit = response.purchase_units.first()
                val amount = unit.amount
                return GetPaymentResponse(
                    externalId = unit.reference_id,
                    amount = Money(amount.value, amount.currency_code),
                    status = toStatus(response.status),
                    description = unit.description ?: "",
                    payer = Party(
                        fullName = "${response.payer.name.given_name} ${response.payer.name.subname}",
                        email = response.payer.email_address,
                        country = response.payer.address.country_code
                    ),
                    creationDateTime = fmt.parse(response.create_time)
                )
            }
        } catch (ex: HttpException) {
            throw PaymentException(
                error = Error(
                    code = ErrorCode.UNEXPECTED_ERROR
                ),
                ex
            )
        }
    }

    override fun createTransfer(request: CreateTransferRequest): CreateTransferResponse {
        TODO()
    }

    override fun getTransfer(transactionId: String): GetTransferResponse {
        TODO()
    }

    private fun toStatus(status: String): Status =
        when (status) {
            "COMPLETED", "APPROVED" -> Status.SUCCESSFUL
            "VOIDED" -> Status.FAILED
            else -> Status.PENDING
        }

    private fun toHeaders(accessToken: String) = mapOf(
        "Authorization" to "Bearer $accessToken",
        "Content-Type" to "application/json",
    )

    private fun getAccessToken(referenceId: String): String {
        val response = http.post(
            referenceId,
            url() + "/v1/oauth2/token",
            "grant_type=client_credentials",
            PPAuthResponse::class.java,
            mapOf(
                "Content-Type" to "application/x-www-form-urlencoded",
                "Authorization" to "Basic " + Base64.getEncoder().encodeToString("$clientId:$secretKey".toByteArray())
            )
        )
        return response?.access_token ?: "-"
    }

    private fun url(): String =
        if (testMode) SANDBOX_URI else PRODUCTION_URI
}
