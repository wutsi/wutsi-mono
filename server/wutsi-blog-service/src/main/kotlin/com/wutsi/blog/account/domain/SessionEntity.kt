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
@Table(name = "T_SESSION")
data class SessionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_fk")
    val account: AccountEntity = AccountEntity(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "run_as_user_fk")
    var runAsUser: UserEntity? = null,

    val accessToken: String = "",
    val refreshToken: String? = null,
    val loginDateTime: Date = Date(),
    var logoutDateTime: Date? = null,
)
