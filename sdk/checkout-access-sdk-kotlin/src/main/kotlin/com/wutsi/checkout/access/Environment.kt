package com.wutsi.checkout.access

import kotlin.String

public enum class Environment(
    public val url: String,
) {
    SANDBOX("https://checkout-access-test.herokuapp.com"),
    PRODUCTION("https://checkout-access-prod.herokuapp.com"),
}
