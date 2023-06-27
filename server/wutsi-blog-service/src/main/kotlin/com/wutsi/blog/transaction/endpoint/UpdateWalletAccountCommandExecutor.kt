package com.wutsi.blog.transaction.endpoint

import com.wutsi.blog.transaction.dto.UpdateWalletAccountCommand
import com.wutsi.blog.transaction.service.WalletService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/v1/wallets/commands/update-account")
class UpdateWalletAccountCommandExecutor(
    private val service: WalletService,
) {
    @PostMapping()
    fun create(@RequestBody @Valid command: UpdateWalletAccountCommand) {
        service.updateAccount(command)
    }
}
