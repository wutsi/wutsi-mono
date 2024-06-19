package com.wutsi.blog.transaction.dao

import com.wutsi.blog.transaction.domain.SuperFanEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SuperFanRepository : CrudRepository<SuperFanEntity, String> {
    fun countByWalletId(walletId: String): Long?

}