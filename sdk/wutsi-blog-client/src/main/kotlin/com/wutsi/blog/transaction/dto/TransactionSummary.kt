package com.wutsi.blog.transaction.dto

import com.wutsi.platform.payment.core.Status
import java.util.Date

data class TransactionSummary(
    val id: String = "",
    val type: TransactionType = TransactionType.UNKNOWN,
    val status: Status = Status.UNKNOWN,
    val walletId: String = "",
    val userId: Long = -1,
    val amount: Long = 0L,
    val fees: Long = 0L,
    val net: Long = 0L,
    val currency: String = "",
    val paymentMethodOwner: String = "",
    val paymentMethodType: PaymentMethodType = PaymentMethodType.UNKNOWN,
    val errorCode: String? = null,
    val creationDateTime: Date = Date(),
    val anonymous: Boolean = false,
)
