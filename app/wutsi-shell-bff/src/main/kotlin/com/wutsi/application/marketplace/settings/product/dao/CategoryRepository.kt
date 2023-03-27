package com.wutsi.application.marketplace.settings.product.dao

import com.wutsi.application.common.dao.AbstractCacheRepository
import com.wutsi.application.marketplace.settings.product.entity.CategoryEntity
import org.springframework.stereotype.Service

@Service
class CategoryRepository : AbstractCacheRepository<CategoryEntity>() {
    override fun get(): CategoryEntity =
        cache.get(getKey(), CategoryEntity::class.java)
            ?: CategoryEntity()
}
