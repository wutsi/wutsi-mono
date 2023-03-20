package com.wutsi.security.manager.entity

import java.util.Date
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "T_PASSWORD")
data class PasswordEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    var accountId: Long = -1,
    val username: String = "",
    var value: String = "",
    val salt: String = "",
    var isDeleted: Boolean = false,

    val created: Date = Date(),
    var updated: Date = Date(),
    var deleted: Date? = null,
)
