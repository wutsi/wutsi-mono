package com.wutsi.blog.payment.dao

import com.wutsi.blog.payment.entity.TransactionEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TransactionRepository : CrudRepository<TransactionEntity, String>
