package com.wutsi.platform.payment.model

import com.wutsi.platform.payment.core.Money
import com.wutsi.platform.payment.core.Status
import com.wutsi.platform.payment.core.Status.UNKNOWN

data class GetTransferResponse(
    val walletId: String? = null,
    val payee: Party = Party(),
    val amount: Money = Money(),
    val externalId: String = "",
    val description: String = "",
    val payerMessage: String? = null,
    val status: Status = UNKNOWN,
    val financialTransactionId: String? = null,
    val fees: Money = Money(),
)
