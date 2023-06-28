package com.wutsi.blog.transaction.dto

data class WalletAccount(
    var number: String? = null,
    var type: PaymentMethodType = PaymentMethodType.UNKNOWN,
    var owner: String? = null,
)
