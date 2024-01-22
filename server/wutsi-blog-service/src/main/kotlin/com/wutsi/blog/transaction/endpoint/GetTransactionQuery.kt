package com.wutsi.blog.transaction.endpoint

import com.wutsi.blog.transaction.dto.GetTransactionResponse
import com.wutsi.blog.transaction.dto.Transaction
import com.wutsi.blog.transaction.service.TransactionService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class GetTransactionQuery(
    private val service: TransactionService,
) {
    @GetMapping("/v1/transactions/{id}")
    fun create(
        @PathVariable id: String,
        @RequestParam(required = false, defaultValue = "false") sync: Boolean = false,
    ): GetTransactionResponse {
        val tx = service.findById(id, sync)
        return GetTransactionResponse(
            transaction = Transaction(
                id = tx.id ?: "",
                email = tx.email,
                idempotencyKey = tx.idempotencyKey,
                amount = tx.amount,
                currency = tx.currency,
                walletId = tx.wallet.id!!,
                userId = tx.user?.id,
                storeId = tx.store?.id,
                productId = tx.product?.id,
                paymentMethodType = tx.paymentMethodType,
                paymentMethodOwner = tx.paymentMethodOwner,
                paymentMethodNumber = tx.paymentMethodNumber,
                fees = tx.fees,
                lastModificationDateTime = tx.lastModificationDateTime,
                description = tx.description,
                status = tx.status,
                supplierErrorCode = tx.supplierErrorCode,
                anonymous = tx.anonymous,
                errorCode = tx.errorCode,
                creationDateTime = tx.creationDateTime,
                type = tx.type,
                gatewayType = tx.gatewayType,
                gatewayTransactionId = tx.gatewayTransactionId,
                gatewayFees = tx.gatewayFees,
                net = tx.net,
                errorMessage = tx.errorMessage,
            ),
        )
    }
}
