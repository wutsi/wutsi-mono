package com.wutsi.blog.product.service

import com.wutsi.blog.error.ErrorCode
import com.wutsi.blog.event.EventType
import com.wutsi.blog.event.EventType.STORE_CREATED_EVENT
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.product.dao.ProductRepository
import com.wutsi.blog.product.dao.StoreRepository
import com.wutsi.blog.product.domain.StoreEntity
import com.wutsi.blog.product.dto.CreateStoreCommand
import com.wutsi.blog.product.dto.ProductStatus
import com.wutsi.blog.product.dto.UpdateStoreDiscountsCommand
import com.wutsi.blog.transaction.dao.TransactionRepository
import com.wutsi.blog.transaction.dto.TransactionType
import com.wutsi.blog.transaction.service.WalletService
import com.wutsi.blog.user.service.UserService
import com.wutsi.event.store.Event
import com.wutsi.event.store.EventStore
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.ConflictException
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.payment.core.Status
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date
import java.util.UUID

@Service
class StoreService(
    private val dao: StoreRepository,
    private val productDao: ProductRepository,
    private val walletService: WalletService,
    private val userService: UserService,
    private val logger: KVLogger,
    private val eventStore: EventStore,
    private val transactionDao: TransactionRepository
) {
    fun findById(storeId: String): StoreEntity =
        dao.findById(storeId)
            .orElseThrow {
                com.wutsi.platform.core.error.exception.NotFoundException(
                    error = Error(
                        code = ErrorCode.STORE_NOT_FOUND
                    )
                )
            }

    @Transactional
    fun create(command: CreateStoreCommand): StoreEntity {
        logger.add("command_user_id", command.userId)

        // Already created?
        val opt = dao.findByUserId(command.userId)
        if (opt.isPresent) {
            return opt.get()
        }

        // Create
        val store = execute(command)
        notify(STORE_CREATED_EVENT, store, command.timestamp)
        return store
    }

    private fun execute(command: CreateStoreCommand): StoreEntity {
        val user = userService.findById(command.userId)
        if (user.walletId == null) {
            throw ConflictException(
                error = Error(
                    code = ErrorCode.USER_HAS_NO_WALLET
                )
            )
        }

        val wallet = walletService.findById(user.walletId!!)

        // Create the store
        val store = dao.save(
            StoreEntity(
                id = UUID.randomUUID().toString(),
                currency = wallet.currency,
                userId = command.userId,
            )
        )

        // Sync the user
        userService.onStoreCreated(user, store)
        return store
    }

    @Transactional
    fun updateDiscounts(command: UpdateStoreDiscountsCommand) {
        logger.add("command", "UpdateStoreDiscountsCommand")
        logger.add("command_store_id", command.storeId)
        logger.add("command_first_purchase_discount", command.firstPurchaseDiscount)
        logger.add("command_next_purchase_discount", command.nextPurchaseDiscount)
        logger.add("command_next_purchase_discount_days", command.nextPurchaseDiscountDays)
        logger.add("command_subscriber_discount", command.subscriberDiscount)

        val store = execute(command)
        notify(EventType.STORE_DISCOUNTS_UPDATED_EVENT, store, command.timestamp)
    }

    private fun execute(command: UpdateStoreDiscountsCommand): StoreEntity {
        val store = findById(command.storeId)
        store.subscriberDiscount = command.subscriberDiscount
        store.firstPurchaseDiscount = command.firstPurchaseDiscount
        store.nextPurchaseDiscount = command.nextPurchaseDiscount
        store.nextPurchaseDiscountDays = command.nextPurchaseDiscountDays
        store.modificationDateTime = Date()
        return dao.save(store)
    }

    @Transactional
    fun onProductsImported(store: StoreEntity) {
        store.productCount = productDao.countByStore(store) ?: 0
        store.publishProductCount = productDao.countByStoreAndStatus(store, ProductStatus.PUBLISHED) ?: 0
        store.modificationDateTime = Date()
        dao.save(store)
    }

    @Transactional
    fun onProductsCreated(store: StoreEntity) {
        store.productCount = productDao.countByStore(store) ?: 0
        store.publishProductCount = productDao.countByStoreAndStatus(store, ProductStatus.PUBLISHED) ?: 0
        store.modificationDateTime = Date()
        dao.save(store)
    }

    @Transactional
    fun onTransactionSuccessful(store: StoreEntity) {
        store.orderCount =
            transactionDao.countByStoreAndTypeAndStatus(store, TransactionType.CHARGE, Status.SUCCESSFUL) ?: 0

        store.totalSales =
            transactionDao.sumNetByStoreAndTypeAndStatus(store, TransactionType.CHARGE, Status.SUCCESSFUL) ?: 0

        store.modificationDateTime = Date()
        dao.save(store)
    }

    fun notify(type: String, store: StoreEntity, timestamp: Long) {
        val event = Event(
            streamId = StreamId.STORE,
            type = type,
            entityId = store.id!!,
            userId = store.userId.toString(),
            timestamp = Date(timestamp),
        )
        eventStore.store(event)
    }
}
