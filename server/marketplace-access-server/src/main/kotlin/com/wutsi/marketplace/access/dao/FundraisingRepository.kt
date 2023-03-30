package com.wutsi.marketplace.access.dao

import com.wutsi.enums.FundraisingStatus
import com.wutsi.marketplace.access.entity.FundraisingEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface FundraisingRepository : CrudRepository<FundraisingEntity, Long> {
    fun findByAccountIdAndStatusNotIn(accountId: Long, status: List<FundraisingStatus>): List<FundraisingEntity>
}
