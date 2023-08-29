package com.wutsi.platform.payment.model

import com.wutsi.platform.payment.core.Money
import com.wutsi.platform.payment.core.Status
import java.util.Date

data class GetPaymentResponse(
    val walletId: String? = null,
    val amount: Money = Money(),
    val payer: Party = Party(),
    val status: Status = Status.UNKNOWN,
    val description: String = "",
    val payerMessage: String? = null,
    val externalId: String = "",
    val financialTransactionId: String? = null,
    val fees: Money = Money(),
    val creationDateTime: Date? = null,
)
