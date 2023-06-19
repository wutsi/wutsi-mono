package com.wutsi.blog.account.domain

import com.wutsi.blog.user.domain.UserEntity
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
