package com.wutsi.platform.core.error.exception

import com.wutsi.platform.core.error.Error

open class WutsiException(val error: Error, cause: Throwable? = null) : RuntimeException(null, cause) {
    override val message: String?
        get() = "error-code=${error.code}"
}
