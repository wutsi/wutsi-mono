package com.wutsi.security.manager.dao

import com.wutsi.security.manager.entity.LoginEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface LoginRepository : CrudRepository<LoginEntity, Long> {
    fun findByHash(hash: String): Optional<LoginEntity>
    fun findByAccountIdAndExpiredIsNull(accountId: Long): List<LoginEntity>
}
