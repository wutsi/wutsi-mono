package com.wutsi.blog.transaction.exception

import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.WutsiException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.CONFLICT)
class TransactionException(
    val transactionId: String,
    error: Error,
    cause: Throwable? = null
) : WutsiException(error, cause)
