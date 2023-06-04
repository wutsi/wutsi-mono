package com.wutsi.blog.story.exception

import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.WutsiException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.CONFLICT)
class ImportException(error: Error, ex: Throwable? = null) : WutsiException(error, ex)
