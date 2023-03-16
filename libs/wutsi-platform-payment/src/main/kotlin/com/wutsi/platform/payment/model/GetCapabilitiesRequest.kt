package com.wutsi.platform.payment.model

import com.wutsi.platform.payment.PaymentMethodProvider

data class GetCapabilitiesRequest(
    val paymentMethodProvider: PaymentMethodProvider,
    val country: String,
)
