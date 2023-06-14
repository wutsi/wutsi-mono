package com.wutsi.blog.transaction.domain

import com.wutsi.blog.transaction.dto.PaymentMethodType
import com.wutsi.blog.transaction.dto.TransactionType
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.platform.payment.GatewayType
import com.wutsi.platform.payment.core.Status
import java.util.Date
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "T_TRANSACTION")
data class TransactionEntity(
    @Id
    val id: String? = null,
    val idempotencyKey: String = "",

    val type: TransactionType = TransactionType.UNKNOWN,
    var status: Status = Status.UNKNOWN,

    @ManyToOne
    @JoinColumn(name = "merchant_fk")
    val merchant: UserEntity = UserEntity(),

    @ManyToOne
    @JoinColumn(name = "user_fk")
    val user: UserEntity? = null,
    var email: String? = null,
    val anonymous: Boolean = false,

    var amount: Long = 0L,
    var fees: Long = 0L,
    var net: Long = 0L,
    var gatewayFees: Long = 0,
    val currency: String = "",
    val description: String? = null,

    val paymentMethodOwner: String = "",
    val paymentMethodNumber: String = "",
    val paymentMethodType: PaymentMethodType = PaymentMethodType.UNKNOWN,
    val gatewayType: GatewayType = GatewayType.UNKNOWN,
    var gatewayTransactionId: String? = null,
    var errorCode: String? = null,
    var supplierErrorCode: String? = null,
    var errorMessage: String? = null,

    val creationDateTime: Date = Date(),
    var lastModificationDateTime: Date = Date(),
)
