package com.wutsi.blog.transaction.service

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.wutsi.blog.country.dto.Country
import com.wutsi.blog.error.ErrorCode.COUNTRY_DONT_SUPPORT_WALLET
import com.wutsi.blog.error.ErrorCode.USER_DONT_SUPPORT_WALLET
import com.wutsi.blog.error.ErrorCode.WALLET_NOT_FOUND
import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType.WALLET_ACCOUNT_UPDATED_EVENT
import com.wutsi.blog.event.EventType.WALLET_CREATED_EVENT
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.transaction.dao.TransactionRepository
import com.wutsi.blog.transaction.dao.WalletRepository
import com.wutsi.blog.transaction.domain.TransactionEntity
import com.wutsi.blog.transaction.domain.WalletEntity
import com.wutsi.blog.transaction.dto.CreateWalletCommand
import com.wutsi.blog.transaction.dto.TransactionType.CASHOUT
import com.wutsi.blog.transaction.dto.TransactionType.CHARGE
import com.wutsi.blog.transaction.dto.TransactionType.DONATION
import com.wutsi.blog.transaction.dto.UpdateWalletAccountCommand
import com.wutsi.blog.transaction.dto.WalletAccountUpdatedEventPayload
import com.wutsi.blog.transaction.dto.WalletCreatedEventPayload
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.blog.user.service.UserService
import com.wutsi.blog.util.DateUtils
import com.wutsi.event.store.Event
import com.wutsi.event.store.EventStore
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.exception.ConflictException
import com.wutsi.platform.core.error.exception.NotFoundException
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.platform.payment.PaymentException
import com.wutsi.platform.payment.core.Status.SUCCESSFUL
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date
import java.util.UUID

@Service
class WalletService(
    private val dao: WalletRepository,
    private val transactionDao: TransactionRepository,
    private val eventStore: EventStore,
    private val eventStream: EventStream,
    private val userService: UserService,
    private val logger: KVLogger,
    @Value("\${wutsi.application.transaction.cashout.frequency-days}") private val cashoutFrequencyDays: Int,
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

    fun findByIds(ids: List<String>): List<WalletEntity> =
        dao.findAllById(ids).toList()

    fun findWalletToCashout(now: Date): List<WalletEntity> =
        dao.findByNextCashoutDateLessThanEqualAndBalanceGreaterThanAndAccountNumberNotNull(now, 0)

    @Transactional
    fun onTransactionSuccessful(tx: TransactionEntity) {
        tx.wallet ?: return

        val now = Date()
        val wallet = tx.wallet
        if (tx.type == CASHOUT) {
            wallet.lastModificationDateTime = now
            wallet.nextCashoutDate = DateUtils.addDays(now, cashoutFrequencyDays)
            logger.add("last_modification_date_time", wallet.lastModificationDateTime)
        } else if (tx.type == DONATION) {
            wallet.donationCount = transactionDao.countByWalletAndTypeAndStatus(wallet, DONATION, SUCCESSFUL) ?: 0
        } else if (tx.type == CHARGE) {
            wallet.chargeCount = transactionDao.countByWalletAndTypeAndStatus(wallet, CHARGE, SUCCESSFUL) ?: 0
        }

        wallet.balance = computeBalance(wallet)
        wallet.lastModificationDateTime = now
        if (wallet.nextCashoutDate == null) {
            wallet.nextCashoutDate = DateUtils.addDays(now, cashoutFrequencyDays)
        }
        dao.save(wallet)

        logger.add("wallet_id", wallet.id)
        logger.add("user_id", wallet.user.id)
        logger.add("balance", wallet.balance)
        logger.add("next_cashout_date", wallet.nextCashoutDate)
    }

    @Transactional
    fun onTransactionFailed(wallet: WalletEntity, tx: TransactionEntity) {
        if (tx.type == CASHOUT) {
            wallet.balance = computeBalance(wallet)
            wallet.lastModificationDateTime = Date()
            dao.save(wallet)

            logger.add("wallet_id", wallet.id)
            logger.add("user_id", wallet.user.id)
            logger.add("balance", wallet.balance)
        }
    }

    fun computeBalance(wallet: WalletEntity): Long =
        (transactionDao.sumNetByWalletAndTypeAndStatus(wallet, DONATION, SUCCESSFUL) ?: 0) +
                (transactionDao.sumNetByWalletAndTypeAndStatus(wallet, CHARGE, SUCCESSFUL) ?: 0) -
                (transactionDao.sumNetByWalletAndTypeAndStatus(wallet, CASHOUT, SUCCESSFUL) ?: 0)

    fun prepareCashout(wallet: WalletEntity, amount: Long) {
        if (wallet.balance - amount < 0) {
            throw PaymentException(
                error = com.wutsi.platform.payment.core.Error(
                    code = com.wutsi.platform.payment.core.ErrorCode.NOT_ENOUGH_FUNDS,
                    transactionId = "",
                ),
            )
        }

        wallet.balance -= amount
        wallet.lastModificationDateTime = Date()
        dao.save(wallet)
    }

    fun getMinCashoutAmount(wallet: WalletEntity): Long =
        Country.all.find { it.code == wallet.country }?.minCashoutAmount ?: 0L

    @Transactional
    fun create(command: CreateWalletCommand): WalletEntity {
        logger.add("request_country", command.country)
        logger.add("request_user_id", command.userId)

        // Already created?
        val user = userService.findById(command.userId)
        val opt = dao.findByUser(user)
        if (opt.isPresent) {
            return opt.get()
        }

        val wallet = execute(command, user)
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

    private fun execute(command: CreateWalletCommand, user: UserEntity): WalletEntity {
        // Validation
        if (!user.blog) {
            throw ConflictException(Error(USER_DONT_SUPPORT_WALLET))
        }
        val country = Country.all.find { it.code == command.country }
            ?: throw ConflictException(Error(COUNTRY_DONT_SUPPORT_WALLET))

        // Wallet
        val wallet = dao.save(
            WalletEntity(
                id = UUID.randomUUID().toString(),
                user = user,
                country = command.country,
                currency = country.currency,
            ),
        )
        userService.onWalletCreated(user, wallet)
        return wallet
    }

    @Transactional
    fun updateAccount(command: UpdateWalletAccountCommand) {
        logger.add("request_wallet_id", command.walletId)
        logger.add("request_number", command.number)
        logger.add("request_owner", command.owner)
        logger.add("request_type", command.type)
        logger.add("request_timestamp", command.timestamp)

        val wallet = execute(command)
        val payload = WalletAccountUpdatedEventPayload(
            number = command.number,
            owner = command.owner,
            type = command.type,
        )
        notify(WALLET_ACCOUNT_UPDATED_EVENT, wallet.id!!, null, command.timestamp, payload)
    }

    private fun execute(command: UpdateWalletAccountCommand): WalletEntity {
        val wallet = findById(command.walletId)
        val country = Country.all.find { it.code == wallet.country }
            ?: throw ConflictException(
                error = Error(
                    code = COUNTRY_DONT_SUPPORT_WALLET,
                    data = mapOf("country" to wallet.country),
                ),
            )

        wallet.accountNumber = command.number
        wallet.accountOwner = command.owner
        wallet.accountType = command.type
        wallet.lastModificationDateTime = Date()
        return dao.save(wallet)
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

    private fun isPhoneNumberValid(number: String, country: Country): Boolean =
        try {
            PhoneNumberUtil.getInstance().parse(number, country.code)
            true
        } catch (ex: Exception) {
            false
        }
}
