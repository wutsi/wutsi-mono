package com.wutsi.blog.transaction.dto

enum class PaymentProviderType(val paymentMethodType: PaymentMethodType) {
    UNKNOWN(PaymentMethodType.UNKNOWN),
    MTN(PaymentMethodType.MOBILE_MONEY),
    ORANGE(PaymentMethodType.MOBILE_MONEY),
    PAYPAL(PaymentMethodType.PAYPAL),
}
