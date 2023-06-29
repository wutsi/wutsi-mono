package com.wutsi.blog.transaction.endpoint

import com.wutsi.blog.security.service.SecurityManager
import com.wutsi.blog.transaction.dto.SubmitCashoutCommand
import com.wutsi.blog.transaction.dto.SubmitCashoutResponse
import com.wutsi.blog.transaction.exception.TransactionException
import com.wutsi.blog.transaction.service.TransactionService
import com.wutsi.blog.transaction.service.WalletService
import com.wutsi.platform.payment.core.ErrorCode
import com.wutsi.platform.payment.core.Status
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/v1/transactions/commands/submit-cashout")
class SubmitCashoutCommandExecutor(
    private val service: TransactionService,
    private val securityManager: SecurityManager,
    private val walletService: WalletService,
) {
    @PostMapping()
    fun create(@RequestBody @Valid command: SubmitCashoutCommand): SubmitCashoutResponse {
        val wallet = walletService.findById(command.walletId)
        securityManager.checkUser(wallet.user.id!!)

        return try {
            val tx = service.cashout(command)
            SubmitCashoutResponse(
                transactionId = tx.id!!,
                status = tx.status.name,
            )
        } catch (ex: TransactionException) {
            SubmitCashoutResponse(
                transactionId = ex.transactionId,
                status = Status.FAILED.name,
                errorCode = ex.error.code,
                errorMessage = ex.error.message,
            )
        } catch (ex: Exception) {
            SubmitCashoutResponse(
                transactionId = "",
                status = Status.FAILED.name,
                errorCode = ErrorCode.UNEXPECTED_ERROR.name,
            )
        }
    }
}
