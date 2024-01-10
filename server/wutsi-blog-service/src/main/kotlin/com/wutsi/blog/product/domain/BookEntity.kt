package com.wutsi.blog.product.domain

import com.wutsi.blog.transaction.domain.TransactionEntity
import com.wutsi.blog.user.domain.UserEntity
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_BOOK")
data class BookEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne()
    @JoinColumn(name = "product_fk")
    val product: ProductEntity = ProductEntity(),

    @ManyToOne()
    @JoinColumn(name = "user_fk")
    var user: UserEntity = UserEntity(),

    @ManyToOne()
    @JoinColumn(name = "transaction_fk")
    var transaction: TransactionEntity = TransactionEntity(),

    var location: String? = null,
    var readPercentage: Int = 0,
    val creationDateTime: Date = Date(),
    var modificationDateTime: Date = Date()
)
