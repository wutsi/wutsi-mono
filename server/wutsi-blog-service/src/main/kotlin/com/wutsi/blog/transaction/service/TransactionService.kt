package com.wutsi.blog.transaction.service

import com.wutsi.blog.error.ErrorCode.TRANSACTION_NOT_FOUND
import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType.TRANSACTION_FAILED_EVENT
import com.wutsi.blog.event.EventType.TRANSACTION_NOTIFICATION_SUBMITTED_EVENT
import com.wutsi.blog.event.EventType.TRANSACTION_SUBMITTED_EVENT
import com.wutsi.blog.event.EventType.TRANSACTION_SUCCEEDED_EVENT
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.transaction.dao.SearchTransactionQueryBuilder
import com.wutsi.blog.transaction.dao.TransactionRepository
import com.wutsi.blog.transaction.domain.TransactionEntity
import com.wutsi.blog.transaction.dto.SearchTransactionRequest
import com.wutsi.blog.transaction.dto.SubmitCashoutCommand
import com.wutsi.blog.transaction.dto.SubmitDonationCommand
import com.wutsi.blog.transaction.dto.SubmitTransactionNotificationCommand
import com.wutsi.blog.transaction.dto.TransactionNotificationSubmittedEventPayload
import com.wutsi.blog.transaction.dto.TransactionType
import com.wutsi.blog.transaction.exception.TransactionException
import com.wutsi.blog.user.service.UserService
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
import com.wutsi.platform.payment.core.Money
import com.wutsi.platform.payment.core.Status
import com.wutsi.platform.payment.model.CreatePaymentRequest
import com.wutsi.platform.payment.model.CreateTransferRequest
import com.wutsi.platform.payment.model.Party
import org.springframework.stereotype.Service
import java.lang.Long.max
import java.math.RoundingMode
import java.util.Date
import java.util.UUID
import javax.persistence.EntityManager
import javax.transaction.Transactional

@Service
class TransactionService(
    private val dao: TransactionRepository,
    private val walletService: WalletService,
    private val eventStore: EventStore,
    private val eventStream: EventStream,
    private val userService: UserService,
    private val gatewayProvider: PaymentGatewayProvider,
    private val logger: KVLogger,
    private val tracingContext: TracingContext,
    private val em: EntityManager,
) {
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

    @Transactional(dontRollbackOn = [TransactionException::class])
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

        val opt = dao.findByIdempotencyKey(command.idempotencyKey) // Request already submitted?
        if (opt.isPresent) {
            logger.add("transaction_already_processed", true)
            return opt.get()
        }

        try {
            val tx = execute(command)
            logger.add("transaction_id", tx.id)
            logger.add("transaction_status", tx.status)

            this.notify(TRANSACTION_SUBMITTED_EVENT, tx.id!!, command.userId, command.timestamp)
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
                    amount = Money(command.amount.toDouble(), command.currency),
                    deviceId = tracingContext.deviceId(),
                    description = command.description ?: "",
                    payerMessage = null,
                    externalId = tx.id!!,
                    payer = Party(
                        fullName = command.paymentMethodOwner,
                        phoneNumber = tx.paymentMethodNumber,
                        email = tx.email,
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

    @Transactional(dontRollbackOn = [TransactionException::class])
    fun cashout(command: SubmitCashoutCommand): TransactionEntity {
        logger.add("request_amount", command.amount)
        logger.add("request_timestamp", command.timestamp)
        logger.add("request_idempotency_key", command.idempotencyKey)
        logger.add("request_wallet_id", command.walletId)

        val opt = dao.findByIdempotencyKey(command.idempotencyKey) // Request already submitted?
        if (opt.isPresent) {
            logger.add("transaction_already_processed", true)
            return opt.get()
        }

        try {
            val tx = execute(command)
            logger.add("transaction_id", tx.id)
            logger.add("transaction_status", tx.status)

            this.notify(TRANSACTION_SUBMITTED_EVENT, tx.id!!, null, command.timestamp)
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
                    amount = Money(tx.amount.toDouble(), wallet.currency),
                    payerMessage = null,
                    externalId = tx.id!!,
                    payee = Party(
                        fullName = tx.paymentMethodOwner,
                        phoneNumber = wallet.accountNumber!!,
                        email = tx.email,
                    ),
                    description = "",
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

        walletService.onTransactionSuccessful(tx.wallet, tx)
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
            if (tx.type == TransactionType.DONATION) {
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

        val evenPayload = EventPayload(eventId = eventId)
        eventStream.enqueue(type, evenPayload)
        eventStream.publish(type, evenPayload)
    }
}
