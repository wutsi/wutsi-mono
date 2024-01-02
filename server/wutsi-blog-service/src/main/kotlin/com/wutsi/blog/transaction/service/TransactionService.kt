package com.wutsi.blog.transaction.service

import com.wutsi.blog.error.ErrorCode.TRANSACTION_NOT_FOUND
import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType.TRANSACTION_FAILED_EVENT
import com.wutsi.blog.event.EventType.TRANSACTION_NOTIFICATION_SUBMITTED_EVENT
import com.wutsi.blog.event.EventType.TRANSACTION_RECONCILIATED_EVENT
import com.wutsi.blog.event.EventType.TRANSACTION_SUBMITTED_EVENT
import com.wutsi.blog.event.EventType.TRANSACTION_SUCCEEDED_EVENT
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.mail.service.MailService
import com.wutsi.blog.product.service.ProductService
import com.wutsi.blog.product.service.StoreService
import com.wutsi.blog.transaction.dao.SearchTransactionQueryBuilder
import com.wutsi.blog.transaction.dao.TransactionRepository
import com.wutsi.blog.transaction.domain.TransactionEntity
import com.wutsi.blog.transaction.domain.WalletEntity
import com.wutsi.blog.transaction.dto.SearchTransactionRequest
import com.wutsi.blog.transaction.dto.SubmitCashoutCommand
import com.wutsi.blog.transaction.dto.SubmitChargeCommand
import com.wutsi.blog.transaction.dto.SubmitDonationCommand
import com.wutsi.blog.transaction.dto.SubmitTransactionNotificationCommand
import com.wutsi.blog.transaction.dto.TransactionNotificationSubmittedEventPayload
import com.wutsi.blog.transaction.dto.TransactionType
import com.wutsi.blog.transaction.exception.TransactionException
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
import com.wutsi.platform.payment.GatewayType
import com.wutsi.platform.payment.PaymentException
import com.wutsi.platform.payment.core.ErrorCode
import com.wutsi.platform.payment.core.Money
import com.wutsi.platform.payment.core.Status
import com.wutsi.platform.payment.model.CreatePaymentRequest
import com.wutsi.platform.payment.model.CreateTransferRequest
import com.wutsi.platform.payment.model.Party
import jakarta.persistence.EntityManager
import org.slf4j.LoggerFactory
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
    private val storeService: StoreService,
    private val mailService: MailService,
    private val gatewayProvider: PaymentGatewayProvider,
    private val logger: KVLogger,
    private val tracingContext: TracingContext,
    private val em: EntityManager,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(TransactionService::class.java)
        const val DONATION_FEES_PERCENT = 0.1
        const val CHARGE_FEES_PERCENT = 0.2
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
    fun reconciliate(gatewayTransactionId: String, walletId: String, gatewayType: GatewayType) {
        val gateway = gatewayProvider.get(gatewayType)
        val payment = gateway.getPayment(gatewayTransactionId)

        // Recover the transaction
        val opt = dao.findById(payment.externalId)
        if (opt.isPresent) {
            return
        }

        val wallet = walletService.findById(payment.walletId ?: walletId)
        val user = try {
            payment.payer.id?.let { userId ->
                userService.findById(userId.toLong())
            }
        } catch (ex: Exception) {
            null
        }

        val tx = dao.save(
            TransactionEntity(
                id = payment.externalId,
                idempotencyKey = UUID.randomUUID().toString(),
                wallet = wallet,
                user = user,
                type = TransactionType.DONATION,
                currency = payment.amount.currency,
                description = payment.description,
                status = Status.PENDING,
                amount = payment.amount.value.toLong(),
                fees = 0,
                net = 0,
                paymentMethodType = wallet.accountType,
                paymentMethodNumber = payment.payer.phoneNumber,
                paymentMethodOwner = payment.payer.fullName,
                gatewayType = gateway.getType(),
                anonymous = false,
                email = payment.payer.email,
                creationDateTime = payment.creationDateTime ?: Date(),
                lastModificationDateTime = Date(),
                gatewayTransactionId = gatewayTransactionId,
            ),
        )
        logger.add("transaction_id", tx.id)
        logger.add("transaction_status", tx.status)

        // Sync
        if (tx.status == Status.PENDING) {
            this.notify(
                TRANSACTION_RECONCILIATED_EVENT,
                tx.id!!,
                tx.user?.id,
                tx.creationDateTime.time,
            )
            syncStatus(tx, System.currentTimeMillis())
        }
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

        val tx = dao.save(
            TransactionEntity(
                id = UUID.randomUUID().toString(),
                idempotencyKey = command.idempotencyKey,
                wallet = wallet,
                product = product,
                store = product.store,
                user = command.userId?.let { userService.findById(it) },
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
            ),
        )

        // Apply the charge
        try {
            val response = gateway.createPayment(
                request = CreatePaymentRequest(
                    walletId = tx.wallet.id,
                    amount = Money(command.amount.toDouble(), command.currency),
                    deviceId = tracingContext.deviceId(),
                    description = "",
                    payerMessage = null,
                    externalId = tx.id!!,
                    payer = Party(
                        fullName = command.paymentMethodOwner,
                        phoneNumber = tx.paymentMethodNumber,
                        email = tx.email,
                        id = tx.user?.id?.toString(),
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
        val gateway = gatewayProvider.get(command.paymentMethodType)
        val tx = dao.save(
            TransactionEntity(
                id = UUID.randomUUID().toString(),
                idempotencyKey = command.idempotencyKey,
                wallet = walletService.findById(command.walletId),
                user = command.userId?.let { userService.findById(it) },
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
            ),
        )

        // Apply the charge
        try {
            val response = gateway.createPayment(
                request = CreatePaymentRequest(
                    walletId = tx.wallet.id,
                    amount = Money(command.amount.toDouble(), command.currency),
                    deviceId = tracingContext.deviceId(),
                    description = command.description ?: "",
                    payerMessage = null,
                    externalId = tx.id!!,
                    payer = Party(
                        fullName = command.paymentMethodOwner,
                        phoneNumber = tx.paymentMethodNumber,
                        email = tx.email,
                        id = tx.user?.id?.toString(),
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
                currency = wallet.currency,
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
                    walletId = tx.wallet.id,
                    amount = Money(tx.amount.toDouble(), wallet.currency),
                    payerMessage = null,
                    externalId = tx.id!!,
                    payee = Party(
                        fullName = tx.paymentMethodOwner,
                        phoneNumber = wallet.accountNumber!!,
                        email = tx.email,
                        id = tx.user?.id?.toString(),
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

        walletService.onTransactionSuccessful(tx)
        if (tx.product != null) {
            productService.onTransactionSuccessful(tx.product)
            storeService.onTransactionSuccessful(tx.product.store)
        }
        mailService.onTransactionSuccessful(tx)
    }

    @Transactional
    fun onTransactionFailed(payload: EventPayload) {
        val event = eventStore.event(payload.eventId)
        val tx = findById(event.entityId, false)

        walletService.onTransactionFailed(tx.wallet, tx)
    }

    private fun syncStatus(tx: TransactionEntity, timestamp: Long): TransactionEntity {
        logger.add("transaction_status", tx.status)
        logger.add("transaction_type", tx.type)
        logger.add("transaction_gateway_type", tx.gatewayType)

        val gateway = gatewayProvider.get(tx.gatewayType)
        try {
            if (tx.type == TransactionType.DONATION || tx.type == TransactionType.CHARGE) {
                val response = gateway.getPayment(tx.gatewayTransactionId ?: "")
                logger.add("status", response.status)

                return syncStatus(tx, response.status, timestamp, response.fees.value.toLong())
            } else if (tx.type == TransactionType.CASHOUT) {
                val response = gateway.getTransfer(tx.gatewayTransactionId ?: "")
                logger.add("status", response.status)

                return syncStatus(tx, response.status, timestamp, response.fees.value.toLong())
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

    private fun syncStatus(
        tx: TransactionEntity,
        status: Status,
        timestamp: Long,
        gatewayFees: Long,
    ): TransactionEntity {
        logger.add("status", status)

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
            TransactionType.DONATION -> DONATION_FEES_PERCENT
            TransactionType.CHARGE -> CHARGE_FEES_PERCENT
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
}
