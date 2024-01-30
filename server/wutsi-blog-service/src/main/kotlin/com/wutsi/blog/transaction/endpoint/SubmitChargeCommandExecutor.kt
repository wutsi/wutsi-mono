package com.wutsi.blog.transaction.endpoint

import com.wutsi.blog.transaction.dto.SubmitChargeCommand
import com.wutsi.blog.transaction.dto.SubmitChargeResponse
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
@RequestMapping("/v1/transactions/commands/submit-charge")
class SubmitChargeCommandExecutor(
    private val service: TransactionService,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(SubmitChargeCommandExecutor::class.java)
    }

    @PostMapping
    fun create(@RequestBody @Valid command: SubmitChargeCommand): SubmitChargeResponse =
        try {
            val tx = service.charge(command)
            SubmitChargeResponse(
                transactionId = tx.id!!,
                status = tx.status.name,
            )
        } catch (ex: TransactionException) {
            SubmitChargeResponse(
                transactionId = ex.transactionId,
                status = Status.FAILED.name,
                errorCode = ex.error.code,
                errorMessage = ex.error.message,
            )
        } catch (ex: Exception) {
            LOGGER.warn("Unexpected error", ex)
            SubmitChargeResponse(
                transactionId = "",
                status = Status.FAILED.name,
                errorCode = ErrorCode.UNEXPECTED_ERROR.name,
            )
        }
}
