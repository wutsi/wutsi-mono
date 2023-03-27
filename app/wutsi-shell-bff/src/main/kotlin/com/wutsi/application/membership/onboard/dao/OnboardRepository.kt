package com.wutsi.application.membership.onboard.dao

import com.wutsi.application.common.dao.AbstractCacheRepository
import com.wutsi.application.membership.onboard.entity.OnboardEntity
import com.wutsi.application.membership.onboard.exception.OnboardEntityNotFoundException
import org.springframework.stereotype.Service

@Service
class OnboardRepository : AbstractCacheRepository<OnboardEntity>() {
    override fun get(): OnboardEntity =
        cache.get(getKey(), OnboardEntity::class.java)
            ?: throw OnboardEntityNotFoundException()
}
