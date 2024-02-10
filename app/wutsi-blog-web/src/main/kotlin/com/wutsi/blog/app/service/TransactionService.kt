package com.wutsi.blog.app.service

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.wutsi.blog.app.backend.TransactionBackend
import com.wutsi.blog.app.exception.MobilePaymentNotSupportedForCountryException
import com.wutsi.blog.app.form.BuyForm
import com.wutsi.blog.app.form.DonateForm
import com.wutsi.blog.app.mapper.TransactionMapper
import com.wutsi.blog.app.model.TransactionModel
import com.wutsi.blog.country.dto.Country
import com.wutsi.blog.product.dto.SearchProductRequest
import com.wutsi.blog.transaction.dto.CaptureTransactionCommand
import com.wutsi.blog.transaction.dto.PaymentMethodType
import com.wutsi.blog.transaction.dto.SearchTransactionRequest
import com.wutsi.blog.transaction.dto.SubmitChargeCommand
import com.wutsi.blog.transaction.dto.SubmitDonationCommand
import com.wutsi.blog.user.dto.SearchUserRequest
import com.wutsi.platform.payment.core.Money
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
    private val storeService: StoreService,
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
        val money = getMoney(form.number, form.amount, wallet.currency)

        return backend.donate(
            SubmitDonationCommand(
                userId = user?.id,
                walletId = walletId,
                email = user?.email?.ifEmpty { null } ?: form.email,
                currency = money.currency,
                amount = money.value.toLong(),
                idempotencyKey = form.idempotencyKey,
                paymentMethodType = PaymentMethodType.MOBILE_MONEY,
                paymentNumber = form.number,
                paymentMethodOwner = user?.fullName?.ifEmpty { null } ?: form.fullName.ifEmpty { "-" },
            ),
        ).transactionId
    }

    fun buy(form: BuyForm): String {
        val product = productService.get(form.productId)
        val store = storeService.get(product.storeId)
        val money = if (form.paypal) {
            Money(product.offer.price.value.toDouble(), store.currency)
        } else {
            getMoney(form.number, product.offer.price.value, store.currency)
        }

        val user = requestContext.currentUser()
        return backend.charge(
            SubmitChargeCommand(
                productId = product.id,
                userId = user?.id,
                email = user?.email?.ifEmpty { null } ?: form.email,
                currency = money.currency,
                amount = money.value.toLong(),
                idempotencyKey = form.idempotencyKey,
                discountType = product.offer.discount?.type,
                paymentNumber = form.number,
                paymentMethodOwner = user?.fullName?.ifEmpty { null } ?: form.fullName.ifEmpty { "-" },
                paymentMethodType = if (product.offer.price.free) {
                    PaymentMethodType.NONE
                } else if (form.paypal) {
                    PaymentMethodType.PAYPAL
                } else {
                    PaymentMethodType.MOBILE_MONEY
                },
                internationalCurrency = product.offer.internationalPrice?.currency,
            )
        ).transactionId
    }

    fun capture(id: String) {
        backend.capture(CaptureTransactionCommand(transactionId = id))
    }

    private fun getMoney(number: String, amount: Long, defaultCurrency: String): Money {
        if (amount == 0L) {
            return Money(0.0, defaultCurrency)
        }

        // Country supported
        val country = Country.all.find { country -> number.startsWith("+${country.phoneNumberCode}") }
            ?: throw MobilePaymentNotSupportedForCountryException(number)

        // Validate phone number
        PhoneNumberUtil.getInstance().parse(number, country.code)

        // Money
        return Money(amount.toDouble(), country.currency)
    }

    fun search(limit: Int, offset: Int): List<TransactionModel> {
        val user = requestContext.currentUser() ?: return emptyList()
        val wallet = user.walletId?.let { walletService.get(it) } ?: return emptyList()

        return search(
            SearchTransactionRequest(
                statuses = if (requestContext.currentSuperUser() == null) {
                    listOf(Status.SUCCESSFUL, Status.FAILED)
                } else {
                    listOf(Status.SUCCESSFUL, Status.FAILED, Status.PENDING)
                },
                walletId = wallet.id,
                limit = limit,
                offset = offset,
            ),
        )
    }

    fun search(request: SearchTransactionRequest): List<TransactionModel> {
        val txs = backend.search(request).transactions

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

        val walletIds = txs.map { it.walletId }.toSet()
        val walletMap = if (walletIds.isEmpty()) {
            emptyMap()
        } else {
            walletIds.map { id -> walletService.get(id) }.associateBy { it.id }
        }

        val merchantIds = walletMap.values.map { it.userId }
        val merchantMap = if (merchantIds.isEmpty()) {
            emptyMap()
        } else {
            userService.search(
                SearchUserRequest(
                    userIds = merchantIds,
                    limit = merchantIds.size
                )
            ).associateBy { it.id }
        }

        return txs.map { tx ->
            mapper.toTransactionModel(
                tx = tx,
                wallet = walletMap[tx.walletId]!!,
                merchant = merchantMap[walletMap[tx.walletId]!!.userId]!!,
                product = tx.productId?.let { id -> productMap[id] }
            )
        }
    }
}
