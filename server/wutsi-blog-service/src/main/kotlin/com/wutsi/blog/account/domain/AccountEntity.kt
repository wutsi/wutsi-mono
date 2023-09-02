package com.wutsi.blog.account.domain

import com.wutsi.blog.user.domain.UserEntity
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_ACCOUNT")
data class AccountEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_fk")
    val provider: AccountProviderEntity = AccountProviderEntity(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_fk")
    val user: UserEntity = UserEntity(),

    val providerUserId: String = "",
    var loginCount: Long = 0,
    var lastLoginDateTime: Date? = null,
    val creationDateTime: Date = Date(),
    var modificationDateTime: Date = Date(),
)
