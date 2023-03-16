package com.wutsi.platform.payment.provider.mtn.product

import com.wutsi.platform.payment.provider.mtn.Environment
import com.wutsi.platform.payment.provider.mtn.UserProvider

data class ProductConfig(
    val environment: Environment,
    val subscriptionKey: String = "",
    val callbackUrl: String = "",
    val userProvider: UserProvider,
)
