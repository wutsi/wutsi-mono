package com.wutsi.blog.product.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_STORE")
data class StoreEntity(
    @Id
    val id: String? = null,

    @Column(name = "user_fk")
    val userId: Long = -1,

    val currency: String = "",
    var feedUrl: String = "",
    var productCount: Long = 0,
    var orderCount: Long = 0,
    var totalSales: Long = 0,
    var creationDateTime: Date = Date(),
    var modificationDateTime: Date = Date(),
)
