package com.wutsi.tracking.manager

import kotlin.String

public enum class Environment(
    public val url: String,
) {
    SANDBOX("https://tracking-manager-test.herokuapp.com"),
    PRODUCTION("https://tracking-manager-server-prod.herokuapp.com"),
}
