package com.wutsi.blog.app.model

import com.wutsi.blog.transaction.dto.PaymentMethodType

data class WalletAccountModel(
    var number: String? = null,
    var type: PaymentMethodType = PaymentMethodType.UNKNOWN,
    var owner: String? = null,
    var providerLogoUrl: String? = null,
)
