package com.wutsi.blog.account.domain

import java.util.Date
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "T_ACCOUNT")
data class Account(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_fk")
    val provider: AccountProvider = AccountProvider(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_fk")
    val user: User = User(),

    val providerUserId: String = "",
    var loginCount: Long = 0,
    var lastLoginDateTime: Date? = null,
    val creationDateTime: Date = Date(),
    var modificationDateTime: Date = Date(),
)
