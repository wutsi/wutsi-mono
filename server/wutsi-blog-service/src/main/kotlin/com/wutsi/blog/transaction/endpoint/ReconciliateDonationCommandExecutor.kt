package com.wutsi.blog.transaction.endpoint

import com.wutsi.blog.transaction.service.TransactionService
import com.wutsi.platform.payment.GatewayType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/transactions/commands/reconciliate-donation")
class ReconciliateDonationCommandExecutor(
    private val service: TransactionService,
//    private val securityManager: SecurityManager,
) {
    @GetMapping()
    fun reconciliate(
        @RequestParam("gateway-transaction-id") gatewayTransactionId: String,
        @RequestParam("wallet-id") walletId: String,
        @RequestParam("gateway-type") gatewayType: GatewayType,
    ) {
//        securityManager.checkSuperUser()
        service.reconciliate(gatewayTransactionId, walletId, gatewayType)
    }
}
