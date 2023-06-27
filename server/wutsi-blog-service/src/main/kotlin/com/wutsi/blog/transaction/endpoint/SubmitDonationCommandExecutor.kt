package com.wutsi.blog.transaction.endpoint

import com.wutsi.blog.transaction.dto.SubmitDonationCommand
import com.wutsi.blog.transaction.dto.SubmitDonationResponse
import com.wutsi.blog.transaction.exception.TransactionException
import com.wutsi.blog.transaction.service.TransactionService
import com.wutsi.platform.payment.core.ErrorCode
import com.wutsi.platform.payment.core.Status
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/v1/transactions/commands/submit-donation")
class SubmitDonationCommandExecutor(
    private val service: TransactionService,
) {
    @PostMapping()
    fun create(@RequestBody @Valid command: SubmitDonationCommand): SubmitDonationResponse =
        try {
            val tx = service.donate(command)
            SubmitDonationResponse(
                transactionId = tx.id!!,
                status = tx.status.name,
            )
        } catch (ex: TransactionException) {
            SubmitDonationResponse(
                transactionId = ex.transactionId,
                status = Status.FAILED.name,
                errorCode = ex.error.code,
                errorMessage = ex.error.message,
            )
        } catch (ex: Exception) {
            SubmitDonationResponse(
                transactionId = "",
                status = Status.FAILED.name,
                errorCode = ErrorCode.UNEXPECTED_ERROR.name,
            )
        }
}
