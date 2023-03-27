package com.wutsi.application.membership.settings.business.dao

import com.wutsi.application.common.dao.AbstractCacheRepository
import com.wutsi.application.membership.settings.business.entity.BusinessEntity
import org.springframework.stereotype.Service

@Service
class BusinessRepository : AbstractCacheRepository<BusinessEntity>() {
    override fun get(): BusinessEntity =
        cache.get(getKey(), BusinessEntity::class.java)
            ?: BusinessEntity()
}
