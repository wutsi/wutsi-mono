package com.wutsi.blog.product.domain

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_PRODUCT_IMPORT")
data class ProductImportEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "store_fk")
    val store: StoreEntity = StoreEntity(),

    val errorCount: Int = 0,
    val importedCount: Int = 0,
    val unpublishedCount: Int = 0,
    val url: String = "",
    val errorReportUrl: String? = null,
    val creationDateTime: Date = Date(),
)
