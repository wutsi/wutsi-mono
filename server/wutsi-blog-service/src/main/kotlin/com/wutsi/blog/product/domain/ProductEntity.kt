package com.wutsi.blog.product.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_PRODUCT")
data class ProductEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "user_fk")
    val userId: Long = -1,

    val externalId: String = "",
    var title: String = "",
    var description: String? = null,
    var imageUrl: String? = null,
    var fileUrl: String? = null,
    var price: Long = 0,
    var currency: String = "",
    var available: Boolean = true,
    val creationDateTime: Date = Date(),
    var modificationDateTime: Date = Date(),
    val orderCount: Long = 0,
    val totalSales: Long = 0,
)
