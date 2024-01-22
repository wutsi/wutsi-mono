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
    val paymentMethodNumber: String = "",
    val wallet: WalletModel = WalletModel(),
    val merchant: UserModel = UserModel(),
    val product: ProductModel? = null,
    val amount: MoneyModel = MoneyModel(),
    val fees: MoneyModel = MoneyModel(),
    val net: MoneyModel = MoneyModel(),
    val creationDateTimeText: String = "",
    val email: String = "",
    val errorCode: String? = null,
    val errorMessage: String? = null,
) {
    val successful: Boolean get() = status == Status.SUCCESSFUL
    val failed: Boolean get() = status == Status.FAILED
}
