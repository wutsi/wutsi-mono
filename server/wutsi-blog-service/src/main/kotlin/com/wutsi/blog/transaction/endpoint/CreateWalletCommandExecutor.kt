package com.wutsi.blog.transaction.endpoint

import com.wutsi.blog.transaction.dto.CreateWalletCommand
import com.wutsi.blog.transaction.dto.CreateWalletResponse
import com.wutsi.blog.transaction.service.WalletService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/v1/wallets/commands/create")
class CreateWalletCommandExecutor(
    private val service: WalletService,
) {
    @PostMapping()
    fun create(@RequestBody @Valid command: CreateWalletCommand) = CreateWalletResponse(
        walletId = service.create(command).id!!,
    )
}
