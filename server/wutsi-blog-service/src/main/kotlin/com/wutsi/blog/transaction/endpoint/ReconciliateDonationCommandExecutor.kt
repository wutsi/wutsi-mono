package com.wutsi.blog.transaction.endpoint

import com.wutsi.blog.security.service.SecurityManager
import com.wutsi.blog.transaction.dto.UpdateWalletAccountCommand
import com.wutsi.blog.transaction.service.TransactionService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/v1/wallets/commands/reconciliate-donation")
class ConciliateDonationCommandExecutor(
    private val service: TransactionService,
    private val securityManager: SecurityManager,
) {
    @PostMapping()
    fun conciliate(@RequestBody @Valid command: UpdateWalletAccountCommand) {
    }
}
