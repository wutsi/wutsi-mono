package com.wutsi.blog.transaction.service

import com.wutsi.blog.country.dto.Country
import com.wutsi.blog.error.ErrorCode.COUNTRY_DONT_SUPPORT_WALLET
import com.wutsi.blog.error.ErrorCode.USER_DONT_SUPPORT_WALLET
import com.wutsi.blog.error.ErrorCode.WALLET_ALREADY_CREATED
import com.wutsi.blog.error.ErrorCode.WALLET_NOT_FOUND
import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType.WALLET_CREATED_EVENT
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.transaction.dao.TransactionRepository
import com.wutsi.blog.transaction.dao.WalletRepository
import com.wutsi.blog.transaction.domain.WalletEntity
import com.wutsi.blog.transaction.dto.CreateWalletCommand
import com.wutsi.blog.transaction.dto.TransactionType.CASHOUT
import com.wutsi.blog.transaction.dto.TransactionType.DONATION
import com.wutsi.blog.transaction.dto.WalletCreatedEventPayload
import com.wutsi.blog.user.service.UserService
import com.wutsi.event.store.Event
import com.wutsi.event.store.EventStore
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.exception.ConflictException
import com.wutsi.platform.core.error.exception.NotFoundException
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.platform.payment.core.Status.SUCCESSFUL
import org.springframework.stereotype.Service
import java.util.Date
import java.util.UUID
import javax.transaction.Transactional

@Service
class WalletService(
    private val dao: WalletRepository,
    private val transactionDao: TransactionRepository,
    private val eventStore: EventStore,
    private val eventStream: EventStream,
    private val userService: UserService,
    private val logger: KVLogger,
) {
    fun findById(id: String): WalletEntity =
        dao.findById(id).orElseThrow {
            NotFoundException(
                Error(
                    code = WALLET_NOT_FOUND,
                    parameter = Parameter(
                        value = id,
                    ),
                ),
            )
        }

    @Transactional
    fun onTransactionSuccessful(wallet: WalletEntity) {
        wallet.balance = java.lang.Long.max(
            0,
            (transactionDao.sumNetByWalletAndTypeAndStatus(wallet, DONATION, SUCCESSFUL) ?: 0) -
                (transactionDao.sumNetByWalletAndTypeAndStatus(wallet, CASHOUT, SUCCESSFUL) ?: 0),
        )
        wallet.donationCount =
            transactionDao.countByWalletAndTypeAndStatus(wallet, DONATION, SUCCESSFUL)
        wallet.lastModificationDateTime = Date()
        dao.save(wallet)

        logger.add("wallet_id", wallet.id)
        logger.add("user_id", wallet.user.id)
        logger.add("wallet_balance", wallet.balance)
    }

    @Transactional
    fun create(command: CreateWalletCommand): WalletEntity {
        logger.add("request_country", command.country)
        logger.add("request_user_id", command.userId)

        // Validation
        val wallet = execute(command)
        notify(
            type = WALLET_CREATED_EVENT,
            walletId = wallet.id!!,
            userId = command.userId,
            timestamp = command.timestamp,
            payload = WalletCreatedEventPayload(
                country = command.country,
            ),
        )

        return wallet
    }

    private fun execute(command: CreateWalletCommand): WalletEntity {
        // Validation
        val user = userService.findById(command.userId)
        if (!user.blog) {
            throw ConflictException(Error(USER_DONT_SUPPORT_WALLET))
        }
        val country = Country.all.find { it.code == command.country }
            ?: throw ConflictException(Error(COUNTRY_DONT_SUPPORT_WALLET))

        // Wallet
        val opt = dao.findByUser(user)
        if (opt.isPresent) {
            throw ConflictException(Error(WALLET_ALREADY_CREATED))
        }
        return dao.save(
            WalletEntity(
                id = UUID.randomUUID().toString(),
                user = user,
                country = command.country,
                currency = country.currency,
            ),
        )
    }

    @Transactional
    fun onWalletCreated(payload: EventPayload) {
        val event = eventStore.event(payload.eventId)
        event.userId?.let {
            userService.onWalletCreated(it.toLong(), event.entityId)
        }
    }

    private fun notify(type: String, walletId: String, userId: Long?, timestamp: Long, payload: Any? = null) {
        val eventId = eventStore.store(
            Event(
                streamId = StreamId.WALLET,
                type = type,
                entityId = walletId,
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
