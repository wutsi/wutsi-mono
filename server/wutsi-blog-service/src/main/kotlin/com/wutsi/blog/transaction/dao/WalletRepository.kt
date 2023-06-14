package com.wutsi.blog.transaction.dao

import com.wutsi.blog.transaction.domain.TransactionEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface TransactionRepository : CrudRepository<TransactionEntity, String> {
    fun findByIdempotencyKey(idempotencyKey: String): Optional<TransactionEntity>
}
