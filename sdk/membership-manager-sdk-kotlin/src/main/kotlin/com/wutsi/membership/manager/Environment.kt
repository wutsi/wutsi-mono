package com.wutsi.membership.manager

import kotlin.String

public enum class Environment(
    public val url: String,
) {
    SANDBOX("https://membership-manager-test.herokuapp.com"),
    PRODUCTION("https://membership-manager-prod.herokuapp.com"),
}
