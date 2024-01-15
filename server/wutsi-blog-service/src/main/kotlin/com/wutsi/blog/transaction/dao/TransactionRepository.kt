package com.wutsi.blog.transaction.dao

import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.product.domain.StoreEntity
import com.wutsi.blog.transaction.domain.TransactionEntity
import com.wutsi.blog.transaction.domain.WalletEntity
import com.wutsi.blog.transaction.dto.TransactionType
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.platform.payment.core.Status
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface TransactionRepository : CrudRepository<TransactionEntity, String> {
    fun findByIdempotencyKey(idempotencyKey: String): Optional<TransactionEntity>

    fun findByGatewayTransactionId(gatewayTransactionId: String): Optional<TransactionEntity>

    fun findByStoreAndUserAndTypeAndStatusOrderByCreationDateTimeDesc(
        store: StoreEntity,
        user: UserEntity,
        type: TransactionType,
        status: Status,
    ): List<TransactionEntity>

    fun findByWalletAndUserAndTypeAndStatusOrderByCreationDateTimeDesc(
        wallet: WalletEntity,
        user: UserEntity,
        type: TransactionType,
        status: Status,
    ): List<TransactionEntity>

    @Query("SELECT SUM(T.net) FROM TransactionEntity T WHERE T.wallet=?1 AND T.type=?2 AND T.status=?3")
    fun sumNetByWalletAndTypeAndStatus(wallet: WalletEntity, type: TransactionType, status: Status): Long?

    fun countByWalletAndTypeAndStatus(wallet: WalletEntity, type: TransactionType, status: Status): Long?

    @Query("SELECT SUM(T.amount) FROM TransactionEntity T WHERE T.product=?1 AND T.type=?2 AND T.status=?3")
    fun sumAmountByProductAndTypeAndStatus(product: ProductEntity, type: TransactionType, status: Status): Long?

    fun countByProductAndTypeAndStatus(product: ProductEntity, type: TransactionType, status: Status): Long?

    @Query("SELECT SUM(T.net) FROM TransactionEntity T WHERE T.store=?1 AND T.type=?2 AND T.status=?3")
    fun sumNetByStoreAndTypeAndStatus(store: StoreEntity, type: TransactionType, status: Status): Long?

    fun countByStoreAndTypeAndStatus(store: StoreEntity, type: TransactionType, status: Status): Long?
}
