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
