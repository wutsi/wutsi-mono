package com.wutsi.blog.app.model

import com.wutsi.blog.transaction.dto.PaymentProviderType

data class PaymentProviderTypeModel(
    val type: PaymentProviderType = PaymentProviderType.UNKNOWN,
    val logoUrl: String = "",
)
