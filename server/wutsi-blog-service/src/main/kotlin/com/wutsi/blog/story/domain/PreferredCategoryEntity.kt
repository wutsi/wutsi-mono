package com.wutsi.blog.story.domain

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "V_PREFERRED_CATEGORY")
data class PreferredCategoryEntity(
    @Id
    val id: String = "",
    val userId: Long = -1,
    val categoryId: Long? = null,
    val totalReads: Long = 0,
)