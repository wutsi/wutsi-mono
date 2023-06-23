package com.wutsi.blog.transaction.domain

import com.wutsi.blog.user.domain.UserEntity
import java.util.Date
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "T_WALLET")
data class WalletEntity(
    @Id
    val id: String? = null,

    @ManyToOne
    @JoinColumn(name = "user_fk")
    val user: UserEntity = UserEntity(),

    val country: String = "",
    val currency: String = "",
    var balance: Long = 0,
    var donationCount: Long = 0,
    val creationDateTime: Date = Date(),
    var lastModificationDateTime: Date = Date(),
)
