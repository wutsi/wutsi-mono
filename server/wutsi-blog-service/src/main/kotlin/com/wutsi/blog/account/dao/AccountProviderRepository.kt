package com.wutsi.blog.account.dao

import com.wutsi.blog.account.domain.AccountProviderEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface AccountProviderRepository : CrudRepository<AccountProviderEntity, Long> {
    fun findByNameIgnoreCase(name: String): Optional<AccountProviderEntity>
}
