package com.wutsi.checkout.manager

import kotlin.String

public enum class Environment(
    public val url: String,
) {
    SANDBOX("https://checkout-manager-test.herokuapp.com"),
    PRODUCTION("https://checkout-manager-prod.herokuapp.com"),
}
