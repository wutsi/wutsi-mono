package com.wutsi.platform.payment.core

import org.slf4j.LoggerFactory

class DefaultHttpListener : HttpListener {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(DefaultHttpListener::class.java)
    }

    override fun notify(
        transactionId: String,
        method: String,
        uri: String,
        statusCode: Int,
        request: String?,
        response: String?,
    ) {
        LOGGER.info("$transactionId\t$method\t$uri\t$statusCode\nrequest=$request\nresponse=$response")
    }
}
