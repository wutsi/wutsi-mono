package com.wutsi.blog.transaction.domain

import com.wutsi.blog.transaction.dto.PaymentMethodType
import com.wutsi.blog.user.domain.UserEntity
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.Date

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
    var chargeCount: Long = 0,
    val creationDateTime: Date = Date(),
    var lastModificationDateTime: Date = Date(),
    var accountNumber: String? = null,
    var accountType: PaymentMethodType = PaymentMethodType.UNKNOWN,
    var accountOwner: String? = null,
    var lastCashoutDateTime: Date? = null,
    var nextCashoutDate: Date? = null,
)
