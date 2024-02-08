package com.wutsi.blog.transaction.endpoint

import com.wutsi.blog.transaction.dto.CaptureTransactionCommand
import com.wutsi.blog.transaction.dto.CaptureTransactionResponse
import com.wutsi.blog.transaction.exception.TransactionException
import com.wutsi.blog.transaction.service.TransactionService
import com.wutsi.platform.payment.core.ErrorCode
import com.wutsi.platform.payment.core.Status
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/v1/transactions/commands/capture-transaction")
class CaptureTransactionCommandExecutor(
    private val service: TransactionService,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(CaptureTransactionCommandExecutor::class.java)
    }

    @PostMapping
    fun create(@RequestBody @Valid command: CaptureTransactionCommand): CaptureTransactionResponse =
        try {
            val tx = service.capture(command)
            CaptureTransactionResponse(
                transactionId = tx.id!!,
                status = tx.status.name,
            )
        } catch (ex: TransactionException) {
            CaptureTransactionResponse(
                transactionId = ex.transactionId,
                status = Status.FAILED.name,
                errorCode = ex.error.code,
                errorMessage = ex.error.message,
            )
        } catch (ex: Exception) {
            LOGGER.warn("Unexpected error", ex)
            CaptureTransactionResponse(
                transactionId = "",
                status = Status.FAILED.name,
                errorCode = ErrorCode.UNEXPECTED_ERROR.name,
            )
        }
}
