package com.wutsi.blog.app.model

import com.wutsi.blog.transaction.dto.PaymentMethodType
import com.wutsi.blog.transaction.dto.TransactionType
import com.wutsi.platform.payment.core.Status

data class TransactionModel(
    val id: String = "",
    val status: Status = Status.UNKNOWN,
    val type: TransactionType = TransactionType.UNKNOWN,
    val paymentMethodType: PaymentMethodType = PaymentMethodType.UNKNOWN,
    val paymentMethodOwner: String = "",
    val wallet: WalletModel = WalletModel(),
    val merchant: UserModel = UserModel(),
    val amount: MoneyModel = MoneyModel(),
    val fees: MoneyModel = MoneyModel(),
    val net: MoneyModel = MoneyModel(),
    val creationDateTimeText: String = "",
    val email: String = "",
)
