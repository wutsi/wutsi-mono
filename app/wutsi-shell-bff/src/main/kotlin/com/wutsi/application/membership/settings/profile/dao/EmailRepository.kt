package com.wutsi.application.membership.settings.profile.dao

import com.wutsi.application.common.dao.AbstractCacheRepository
import com.wutsi.application.membership.settings.profile.entity.EmailEntity
import org.springframework.stereotype.Service

@Service
class EmailRepository : AbstractCacheRepository<EmailEntity>() {
    override fun get(): EmailEntity =
        cache.get(getKey(), EmailEntity::class.java)
            ?: EmailEntity()
}
