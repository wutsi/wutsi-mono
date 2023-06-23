package com.wutsi.blog.transaction.dao

import com.wutsi.blog.transaction.domain.TransactionEntity
import com.wutsi.blog.transaction.domain.WalletEntity
import com.wutsi.blog.transaction.dto.TransactionType
import com.wutsi.platform.payment.core.Status
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface TransactionRepository : CrudRepository<TransactionEntity, String> {
    fun findByIdempotencyKey(idempotencyKey: String): Optional<TransactionEntity>

    @Query("SELECT SUM(T.net) FROM TransactionEntity T WHERE T.wallet=?1 AND T.type=?2 AND T.status=?3")
    fun sumNetByWalletAndTypeAndStatus(wallet: WalletEntity, type: TransactionType, status: Status): Long?

    fun countByWalletAndTypeAndStatus(wallet: WalletEntity, type: TransactionType, status: Status): Long
}
