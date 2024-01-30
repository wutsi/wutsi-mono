package com.wutsi.blog.transaction.dao

import com.wutsi.blog.transaction.domain.TransactionEventEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TransactionEventRepository : CrudRepository<TransactionEventEntity, Long>
