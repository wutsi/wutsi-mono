package com.wutsi.marketplace.access.dao

import com.wutsi.marketplace.access.entity.CategoryEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CategoryRepository : CrudRepository<CategoryEntity, Long> {
    fun findByParentId(parentId: Long?): List<CategoryEntity>
}
