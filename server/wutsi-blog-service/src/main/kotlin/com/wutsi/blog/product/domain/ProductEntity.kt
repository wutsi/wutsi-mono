package com.wutsi.blog.product.domain

import com.wutsi.blog.product.dto.ProductStatus
import com.wutsi.blog.product.dto.ProductType
import jakarta.persistence.Entity
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
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

    @ManyToOne()
    @JoinColumn(name = "category_fk")
    var category: CategoryEntity? = null,

    var externalId: String? = null,
    var title: String = "",
    var description: String? = null,
    var imageUrl: String? = null,
    var fileUrl: String? = null,
    var previewUrl: String? = null,
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
    var viewCount: Long = 0,
    var language: String? = null,
    var numberOfPages: Int? = null,

    @Enumerated
    var type: ProductType = ProductType.UNKNOWN,

    var liretamaUrl: String? = null,

    var deleted: Boolean = false,
    var deletedDateTime: Date? = null,
    var processingFile: Boolean = false,
    var processingFileDateTime: Date? = null,
    var hashtag: String? = null,
    var cvr: Double = 0.0,
) {
    @PreUpdate
    @PrePersist
    fun computeCVR() {
        cvr = if (viewCount == 0L) 0.0 else orderCount.toDouble() / viewCount.toDouble()
    }
}
