package com.wutsi.platform.payment.core

interface HttpListener {
    fun notify(
        transactionId: String,
        method: String,
        uri: String,
        statusCode: Int,
        request: String?,
        response: String?,
    )
}
