package com.wutsi.blog.app.model

import com.wutsi.blog.transaction.dto.PaymentMethodType
import com.wutsi.blog.transaction.dto.PaymentProviderType

data class PaymentProviderTypeModel(
    val type: PaymentProviderType = PaymentProviderType.UNKNOWN,
    val logoUrl: String = "",
) {
    val paypal: Boolean
        get() = (type == PaymentProviderType.PAYPAL)

    val mobileMoney: Boolean
        get() = (type.paymentMethodType == PaymentMethodType.MOBILE_MONEY)
}
