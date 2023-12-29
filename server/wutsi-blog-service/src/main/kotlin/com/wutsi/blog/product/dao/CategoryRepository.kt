package com.wutsi.blog.product.dao

import com.wutsi.blog.product.domain.CategoryEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CategoryRepository : CrudRepository<CategoryEntity, Long> {
    fun findByParentId(parentId: Long?): List<CategoryEntity>
}
