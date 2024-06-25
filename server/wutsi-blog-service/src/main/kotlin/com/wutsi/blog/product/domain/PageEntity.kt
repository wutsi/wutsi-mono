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
@Table(name = "T_PAGE")
data class PageEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne()
    @JoinColumn(name = "product_fk")
    val product: ProductEntity = ProductEntity(),

    val number: Int = 0,
    var contentType: String = "",
    var contentUrl: String = "",
    val creationDateTime: Date = Date(),
)
