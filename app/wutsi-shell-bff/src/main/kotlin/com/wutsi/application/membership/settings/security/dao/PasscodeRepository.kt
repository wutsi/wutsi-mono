package com.wutsi.application.membership.settings.security.dao

import com.wutsi.application.common.dao.AbstractCacheRepository
import com.wutsi.application.membership.settings.security.entity.PasscodeEntity
import org.springframework.stereotype.Service

@Service
class PasscodeRepository : AbstractCacheRepository<PasscodeEntity>() {
    override fun get(): PasscodeEntity =
        cache.get(getKey(), PasscodeEntity::class.java)
            ?: PasscodeEntity()
}
