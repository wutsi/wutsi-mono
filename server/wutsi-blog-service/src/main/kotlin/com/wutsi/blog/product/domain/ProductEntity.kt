package com.wutsi.blog.product.domain

import com.wutsi.blog.product.dto.ProductStatus
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_PRODUCT")
data class ProductEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne()
    @JoinColumn(name = "store_fk")
    val store: StoreEntity = StoreEntity(),

    val externalId: String = "",
    var title: String = "",
    var description: String? = null,
    var imageUrl: String? = null,
    var fileUrl: String? = null,
    var fileContentLength: Long = 0,
    var fileContentType: String? = null,
    var price: Long = 0,
    var available: Boolean = true,
    val creationDateTime: Date = Date(),
    var modificationDateTime: Date = Date(),
    var publishedDateTime: Date? = null,
    var status: ProductStatus = ProductStatus.DRAFT,
    var orderCount: Long = 0,
    var totalSales: Long = 0,
)
