package com.wutsi.membership.access.entity

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "T_CATEGORY")
data class CategoryEntity(
    @Id
    val id: Long = -1,
    var title: String = "",
    var titleFrench: String? = null,
    var titleFrenchAscii: String? = null,
)
