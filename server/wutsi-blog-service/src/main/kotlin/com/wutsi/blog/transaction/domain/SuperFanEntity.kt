package com.wutsi.blog.transaction.domain

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "V_SUPER_FAN")
data class SuperFanEntity(
    @Id
    val id: String? = null,

    val walletId: String = "",
    val userId: Long? = null,
    val transactionCount: Long = 0,
    val value: Long = 0,
)
