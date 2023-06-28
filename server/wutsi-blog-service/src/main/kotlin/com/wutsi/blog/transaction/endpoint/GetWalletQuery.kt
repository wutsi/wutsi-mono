package com.wutsi.blog.transaction.endpoint

import com.wutsi.blog.transaction.dto.GetWalletResponse
import com.wutsi.blog.transaction.dto.Wallet
import com.wutsi.blog.transaction.dto.WalletAccount
import com.wutsi.blog.transaction.service.WalletService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class GetWalletQuery(
    private val service: WalletService,
) {
    @GetMapping("/v1/wallets/{id}")
    fun create(@PathVariable id: String): GetWalletResponse {
        val wallet = service.findById(id)
        return GetWalletResponse(
            wallet = Wallet(
                id = id,
                userId = wallet.user.id!!,
                balance = wallet.balance,
                country = wallet.country,
                currency = wallet.currency,
                donationCount = wallet.donationCount,
                creationDateTime = wallet.creationDateTime,
                lastModificationDateTime = wallet.lastModificationDateTime,
                nextCashoutDate = wallet.nextCashoutDate,
                lastCashoutDateTime = wallet.lastModificationDateTime,
                account = if (wallet.accountNumber.isNullOrEmpty()) {
                    null
                } else {
                    WalletAccount(
                        number = wallet.accountNumber,
                        type = wallet.accountType,
                        owner = wallet.accountNumber,
                    )
                }
            ),
        )
    }
}
