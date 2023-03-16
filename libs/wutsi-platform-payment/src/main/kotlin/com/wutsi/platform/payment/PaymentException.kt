package com.wutsi.platform.payment

import com.wutsi.platform.payment.core.Error

class PaymentException(
    val error: Error = Error(),
    cause: Throwable? = null,
) : RuntimeException(cause) {
    override val message: String
        get() = "error-code=${error.code}"
}
