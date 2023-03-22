package com.wutsi.marketplace.manager

import kotlin.String

public enum class Environment(
    public val url: String,
) {
    SANDBOX("https://marketplace-manager-test.herokuapp.com"),
    PRODUCTION("https://marketplace-manager-prod.herokuapp.com"),
}
