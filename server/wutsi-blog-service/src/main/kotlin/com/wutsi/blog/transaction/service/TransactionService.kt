package com.wutsi.blog.transaction.service

import com.wutsi.blog.ads.service.AdsService
import com.wutsi.blog.country.dto.Country
import com.wutsi.blog.error.ErrorCode.TRANSACTION_NOT_FOUND
import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType
import com.wutsi.blog.event.EventType.TRANSACTION_FAILED_EVENT
import com.wutsi.blog.event.EventType.TRANSACTION_NOTIFICATION_SUBMITTED_EVENT
import com.wutsi.blog.event.EventType.TRANSACTION_SUBMITTED_EVENT
import com.wutsi.blog.event.EventType.TRANSACTION_SUCCEEDED_EVENT
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.mail.service.MailService
import com.wutsi.blog.product.dto.CreateBookCommand
import com.wutsi.blog.product.dto.ProductType
import com.wutsi.blog.product.exception.CouponException
import com.wutsi.blog.product.service.CouponService
import com.wutsi.blog.product.service.ExchangeRateService
import com.wutsi.blog.product.service.ProductService
import com.wutsi.blog.product.service.StoreService
import com.wutsi.blog.transaction.dao.SearchTransactionQueryBuilder
import com.wutsi.blog.transaction.dao.TransactionRepository
import com.wutsi.blog.transaction.domain.TransactionEntity
import com.wutsi.blog.transaction.domain.WalletEntity
import com.wutsi.blog.transaction.dto.CaptureTransactionCommand
import com.wutsi.blog.transaction.dto.SearchTransactionRequest
import com.wutsi.blog.transaction.dto.SubmitCashoutCommand
import com.wutsi.blog.transaction.dto.SubmitChargeCommand
import com.wutsi.blog.transaction.dto.SubmitDonationCommand
import com.wutsi.blog.transaction.dto.SubmitPaymentCommand
import com.wutsi.blog.transaction.dto.SubmitTransactionNotificationCommand
import com.wutsi.blog.transaction.dto.TransactionNotificationSubmittedEventPayload
import com.wutsi.blog.transaction.dto.TransactionType
import com.wutsi.blog.transaction.exception.TransactionException
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.blog.user.service.UserService
import com.wutsi.blog.util.DateUtils
import com.wutsi.blog.util.Predicates
import com.wutsi.event.store.Event
import com.wutsi.event.store.EventStore
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.exception.NotFoundException
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.platform.core.tracing.TracingContext
import com.wutsi.platform.payment.PaymentException
import com.wutsi.platform.payment.core.ErrorCode
import com.wutsi.platform.payment.core.Money
import com.wutsi.platform.payment.core.Status
import com.wutsi.platform.payment.model.CreatePaymentRequest
import com.wutsi.platform.payment.model.CreateTransferRequest
import com.wutsi.platform.payment.model.GetPaymentResponse
import com.wutsi.platform.payment.model.Party
import jakarta.persistence.EntityManager
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.lang.Long.max
import java.math.RoundingMode
import java.util.Date
import java.util.UUID

