package com.wutsi.platform.payment.provider.mtn

import com.wutsi.platform.payment.Gateway
import com.wutsi.platform.payment.GatewayType
import com.wutsi.platform.payment.PaymentException
import com.wutsi.platform.payment.core.Error
import com.wutsi.platform.payment.core.ErrorCode
import com.wutsi.platform.payment.core.ErrorCode.AUTHENTICATION_FAILED
import com.wutsi.platform.payment.core.ErrorCode.UNEXPECTED_ERROR
import com.wutsi.platform.payment.core.HttpException
import com.wutsi.platform.payment.core.Money
import com.wutsi.platform.payment.core.Status
import com.wutsi.platform.payment.core.Status.PENDING
import com.wutsi.platform.payment.core.Status.SUCCESSFUL
import com.wutsi.platform.payment.model.CreatePaymentRequest
import com.wutsi.platform.payment.model.CreatePaymentResponse
import com.wutsi.platform.payment.model.CreateTransferRequest
import com.wutsi.platform.payment.model.CreateTransferResponse
import com.wutsi.platform.payment.model.GetPaymentResponse
import com.wutsi.platform.payment.model.GetTransferResponse
import com.wutsi.platform.payment.provider.mtn.Environment.SANDBOX
import com.wutsi.platform.payment.provider.mtn.model.MTNParty
import com.wutsi.platform.payment.provider.mtn.model.MTNRequestToPayRequest
import com.wutsi.platform.payment.provider.mtn.model.MTNTransferRequest
import com.wutsi.platform.payment.provider.mtn.product.Collection
import com.wutsi.platform.payment.provider.mtn.product.Disbursement
import com.wutsi.platform.payment.provider.mtn.product.ProductConfig
import org.slf4j.LoggerFactory
import java.util.UUID

open class MTNGateway(
    private val collection: Collection,
    private val disbursement: Disbursement,
) : Gateway {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(MTNGateway::class.java)
    }

    override fun getType() = GatewayType.MTN

    override fun createPayment(request: CreatePaymentRequest): CreatePaymentResponse {
        LOGGER.info("Creating payment")

        val transactionId = UUID.randomUUID().toString()
        try {
            val accessToken = collection.token(transactionId).access_token
            collection.requestToPay(
                referenceId = transactionId,
                accessToken = accessToken,
                request = MTNRequestToPayRequest(
                    amount = request.amount.value.toString(),
                    currency = currency(collection.config, request.amount.currency),
                    payer = MTNParty(toPhoneNumber(request.payer.phoneNumber)),
                    payeeNote = request.description,
                    externalId = request.externalId,
                    payerMessage = request.payerMessage ?: "",
                ),
            )
            val response = collection.requestToPay(transactionId, accessToken)
            val status = toStatus(response.status)
            if (status == PENDING || status == SUCCESSFUL) {
                return CreatePaymentResponse(
                    status = toStatus(response.status),
                    transactionId = transactionId,
                    financialTransactionId = response.financialTransactionId?.ifBlank { null },
                )
            } else {
                throw PaymentException(
                    error = Error(
                        code = toErrorCode(response.reason),
                        transactionId = transactionId,
                        supplierErrorCode = response.reason,
                    ),
                )
            }
        } catch (ex: HttpException) {
            throw handleException(transactionId, ex)
        }
    }

    override fun getPayment(transactionId: String): GetPaymentResponse {
        LOGGER.info("Retrieving payment $transactionId")

        val accessToken = collection.token(transactionId).access_token
        val response = collection.requestToPay(transactionId, accessToken)
        val status = toStatus(response.status)

        if (status == PENDING || status == SUCCESSFUL) {
            return GetPaymentResponse(
                payer = com.wutsi.platform.payment.model.Party(
                    phoneNumber = response.payer.partyId,
                ),
                amount = Money(
                    value = response.amount.toDouble(),
                    currency = response.currency,
                ),
                status = status,
                description = response.payeeNote,
                payerMessage = response.payerMessage,
                externalId = response.externalId,
                financialTransactionId = response.financialTransactionId?.ifBlank { null },
            )
        } else {
            throw PaymentException(
                error = Error(
                    code = toErrorCode(response.reason),
                    transactionId = transactionId,
                    supplierErrorCode = response.reason,
                ),
            )
        }
    }

    override fun createTransfer(request: CreateTransferRequest): CreateTransferResponse {
        LOGGER.info("Creating transfer")

        val transactionId = UUID.randomUUID().toString()
        try {
            val accessToken = disbursement.token(transactionId).access_token
            disbursement.transfer(
                referenceId = transactionId,
                accessToken = accessToken,
                request = MTNTransferRequest(
                    amount = request.amount.value.toString(),
                    currency = currency(disbursement.config, request.amount.currency),
                    payee = MTNParty(toPhoneNumber(request.payee.phoneNumber)),
                    payeeNote = request.description,
                    externalId = request.externalId,
                    payerMessage = request.payerMessage ?: "",
                ),
            )
            val response = disbursement.transfer(transactionId, accessToken)
            val status = toStatus(response.status)
            if (status == PENDING || status == SUCCESSFUL) {
                return CreateTransferResponse(
                    transactionId = transactionId,
                    financialTransactionId = response.financialTransactionId.ifBlank { null },
                    status = toStatus(response.status),
                )
            } else {
                throw PaymentException(
                    error = Error(
                        code = toErrorCode(response.reason),
                        transactionId = transactionId,
                        supplierErrorCode = response.reason,
                    ),
                )
            }
        } catch (ex: HttpException) {
            throw handleException(transactionId, ex)
        }
    }

    override fun getTransfer(transactionId: String): GetTransferResponse {
        val accessToken = disbursement.token(transactionId).access_token
        val response = disbursement.transfer(transactionId, accessToken)
        val status = toStatus(response.status)

        if (status == PENDING || status == SUCCESSFUL) {
            return GetTransferResponse(
                payee = com.wutsi.platform.payment.model.Party(
                    phoneNumber = response.payee.partyId,
                ),
                amount = Money(
                    value = response.amount.toDouble(),
                    currency = response.currency,
                ),
                status = status,
                description = response.payeeNote,
                payerMessage = response.payerMessage,
                externalId = response.externalId,
                financialTransactionId = response.financialTransactionId.ifBlank { null },
            )
        } else {
            throw PaymentException(
                error = Error(
                    code = toErrorCode(response.reason),
                    transactionId = transactionId,
                    supplierErrorCode = response.reason,
                ),
            )
        }
    }

    private fun currency(config: ProductConfig, currency: String): String =
        if (config.environment == SANDBOX) {
            "EUR"
        } else {
            currency
        }

    private fun toPhoneNumber(number: String): String =
        if (number.startsWith("+")) {
            number.substring(1)
        } else {
            number
        }

    private fun toStatus(status: String): Status =
        Status.valueOf(status)

    private fun toErrorCode(reason: String?): ErrorCode =
        MTNErrorCode.values().find { it.name == reason }?.paymentErrorCode ?: UNEXPECTED_ERROR

    private fun handleException(transactionId: String, ex: HttpException): Throwable =
        if (ex.statusCode == 401) {
            PaymentException(
                error = Error(
                    code = AUTHENTICATION_FAILED,
                    transactionId = transactionId,
                ),
                ex,
            )
        } else {
            PaymentException(
                error = Error(
                    code = UNEXPECTED_ERROR,
                    transactionId = transactionId,
                ),
                ex,
            )
        }
}
