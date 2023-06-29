package com.wutsi.blog.transaction.endpoint

import com.wutsi.blog.transaction.dto.SearchTransactionRequest
import com.wutsi.blog.transaction.dto.SearchTransactionResponse
import com.wutsi.blog.transaction.dto.TransactionSummary
import com.wutsi.blog.transaction.service.TransactionService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping
class SearchTransactionQuery(
    private val service: TransactionService,
) {
    @PostMapping("/v1/transactions/queries/search")
    fun create(@RequestBody @Valid request: SearchTransactionRequest): SearchTransactionResponse {
        val txs = service.search(request)
        return SearchTransactionResponse(
            transactions = txs.map { tx ->
                TransactionSummary(
                    id = tx.id ?: "",
                    amount = tx.amount,
                    currency = tx.currency,
                    walletId = tx.wallet.id!!,
                    paymentMethodType = tx.paymentMethodType,
                    paymentMethodOwner = tx.paymentMethodOwner,
                    userId = tx.user?.id ?: -1,
                    fees = tx.fees,
                    status = tx.status,
                    anonymous = tx.anonymous,
                    errorCode = tx.errorCode,
                    creationDateTime = tx.creationDateTime,
                    type = tx.type,
                    net = tx.net,
                )
            },
        )
    }
}
