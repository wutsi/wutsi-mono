package com.wutsi.platform.core.error.exception

import com.wutsi.platform.core.error.Error
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
class InternalErrorException(error: Error, ex: Throwable? = null) : WutsiException(error, ex)