@Service
class TransactionService(
    private val dao: TransactionRepository,
    private val walletService: WalletService,
    private val eventStore: EventStore,
    private val eventStream: EventStream,
    private val userService: UserService,
    private val productService: ProductService,
    private val adsService: AdsService,
    private val storeService: StoreService,
    private val mailService: MailService,
    private val couponService: CouponService,
    private val gatewayProvider: PaymentGatewayProvider,
    private val logger: KVLogger,
    private val tracingContext: TracingContext,
    private val em: EntityManager,
    private val exchangeRateService: ExchangeRateService,

    @Value("\${wutsi.application.transaction.donation.fees-percentage}") val donationFeesPercent: Double,
    @Value("\${wutsi.application.transaction.charge.fees-percentage}") val chargeFeesPercent: Double,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(TransactionService::class.java)
    }

    fun search(request: SearchTransactionRequest): List<TransactionEntity> {
        val builder = SearchTransactionQueryBuilder()
        val sql = builder.query(request)
        val params = builder.parameters(request)
        val query = em.createNativeQuery(sql, TransactionEntity::class.java)
        Predicates.setParameters(query, params)

        return query.resultList as List<TransactionEntity>
    }

    @Transactional
    fun findById(id: String, sync: Boolean = false): TransactionEntity {
        val tx = dao.findById(id)
            .orElseThrow {
                NotFoundException(
                    Error(
                        code = TRANSACTION_NOT_FOUND,
                        parameter = Parameter(
                            name = "id",
                            value = id,
                        ),
                    ),
                )
            }

        return if (tx.status == Status.PENDING && sync) {
            syncStatus(tx, System.currentTimeMillis())
        } else {
            tx
        }
    }

    @Transactional(noRollbackFor = [TransactionException::class])
    fun charge(command: SubmitChargeCommand): TransactionEntity {
        logger.add("request_amount", command.amount)
        logger.add("request_user_id", command.userId)
        logger.add("request_timestamp", command.timestamp)
        logger.add("request_email", command.email)
        logger.add("request_idempotency_key", command.idempotencyKey)
        logger.add("request_product_id", command.productId)
        logger.add("request_currency", command.currency)
        logger.add("request_payment_method_type", command.paymentMethodType)
        logger.add("request_payment_number", command.paymentNumber)
        logger.add("request_payment_method_owner", command.paymentMethodOwner)
        logger.add("request_coupon_id", command.couponId)
        logger.add("command", "SubmitChargeCommand")

        val opt = dao.findByIdempotencyKey(command.idempotencyKey) // Request already submitted?
        if (opt.isPresent) {
            logger.add("transaction_already_processed", true)
            return opt.get()
        }

        try {
            val tx = execute(command)
            logger.add("transaction_id", tx.id)
            logger.add("transaction_status", tx.status)

            try {
                this.notify(TRANSACTION_SUBMITTED_EVENT, tx.id!!, command.userId, command.timestamp)
            } catch (ex: Exception) { // THIS WOULD BE REALLY BAD :-(
                LOGGER.warn("Unable to submit notification to the queue", ex)
            }

            return tx
        } catch (ex: TransactionException) {
            logger.add("transaction_id", ex.transactionId)
            logger.add("error_code", ex.error.code)
            logger.add("error_message", ex.error.message)
            logger.add("error_downstream_code", ex.error.downstreamCode)
            logger.setException(ex)

            this.notify(TRANSACTION_FAILED_EVENT, ex.transactionId, command.userId, command.timestamp)
            throw ex
        }
    }

    private fun execute(command: SubmitChargeCommand): TransactionEntity {
        // Create the transaction
        val gateway = gatewayProvider.get(command.paymentMethodType)
        val product = productService.findById(command.productId!!)
        val merchant = userService.findById(product.store.userId)
        val wallet = merchant.walletId?.let { walletService.findById(it) }
            ?: throw TransactionException(
                transactionId = "",
                error = Error(
                    code = ErrorCode.NOT_ALLOWED.name,
                    message = "Not allowed. The merchant do not have a wallet",
                ),
            )

        val exchangeRate = command.internationalCurrency?.let { currency ->
            exchangeRateService.getExchangeRate(command.currency, currency)
        }
        val tx = dao.save(
            TransactionEntity(
                id = UUID.randomUUID().toString(),
                idempotencyKey = command.idempotencyKey,
                wallet = wallet,
                product = product,
                store = product.store,
                user = resolveUser(command.userId, command.email, command.paymentNumber),
                type = TransactionType.CHARGE,
                currency = command.currency,
                status = Status.PENDING,
                amount = command.amount,
                fees = 0,
                net = 0,
                paymentMethodNumber = command.paymentNumber,
                paymentMethodType = command.paymentMethodType,
                paymentMethodOwner = command.paymentMethodOwner,
                gatewayType = gateway.getType(),
                email = command.email,
                creationDateTime = Date(),
                lastModificationDateTime = Date(),
                discountType = command.discountType,
                coupon = command.couponId?.let { couponId -> couponService.findById(couponId) },
                internationalCurrency = command.internationalCurrency,
                internationalAmount = exchangeRate?.let { exchangeRateService.convert(command.amount, it) }?.toLong(),
                exchangeRate = exchangeRate,
                channel = command.channel,
            ),
        )

        try {
            // Use the coupon
            if (tx.coupon != null) {
                couponService.use(tx)
            }

            // Perform the payment
            val response = gateway.createPayment(
                request = CreatePaymentRequest(
                    walletId = tx.wallet?.id,
                    amount = Money(
                        value = tx.internationalAmount?.toDouble() ?: command.amount.toDouble(),
                        currency = command.internationalCurrency ?: command.currency,
                    ),
                    deviceId = tracingContext.deviceId(),
                    description = product.title,
                    payerMessage = null,
                    externalId = tx.id!!,
                    payer = Party(
                        fullName = command.paymentMethodOwner,
                        phoneNumber = tx.paymentMethodNumber,
                        email = tx.email,
                        id = tx.user?.id?.toString(),
                        country = Country.fromPhoneNumber(tx.paymentMethodNumber)?.code
                    ),
                ),
            )
            tx.gatewayTransactionId = response.transactionId
            return dao.save(tx)
        } catch (ex: PaymentException) {
            handlePaymentException(tx, ex)
            throw TransactionException(
                transactionId = tx.id!!,
                error = Error(
                    code = ex.error.code.name,
                    message = ex.error.message,
                    downstreamCode = ex.error.supplierErrorCode,
                ),
                cause = ex,
            )
        } catch (ex: CouponException) {
            handleCouponException(tx, ex)
            throw TransactionException(
                transactionId = tx.id!!,
                error = Error(
                    code = ex.error.code,
                    message = ex.error.code
                ),
                cause = ex,
            )
        }
    }

    @Transactional(noRollbackFor = [TransactionException::class])
    fun donate(command: SubmitDonationCommand): TransactionEntity {
        logger.add("request_amount", command.amount)
        logger.add("request_user_id", command.userId)
        logger.add("request_timestamp", command.timestamp)
        logger.add("request_email", command.email)
        logger.add("request_idempotency_key", command.idempotencyKey)
        logger.add("request_anonymous", command.anonymous)
        logger.add("request_description", command.description)
        logger.add("request_wallet_id", command.walletId)
        logger.add("request_currency", command.currency)
        logger.add("request_payment_method_type", command.paymentMethodType)
        logger.add("request_payment_number", command.paymentNumber)
        logger.add("request_payment_method_owner", command.paymentMethodOwner)
        logger.add("command", "SubmitDonationCommand")

        val opt = dao.findByIdempotencyKey(command.idempotencyKey) // Request already submitted?
        if (opt.isPresent) {
            logger.add("transaction_already_processed", true)
            return opt.get()
        }

        try {
            val tx = execute(command)
            logger.add("transaction_id", tx.id)
            logger.add("transaction_status", tx.status)

            try {
                this.notify(TRANSACTION_SUBMITTED_EVENT, tx.id!!, command.userId, command.timestamp)
            } catch (ex: Exception) { // THIS WOULD BE REALLY BAD :-(
                LOGGER.warn("Unable to submit notification to the queue", ex)
            }

            return tx
        } catch (ex: TransactionException) {
            logger.add("transaction_id", ex.transactionId)
            logger.add("error_code", ex.error.code)
            logger.add("error_message", ex.error.message)
            logger.add("error_downstream_code", ex.error.downstreamCode)
            logger.setException(ex)

            this.notify(TRANSACTION_FAILED_EVENT, ex.transactionId, command.userId, command.timestamp)
            throw ex
        }
    }

    fun execute(command: SubmitDonationCommand): TransactionEntity {
        // Create the transaction
        val exchangeRate = command.internationalCurrency?.let { currency ->
            exchangeRateService.getExchangeRate(command.currency, currency)
        }
        val gateway = gatewayProvider.get(command.paymentMethodType)
        val tx = dao.save(
            TransactionEntity(
                id = UUID.randomUUID().toString(),
                idempotencyKey = command.idempotencyKey,
                wallet = walletService.findById(command.walletId),
                user = resolveUser(command.userId, command.email, command.paymentNumber),
                type = TransactionType.DONATION,
                currency = command.currency,
                description = command.description,
                status = Status.PENDING,
                amount = command.amount,
                fees = 0,
                net = 0,
                paymentMethodNumber = command.paymentNumber,
                paymentMethodType = command.paymentMethodType,
                paymentMethodOwner = command.paymentMethodOwner,
                gatewayType = gateway.getType(),
                anonymous = command.anonymous,
                email = command.email,
                creationDateTime = Date(),
                lastModificationDateTime = Date(),
                internationalCurrency = command.internationalCurrency,
                internationalAmount = exchangeRate?.let { exchangeRateService.convert(command.amount, it) }?.toLong(),
                exchangeRate = exchangeRate,
                channel = command.channel,
            ),
        )

        // Apply the charge
        try {
            val response = gateway.createPayment(
                request = CreatePaymentRequest(
                    walletId = tx.wallet?.id,
                    amount = Money(
                        value = tx.internationalAmount?.toDouble() ?: command.amount.toDouble(),
                        currency = command.internationalCurrency ?: command.currency,
                    ),
                    deviceId = tracingContext.deviceId(),
                    description = command.description ?: "",
                    payerMessage = null,
                    externalId = tx.id!!,
                    payer = Party(
                        fullName = command.paymentMethodOwner,
                        phoneNumber = tx.paymentMethodNumber,
                        email = tx.email,
                        id = tx.user?.id?.toString(),
                        country = Country.fromPhoneNumber(tx.paymentMethodNumber)?.code
                    ),
                ),
            )

            tx.gatewayTransactionId = response.transactionId
            return dao.save(tx)
        } catch (ex: PaymentException) {
            handlePaymentException(tx, ex)
            throw TransactionException(
                transactionId = tx.id!!,
                error = Error(
                    code = ex.error.code.name,
                    message = ex.error.message,
                    downstreamCode = ex.error.supplierErrorCode,
                ),
                cause = ex,
            )
        }
    }

    @Transactional(noRollbackFor = [TransactionException::class])
    fun capture(command: CaptureTransactionCommand): TransactionEntity {
        logger.add("request_transaction_id", command.transactionId)
        logger.add("command", "CaptureChargeCommand")

        try {
            return execute(command)
        } catch (ex: TransactionException) {
            logger.add("transaction_id", ex.transactionId)
            logger.add("error_code", ex.error.code)
            logger.add("error_message", ex.error.message)
            logger.add("error_downstream_code", ex.error.downstreamCode)
            logger.setException(ex)

            this.notify(TRANSACTION_FAILED_EVENT, ex.transactionId, null, command.timestamp)
            throw ex
        }
    }

    private fun execute(command: CaptureTransactionCommand): TransactionEntity {
        val tx = findById(command.transactionId, false)
        try {
            gatewayProvider.get(tx.gatewayType).capturePayment(tx.gatewayTransactionId ?: "")
            return tx
        } catch (ex: PaymentException) {
            handlePaymentException(tx, ex)
            throw TransactionException(
                transactionId = tx.id!!,
                error = Error(
                    code = ex.error.code.name,
                    message = ex.error.message,
                    downstreamCode = ex.error.supplierErrorCode,
                ),
                cause = ex,
            )
        }
    }

    @Transactional(noRollbackFor = [TransactionException::class])
    fun cashout(command: SubmitCashoutCommand): TransactionEntity {
        logger.add("request_amount", command.amount)
        logger.add("request_timestamp", command.timestamp)
        logger.add("request_idempotency_key", command.idempotencyKey)
        logger.add("request_wallet_id", command.walletId)
        logger.add("command", "SubmitCashoutCommand")

        val opt = dao.findByIdempotencyKey(command.idempotencyKey) // Request already submitted?
        if (opt.isPresent) {
            logger.add("transaction_already_processed", true)
            return opt.get()
        }

        try {
            val tx = execute(command)
            logger.add("transaction_id", tx.id)
            logger.add("transaction_status", tx.status)

            try {
                this.notify(TRANSACTION_SUBMITTED_EVENT, tx.id!!, null, command.timestamp)
            } catch (ex: Exception) { // THIS WOULD BE REALLY BAD :-(
                LOGGER.warn("Unable to submit notification to the queue", ex)
            }

            return tx
        } catch (ex: TransactionException) {
            logger.add("transaction_id", ex.transactionId)
            logger.add("error_code", ex.error.code)
            logger.add("error_message", ex.error.message)
            logger.add("error_downstream_code", ex.error.downstreamCode)
            logger.setException(ex)

            this.notify(TRANSACTION_FAILED_EVENT, ex.transactionId, null, command.timestamp)
            throw ex
        }
    }

    private fun execute(command: SubmitCashoutCommand): TransactionEntity {
        // Record transaction
        val wallet = walletService.findById(command.walletId)
        val gateway = gatewayProvider.get(wallet.accountType)
        val tx = dao.save(
            TransactionEntity(
                id = UUID.randomUUID().toString(),
                idempotencyKey = command.idempotencyKey,
                wallet = wallet,
                type = TransactionType.CASHOUT,
                currency = command.currency,
                description = null,
                status = Status.PENDING,
                amount = command.amount,
                fees = 0,
                net = 0,
                paymentMethodNumber = wallet.accountNumber!!,
                paymentMethodType = wallet.accountType,
                paymentMethodOwner = wallet.accountOwner ?: wallet.user.fullName,
                gatewayType = gateway.getType(),
                email = wallet.user.email,
                creationDateTime = Date(),
                lastModificationDateTime = Date(),
            ),
        )

        try {
            // Update wallet
            walletService.prepareCashout(wallet, command.amount)

            // Transfer
            val response = gateway.createTransfer(
                request = CreateTransferRequest(
                    walletId = tx.wallet?.id,
                    amount = Money(tx.amount.toDouble(), wallet.currency),
                    payerMessage = null,
                    externalId = tx.id!!,
                    payee = Party(
                        fullName = tx.paymentMethodOwner,
                        phoneNumber = wallet.accountNumber!!,
                        email = tx.email,
                        id = tx.user?.id?.toString(),
                        country = Country.fromPhoneNumber(wallet.accountNumber!!)?.code
                    ),
                    description = "",
                    sender = Party(
                        fullName = "Wutsi",
                    ),
                ),
            )

            tx.gatewayTransactionId = response.transactionId
            dao.save(tx)
            return tx
        } catch (ex: PaymentException) {
            handlePaymentException(tx, ex)
            throw TransactionException(
                transactionId = tx.id!!,
                error = Error(
                    code = ex.error.code.name,
                    message = ex.error.message,
                    downstreamCode = ex.error.supplierErrorCode,
                ),
                cause = ex,
            )
        }
    }

    @Transactional(noRollbackFor = [TransactionException::class])
    fun pay(command: SubmitPaymentCommand): TransactionEntity {
        logger.add("request_amount", command.amount)
        logger.add("request_user_id", command.userId)
        logger.add("request_timestamp", command.timestamp)
        logger.add("request_email", command.email)
        logger.add("request_idempotency_key", command.idempotencyKey)
        logger.add("request_currency", command.currency)
        logger.add("request_payment_method_type", command.paymentMethodType)
        logger.add("request_payment_number", command.paymentNumber)
        logger.add("request_payment_method_owner", command.paymentMethodOwner)
        logger.add("command", "SubmitPaymentCommand")

        val opt = dao.findByIdempotencyKey(command.idempotencyKey) // Request already submitted?
        if (opt.isPresent) {
            logger.add("transaction_already_processed", true)
            return opt.get()
        }

        try {
            val tx = execute(command)
            logger.add("transaction_id", tx.id)
            logger.add("transaction_status", tx.status)

            try {
                this.notify(TRANSACTION_SUBMITTED_EVENT, tx.id!!, command.userId, command.timestamp)
            } catch (ex: Exception) { // THIS WOULD BE REALLY BAD :-(
                LOGGER.warn("Unable to submit notification to the queue", ex)
            }

            return tx
        } catch (ex: TransactionException) {
            logger.add("transaction_id", ex.transactionId)
            logger.add("error_code", ex.error.code)
            logger.add("error_message", ex.error.message)
            logger.add("error_downstream_code", ex.error.downstreamCode)
            logger.setException(ex)

            this.notify(TRANSACTION_FAILED_EVENT, ex.transactionId, command.userId, command.timestamp)
            throw ex
        }
    }

    private fun execute(command: SubmitPaymentCommand): TransactionEntity {
        // Create the transaction
        val gateway = gatewayProvider.get(command.paymentMethodType)
        val ads = adsService.findById(command.adsId!!)
        val exchangeRate = command.internationalCurrency?.let { currency ->
            exchangeRateService.getExchangeRate(command.currency, currency)
        }

        val tx = dao.save(
            TransactionEntity(
                id = UUID.randomUUID().toString(),
                idempotencyKey = command.idempotencyKey,
                ads = ads,
                user = resolveUser(command.userId, command.email, command.paymentNumber),
                type = TransactionType.PAYMENT,
                currency = command.currency,
                status = Status.PENDING,
                amount = command.amount,
                fees = 0,
                net = 0,
                paymentMethodNumber = command.paymentNumber,
                paymentMethodType = command.paymentMethodType,
                paymentMethodOwner = command.paymentMethodOwner,
                gatewayType = gateway.getType(),
                email = command.email,
                creationDateTime = Date(),
                lastModificationDateTime = Date(),
                internationalCurrency = command.internationalCurrency,
                internationalAmount = exchangeRate?.let { exchangeRateService.convert(command.amount, it) }?.toLong(),
                exchangeRate = exchangeRate,
                channel = command.channel,
            ),
        )

        try {
            // Perform the payment
            val response = gateway.createPayment(
                request = CreatePaymentRequest(
                    amount = Money(
                        value = tx.internationalAmount?.toDouble() ?: command.amount.toDouble(),
                        currency = command.internationalCurrency ?: command.currency,
                    ),
                    deviceId = tracingContext.deviceId(),
                    description = ads.title,
                    payerMessage = null,
                    externalId = tx.id!!,
                    payer = Party(
                        fullName = command.paymentMethodOwner,
                        phoneNumber = tx.paymentMethodNumber,
                        email = tx.email,
                        id = tx.user?.id?.toString(),
                        country = Country.fromPhoneNumber(tx.paymentMethodNumber)?.code
                    ),
                ),
            )
            tx.gatewayTransactionId = response.transactionId
            return dao.save(tx)
        } catch (ex: PaymentException) {
            handlePaymentException(tx, ex)
            throw TransactionException(
                transactionId = tx.id!!,
                error = Error(
                    code = ex.error.code.name,
                    message = ex.error.message,
                    downstreamCode = ex.error.supplierErrorCode,
                ),
                cause = ex,
            )
        } catch (ex: CouponException) {
            handleCouponException(tx, ex)
            throw TransactionException(
                transactionId = tx.id!!,
                error = Error(
                    code = ex.error.code,
                    message = ex.error.code
                ),
                cause = ex,
            )
        }
    }

    fun computeCashoutAmount(wallet: WalletEntity): Long {
        val txs = search(
            SearchTransactionRequest(
                walletId = wallet.id,
                statuses = listOf(Status.SUCCESSFUL),
                creationDateTimeFrom = DateUtils.addDays(
                    DateUtils.beginingOfTheDay(Date()),
                    -2,
                ), // Ignore transactions of the past 2 days
                types = listOf(TransactionType.DONATION, TransactionType.CHARGE),
                limit = Int.MAX_VALUE,
            ),
        )
        return wallet.balance - txs.sumOf { it.net }
    }

    @Transactional
    fun notify(command: SubmitTransactionNotificationCommand) {
        notify(
            TRANSACTION_NOTIFICATION_SUBMITTED_EVENT,
            command.transactionId,
            null,
            command.timestamp,
            TransactionNotificationSubmittedEventPayload(
                message = command.message,
            ),
        )
    }

    @Transactional
    fun onNotification(payload: EventPayload) {
        val event = eventStore.event(payload.eventId)
        val tx = findById(event.entityId)
        syncStatus(tx, event.timestamp.time)
    }

    @Transactional
    fun onTransactionSuccessful(payload: EventPayload) {
        val event = eventStore.event(payload.eventId)
        val tx = findById(event.entityId, false)

        if (tx.product != null) {
            val product = tx.product
            productService.onTransactionSuccessful(product)
            storeService.onTransactionSuccessful(product.store)

            if (product.type == ProductType.EBOOK) {
                eventStream.enqueue(EventType.CREATE_BOOK_COMMAND, CreateBookCommand(tx.id ?: ""))
            }
        }

        if (tx.ads != null) {
            adsService.onTransactionSuccessful(tx)
        }

        if (tx.wallet != null) {
            walletService.onTransactionSuccessful(tx)
            userService.onTransactionSuccesfull(tx.wallet.user)
        }

        mailService.onTransactionSuccessful(tx)
    }

    @Transactional
    fun onTransactionFailed(payload: EventPayload) {
        val event = eventStore.event(payload.eventId)
        val tx = findById(event.entityId, false)

        tx.wallet?.let { w -> walletService.onTransactionFailed(tx.wallet, tx) }
        tx.coupon?.let { couponService.onTransactionFailed(tx) }
    }

    private fun syncStatus(tx: TransactionEntity, timestamp: Long): TransactionEntity {
        val gateway = gatewayProvider.get(tx.gatewayType)
        try {
            when (tx.type) {
                TransactionType.DONATION,
                TransactionType.CHARGE,
                TransactionType.PAYMENT,
                -> {
                    val response = gateway.getPayment(tx.gatewayTransactionId ?: "")
                    syncUser(tx, response)
                    logger.add("status", response.status)

                    return syncStatus(tx, response.status, timestamp, response.fees.value.toLong())
                }

                TransactionType.CASHOUT -> {
                    val response = gateway.getTransfer(tx.gatewayTransactionId ?: "")
                    logger.add("status", response.status)

                    return syncStatus(tx, response.status, timestamp, response.fees.value.toLong())
                }

                else -> {}
            }
        } catch (ex: PaymentException) {
            val updatedTx = handlePaymentException(tx, ex)
            if (updatedTx != null) {
                this.notify(TRANSACTION_FAILED_EVENT, tx.id ?: "", null, timestamp)
                return updatedTx
            }
        }

        return tx
    }

    private fun syncUser(tx: TransactionEntity, response: GetPaymentResponse) {
        if (response.status == Status.SUCCESSFUL && tx.user == null) {
            val user = resolveUser(
                id = null,
                email = response.payer.email,
                country = response.payer.country,
                fullName = response.payer.fullName
            )
            tx.user = user
            if (tx.paymentMethodOwner.isEmpty() || tx.paymentMethodOwner.equals("-")) {
                tx.paymentMethodOwner = user?.fullName ?: ""
            }
            if (tx.email.isNullOrEmpty()) {
                tx.email = user?.email
            }
        }
    }

    private fun syncStatus(
        tx: TransactionEntity,
        status: Status,
        timestamp: Long,
        gatewayFees: Long,
    ): TransactionEntity {
        if (status == Status.SUCCESSFUL) {
            val updatedTx = handleSuccess(tx, gatewayFees)
            if (updatedTx != null) {
                this.notify(TRANSACTION_SUCCEEDED_EVENT, tx.id ?: "", null, timestamp)
                return updatedTx
            }
        }
        return tx
    }

    private fun handleSuccess(tx: TransactionEntity, gatewayFees: Long): TransactionEntity? {
        if (isFinal(tx.status)) {
            return null
        }

        val feePercent = when (tx.type) {
            TransactionType.DONATION -> donationFeesPercent
            TransactionType.CHARGE -> chargeFeesPercent
            else -> 0.0
        }
        val fees = (feePercent * tx.amount).toBigDecimal().setScale(0, RoundingMode.HALF_UP).toLong()

        tx.status = Status.SUCCESSFUL
        tx.gatewayFees = gatewayFees
        tx.fees = fees
        tx.net = max(0, tx.amount - fees)
        tx.lastModificationDateTime = Date()
        return dao.save(tx)
    }

    private fun handlePaymentException(tx: TransactionEntity, ex: PaymentException): TransactionEntity? {
        if (isFinal(tx.status)) {
            return null
        }

        tx.status = Status.FAILED
        tx.errorCode = ex.error.code.name
        tx.errorMessage = ex.error.message
        tx.supplierErrorCode = ex.error.supplierErrorCode
        tx.gatewayTransactionId = ex.error.transactionId.ifEmpty { null }
        tx.lastModificationDateTime = Date()
        return dao.save(tx)
    }

    private fun handleCouponException(tx: TransactionEntity, ex: CouponException): TransactionEntity? {
        if (isFinal(tx.status)) {
            return null
        }

        tx.status = Status.FAILED
        tx.errorCode = ex.error.code
        tx.lastModificationDateTime = Date()
        return dao.save(tx)
    }

    private fun isFinal(status: Status): Boolean =
        status == Status.FAILED || status == Status.SUCCESSFUL

    private fun notify(type: String, transactionId: String, userId: Long?, timestamp: Long, payload: Any? = null) {
        val eventId = eventStore.store(
            Event(
                streamId = StreamId.TRANSACTION,
                type = type,
                entityId = transactionId,
                userId = userId?.toString(),
                payload = payload,
                timestamp = Date(timestamp),
            ),
        )
        logger.add("evt_id", eventId)

        val evenPayload = EventPayload(eventId = eventId)
        eventStream.enqueue(type, evenPayload)
        eventStream.publish(type, evenPayload)
    }

    private fun resolveUser(
        id: Long?,
        email: String?,
        phoneNumber: String,
    ): UserEntity? =
        resolveUser(
            id = id,
            email = email,
            country = Country.fromPhoneNumber(phoneNumber)?.code
        )

    private fun resolveUser(
        id: Long?,
        email: String?,
        country: String?,
        fullName: String = "",
    ): UserEntity? =
        if (id != null) {
            userService.findById(id)
        } else if (!email.isNullOrEmpty()) {
            userService.findByEmailOrCreate(email, country, fullName)
        } else {
            null
        }
}
