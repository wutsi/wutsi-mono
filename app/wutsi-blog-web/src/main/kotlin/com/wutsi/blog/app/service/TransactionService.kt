package com.wutsi.blog.app.service

import com.wutsi.blog.app.backend.TransactionBackend
import com.wutsi.blog.app.form.BuyForm
import com.wutsi.blog.app.form.DonateForm
import com.wutsi.blog.app.mapper.TransactionMapper
import com.wutsi.blog.app.model.TransactionModel
import com.wutsi.blog.product.dto.SearchProductRequest
import com.wutsi.blog.transaction.dto.PaymentMethodType
import com.wutsi.blog.transaction.dto.SearchTransactionRequest
import com.wutsi.blog.transaction.dto.SubmitChargeCommand
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
    private val productService: ProductService,
) {
    fun get(id: String, sync: Boolean): TransactionModel {
        val tx = backend.get(id, sync).transaction
        val wallet = walletService.get(tx.walletId)
        val merchant = userService.get(wallet.userId)
        val product = tx.productId?.let { productService.get(it) }
        return mapper.toTransactionModel(tx, wallet, merchant, product)
    }

    fun donate(form: DonateForm): String {
        val walletId = userService.get(form.name).walletId!!
        val wallet = walletService.get(walletId)
        val user = requestContext.currentUser()

        return backend.donate(
            SubmitDonationCommand(
                userId = user?.id,
                walletId = walletId,
                email = user?.email?.ifEmpty { null } ?: form.email,
                currency = wallet.currency,
                amount = form.amount,
                idempotencyKey = form.idempotencyKey,
                paymentMethodType = PaymentMethodType.MOBILE_MONEY,
                paymentNumber = form.number,
                paymentMethodOwner = user?.fullName?.ifEmpty { null } ?: form.fullName.ifEmpty { "-" },
            ),
        ).transactionId
    }

    fun buy(form: BuyForm): String {
        val product = productService.get(form.productId)
        val user = requestContext.currentUser()

        return backend.charge(
            SubmitChargeCommand(
                productId = product.id,
                userId = user?.id,
                email = user?.email?.ifEmpty { null } ?: form.email,
                currency = product.price.currency,
                amount = form.amount,
                idempotencyKey = form.idempotencyKey,
                paymentMethodType = PaymentMethodType.MOBILE_MONEY,
                paymentNumber = form.number,
                paymentMethodOwner = user?.fullName?.ifEmpty { null } ?: form.fullName.ifEmpty { "-" },
            )
        ).transactionId
    }

    fun search(limit: Int, offset: Int): List<TransactionModel> {
        val user = requestContext.currentUser() ?: return emptyList()
        val wallet = user.walletId?.let { walletService.get(it) } ?: return emptyList()

        val txs = backend.search(
            SearchTransactionRequest(
                statuses = if (requestContext.currentSuperUser() == null) {
                    listOf(Status.SUCCESSFUL)
                } else {
                    listOf(Status.SUCCESSFUL, Status.FAILED, Status.PENDING)
                },
                walletId = wallet.id,
                limit = limit,
                offset = offset,
            ),
        ).transactions

        val productIds = txs.mapNotNull { it.productId }.toSet()
        val productMap = if (productIds.isEmpty()) {
            emptyMap()
        } else {
            productService.search(
                SearchProductRequest(
                    productIds = productIds.toList(),
                    limit = productIds.size
                )
            ).associateBy { it.id }
        }

        return txs.map { tx ->
            mapper.toTransactionModel(
                tx = tx,
                wallet = wallet,
                merchant = user,
                product = tx.productId?.let { productId -> productMap[productId] }
            )
        }
    }
}
