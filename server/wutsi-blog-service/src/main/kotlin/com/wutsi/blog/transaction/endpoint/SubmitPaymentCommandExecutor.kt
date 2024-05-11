package com.wutsi.blog.transaction.endpoint

import com.wutsi.blog.transaction.dto.SubmitPaymentCommand
import com.wutsi.blog.transaction.dto.SubmitPaymentResponse
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
@RequestMapping("/v1/transactions/commands/submit-payment")
class SubmitPaymentCommandExecutor(
    private val service: TransactionService,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(SubmitPaymentCommandExecutor::class.java)
    }

    @PostMapping
    fun create(@RequestBody @Valid command: SubmitPaymentCommand): SubmitPaymentResponse =
        try {
            val tx = service.pay(command)
            SubmitPaymentResponse(
                transactionId = tx.id!!,
                status = tx.status.name,
            )
        } catch (ex: TransactionException) {
            SubmitPaymentResponse(
                transactionId = ex.transactionId,
                status = Status.FAILED.name,
                errorCode = ex.error.code,
                errorMessage = ex.error.message,
            )
        } catch (ex: Exception) {
            LOGGER.warn("Unexpected error", ex)
            SubmitPaymentResponse(
                transactionId = "",
                status = Status.FAILED.name,
                errorCode = ErrorCode.UNEXPECTED_ERROR.name,
            )
        }
}
