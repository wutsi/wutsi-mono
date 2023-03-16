package com.wutsi.platform.payment.provider.mtn.product

import com.wutsi.platform.payment.provider.mtn.model.MTNTokenResponse

interface Product {
    fun token(referenceId: String): MTNTokenResponse
}
