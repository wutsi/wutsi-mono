package com.wutsi.blog.product.domain

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "T_CATEGORY")
data class CategoryEntity(
    @Id
    val id: Long = -1,

    var title: String = "",
    var titleFrench: String? = null,
    var titleFrenchAscii: String? = null,
    var level: Int = 0,
    var longTitle: String = "",
    var longTitleFrench: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_fk")
    var parent: CategoryEntity? = null,
)
