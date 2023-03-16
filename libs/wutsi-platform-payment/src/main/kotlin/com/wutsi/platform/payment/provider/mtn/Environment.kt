package com.wutsi.platform.payment.provider.mtn

enum class Environment(val baseUrl: String) {
    SANDBOX("https://sandbox.momodeveloper.mtn.com"),
    PRODUCTION("https://momodeveloper.mtn.com"),
}
