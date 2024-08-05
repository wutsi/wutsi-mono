package com.wutsi.blog.user.service

import com.wutsi.blog.user.dao.PreferredCategoryRepository
import com.wutsi.blog.user.domain.PreferredCategoryEntity
import org.springframework.stereotype.Service

@Service
class PreferredCategoryService(
    private val dao: PreferredCategoryRepository
) {
    fun findByUser(userId: Long): List<PreferredCategoryEntity> =
        dao.findByUserIdOrderByTotalReadsDesc(userId).mapNotNull { it }
}