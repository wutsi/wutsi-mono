package com.wutsi.application.marketplace.settings.product.dao

import com.wutsi.application.common.dao.AbstractCacheRepository
import com.wutsi.application.marketplace.settings.product.entity.PictureEntity
import org.springframework.stereotype.Service

@Service
class PictureRepository : AbstractCacheRepository<PictureEntity>() {
    override fun get(): PictureEntity =
        cache.get(getKey(), PictureEntity::class.java)
            ?: PictureEntity()
}
