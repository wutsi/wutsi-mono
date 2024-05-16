package com.wutsi.blog.transaction.domain

import com.wutsi.blog.ads.domain.AdsEntity
import com.wutsi.blog.product.domain.CouponEntity
import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.product.domain.StoreEntity
import com.wutsi.blog.product.dto.DiscountType
import com.wutsi.blog.transaction.dto.PaymentMethodType
import com.wutsi.blog.transaction.dto.TransactionType
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.platform.payment.GatewayType
import com.wutsi.platform.payment.core.Status
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_TRANSACTION")
data class TransactionEntity(
    @Id
    val id: String? = null,
    val idempotencyKey: String = "",

    val type: TransactionType = TransactionType.UNKNOWN,
    var status: Status = Status.UNKNOWN,

    @ManyToOne
    @JoinColumn(name = "product_fk")
    val product: ProductEntity? = null,

    @ManyToOne
    @JoinColumn(name = "ads_fk")
    val ads: AdsEntity? = null,

    @ManyToOne
    @JoinColumn(name = "store_fk")
    val store: StoreEntity? = null,

    @ManyToOne
    @JoinColumn(name = "wallet_fk")
    val wallet: WalletEntity? = null,

    @ManyToOne
    @JoinColumn(name = "user_fk")
    var user: UserEntity? = null,

    @ManyToOne
    @JoinColumn(name = "coupon_fk")
    val coupon: CouponEntity? = null,

    var email: String? = null,
    val anonymous: Boolean = false,

    var amount: Long = 0L,
    var fees: Long = 0L,
    var net: Long = 0L,
    var gatewayFees: Long = 0,
    val currency: String = "",
    val description: String? = null,

    var paymentMethodOwner: String = "",
    val paymentMethodNumber: String = "",
    val paymentMethodType: PaymentMethodType = PaymentMethodType.UNKNOWN,
    val gatewayType: GatewayType = GatewayType.UNKNOWN,
    var gatewayTransactionId: String? = null,
    var errorCode: String? = null,
    var supplierErrorCode: String? = null,
    var errorMessage: String? = null,

    val creationDateTime: Date = Date(),
    var lastModificationDateTime: Date = Date(),
    val discountType: DiscountType? = null,
    val internationalAmount: Long? = null,
    val internationalCurrency: String? = null,
    val exchangeRate: Double? = null,
    val channel: String? = null,
)
