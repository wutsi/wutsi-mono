package com.wutsi.marketplace.access.entity

import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "T_CATEGORY")
data class CategoryEntity(
    @Id
    val id: Long = -1,

    var title: String = "",
    var titleFrench: String? = null,
    var titleFrenchAscii: String? = null,
    var level: Int = 0,
    var longTitle: String? = null,
    var longTitleFrench: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_fk")
    var parent: CategoryEntity? = null,
)
