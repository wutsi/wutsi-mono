package com.wutsi.security.manager.entity

import java.util.Date
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "T_LOGIN")
data class LoginEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val accountId: Long = -1,
    val hash: String = "",
    val accessToken: String = "",
    val created: Date = Date(),
    val expires: Date = Date(),
    var expired: Date? = null,
)
