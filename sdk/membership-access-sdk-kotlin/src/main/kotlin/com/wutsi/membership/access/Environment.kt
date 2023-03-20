package com.wutsi.membership.access

import kotlin.String

public enum class Environment(
    public val url: String,
) {
    SANDBOX("https://membership-access-test.herokuapp.com"),
    PRODUCTION("https://membership-access-prod.herokuapp.com"),
}
