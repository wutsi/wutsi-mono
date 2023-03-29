package com.wutsi.checkout.manager.delegate

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.error.ErrorURN
import com.wutsi.platform.core.error.ErrorResponse
import com.wutsi.platform.core.error.exception.ConflictException
import feign.FeignException
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractTransactionDelegate {
    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    open fun handleSuccess(transactionId: String) {
        // DO NOTHING
    }

    protected fun handleTransactionException(ex: FeignException): Throwable {
        val response = objectMapper.readValue(ex.contentUTF8(), ErrorResponse::class.java)
        return if (response.error.code == com.wutsi.checkout.access.error.ErrorURN.TRANSACTION_FAILED.urn) {
            ConflictException(
                error = response.error.copy(code = ErrorURN.TRANSACTION_FAILED.urn),
            )
        } else {
            ex
        }
    }
}
