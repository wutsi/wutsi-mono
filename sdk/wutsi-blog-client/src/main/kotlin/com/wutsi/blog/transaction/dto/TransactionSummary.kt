package com.wutsi.blog.transaction.dto

import com.wutsi.platform.payment.GatewayType
import com.wutsi.platform.payment.core.Status
import java.util.Date

public data class Transaction(
    val id: String = "",
    val idempotencyKey: String = "",
    val type: TransactionType = TransactionType.UNKNOWN,
    val status: Status = Status.UNKNOWN,
    val walletId: String = "",
    val userId: Long = -1,
    val email: String? = null,
    val anonymous: Boolean = false,
    val amount: Long = 0L,
    val fees: Long = 0L,
    val net: Long = 0L,
    val gatewayFees: Long = 0,
    val currency: String = "",
    val description: String? = null,
    val paymentMethodOwner: String = "",
    val paymentMethodNumber: String = "",
    val paymentMethodType: PaymentMethodType = PaymentMethodType.UNKNOWN,
    val gatewayType: GatewayType = GatewayType.UNKNOWN,
    val gatewayTransactionId: String? = null,
    val errorCode: String? = null,
    val supplierErrorCode: String? = null,
    val errorMessage: String? = null,
    val creationDateTime: Date = Date(),
    val lastModificationDateTime: Date = Date(),
)
