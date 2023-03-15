package com.wutsi.platform.core.messaging.whatsapp

class WAException(val httpStatusCode: Int, message: String) : Exception(message)
