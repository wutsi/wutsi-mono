package com.wutsi.blog.transaction.service

import com.wutsi.blog.transaction.dao.TransactionRepository
import com.wutsi.blog.transaction.domain.TransactionEntity
import com.wutsi.platform.payment.Gateway
import com.wutsi.platform.payment.GatewayType
import com.wutsi.platform.payment.PaymentException
import com.wutsi.platform.payment.core.ErrorCode
import com.wutsi.platform.payment.core.Money
import com.wutsi.platform.payment.core.Status
import com.wutsi.platform.payment.model.CreatePaymentRequest
import com.wutsi.platform.payment.model.CreatePaymentResponse
import com.wutsi.platform.payment.model.CreateTransferRequest
import com.wutsi.platform.payment.model.CreateTransferResponse
import com.wutsi.platform.payment.model.GetPaymentResponse
import com.wutsi.platform.payment.model.GetTransferResponse
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class NoneGateway(private val transactionDao: TransactionRepository) : Gateway {
    override fun getType(): GatewayType = GatewayType.NONE

    override fun createTransfer(request: CreateTransferRequest): CreateTransferResponse {
        val transactionId = UUID.randomUUID().toString()
        if (request.amount.value == 0.0) {
            return CreateTransferResponse(
                transactionId = transactionId,
                status = Status.SUCCESSFUL,
                fees = Money(0.0, request.amount.currency)
            )
        } else {
            throw PaymentException(
                error = com.wutsi.platform.payment.core.Error(
                    code = ErrorCode.NOT_ALLOWED,
                    transactionId = transactionId
                )
            )
        }
    }

    override fun getTransfer(transactionId: String): GetTransferResponse {
        val tx = transactionDao.findByGatewayTransactionId(transactionId).get()
        if (tx.status == Status.SUCCESSFUL) {
            return GetTransferResponse(
                walletId = tx.wallet.id,
                amount = Money(tx.amount.toDouble(), tx.currency),
                fees = Money(tx.fees.toDouble(), tx.currency),
                status = tx.status,
                creationDateTime = tx.creationDateTime,
                description = tx.description ?: "",
            )
        } else {
            throw throwPaymentException(tx)
        }
    }

    override fun createPayment(request: CreatePaymentRequest): CreatePaymentResponse {
        val transactionId = UUID.randomUUID().toString()
        if (request.amount.value == 0.0) {
            return CreatePaymentResponse(
                transactionId = transactionId,
                status = Status.SUCCESSFUL,
                fees = Money(0.0, request.amount.currency)
            )
        } else {
            throw PaymentException(
                error = com.wutsi.platform.payment.core.Error(
                    code = ErrorCode.NOT_ALLOWED,
                    transactionId = transactionId
                )
            )
        }
    }

    override fun getPayment(transactionId: String): GetPaymentResponse {
        val tx = transactionDao.findByGatewayTransactionId(transactionId).get()
        if (tx.status == Status.SUCCESSFUL) {
            return GetPaymentResponse(
                walletId = tx.wallet.id,
                amount = Money(tx.amount.toDouble(), tx.currency),
                fees = Money(tx.fees.toDouble(), tx.currency),
                status = tx.status,
                creationDateTime = tx.creationDateTime,
                description = tx.description ?: "",
            )
        } else {
            throw throwPaymentException(tx)
        }
    }

    private fun throwPaymentException(tx: TransactionEntity) = PaymentException(
        error = com.wutsi.platform.payment.core.Error(
            transactionId = tx.id!!,
            message = tx.errorMessage,
            supplierErrorCode = tx.supplierErrorCode,
            code = try {
                tx.errorCode?.let { errorCode -> ErrorCode.valueOf(errorCode) } ?: ErrorCode.UNEXPECTED_ERROR
            } catch (ex: Exception) {
                ErrorCode.UNEXPECTED_ERROR
            },
        )
    )
}
