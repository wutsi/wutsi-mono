package com.wutsi.security.manager

import kotlin.String

public enum class Environment(
    public val url: String,
) {
    SANDBOX("https://security-manager-test.herokuapp.com"),
    PRODUCTION("https://security-manager-prod.herokuapp.com"),
}
