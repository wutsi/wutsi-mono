package com.wutsi.security.manager.dao

import com.wutsi.security.manager.entity.PasswordEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PasswordRepository : CrudRepository<PasswordEntity, Long> {
    fun findByUsernameAndIsDeleted(username: String, isDeleted: Boolean): List<PasswordEntity>
    fun findByAccountIdAndIsDeleted(accountId: Long, isDeleted: Boolean): List<PasswordEntity>
}
