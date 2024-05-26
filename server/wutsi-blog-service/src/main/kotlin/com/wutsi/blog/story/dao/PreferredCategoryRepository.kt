package com.wutsi.blog.story.dao

import com.wutsi.blog.story.domain.PreferredCategoryEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PreferredCategoryRepository : CrudRepository<PreferredCategoryEntity, String> {
    fun findByUserIdOrderByTotalReadsDesc(userId: Long): List<PreferredCategoryEntity>
}