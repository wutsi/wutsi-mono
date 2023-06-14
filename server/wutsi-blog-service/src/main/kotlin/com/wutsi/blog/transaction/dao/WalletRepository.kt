package com.wutsi.blog.transaction.dao

import com.wutsi.blog.transaction.domain.WalletEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface WalletRepository : CrudRepository<WalletEntity, String>
