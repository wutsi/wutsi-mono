package com.wutsi.blog.app.service

import com.wutsi.blog.app.backend.TransactionBackend
import com.wutsi.blog.app.form.DonateForm
import com.wutsi.blog.app.mapper.TransactionMapper
import com.wutsi.blog.app.model.TransactionModel
import com.wutsi.blog.transaction.dto.PaymentMethodType
import com.wutsi.blog.transaction.dto.SearchTransactionRequest
import com.wutsi.blog.transaction.dto.SubmitDonationCommand
import com.wutsi.platform.payment.core.Status
import org.springframework.stereotype.Component

@Component
class TransactionService(
    private val backend: TransactionBackend,
    private val walletService: WalletService,
    private val userService: UserService,
    private val mapper: TransactionMapper,
    private val requestContext: RequestContext,
) {
    fun get(id: String, sync: Boolean): TransactionModel {
        val tx = backend.get(id, sync).transaction
        val wallet = walletService.get(tx.walletId)
        val merchant = userService.get(wallet.userId)
        return mapper.toTransactionModel(tx, wallet, merchant)
    }

    fun donate(form: DonateForm): String {
        val walletId = userService.get(form.name).walletId!!
        val wallet = walletService.get(walletId)
        val user = requestContext.currentUser()

        return backend.donate(
            SubmitDonationCommand(
                userId = user?.id,
                walletId = walletId,
                email = form.email,
                currency = wallet.currency,
                amount = form.amount,
                idempotencyKey = form.idempotencyKey,
                paymentMethodType = PaymentMethodType.MOBILE_MONEY,
                paymentNumber = form.number,
                paymentMethodOwner = form.fullName.ifEmpty { "-" },
            ),
        ).transactionId
    }

    fun search(limit: Int, offset: Int): List<TransactionModel> {
        val user = requestContext.currentUser() ?: return emptyList()
        val wallet = user.walletId?.let { walletService.get(it) } ?: return emptyList()

        val txs = backend.search(
            SearchTransactionRequest(
                statuses = listOf(Status.SUCCESSFUL, Status.FAILED),
                walletId = wallet.id,
                limit = limit,
                offset = offset,
            ),
        )
        return txs.transactions.map {
            mapper.toTransactionModel(it, wallet, user)
        }
    }
}
