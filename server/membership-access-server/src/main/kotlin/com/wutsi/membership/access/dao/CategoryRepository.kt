package com.wutsi.membership.access.dao

import com.wutsi.membership.access.entity.CategoryEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CategoryRepository : CrudRepository<CategoryEntity, Long>
