package com.wutsi.application.checkout.settings.account.dao

import com.wutsi.application.checkout.settings.account.entity.AccountEntity
import com.wutsi.application.common.dao.AbstractCacheRepository
import org.springframework.stereotype.Service

@Service
class AccountRepository : AbstractCacheRepository<AccountEntity>() {
    override fun get(): AccountEntity =
        cache.get(getKey(), AccountEntity::class.java)
            ?: AccountEntity()
}
