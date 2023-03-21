package com.wutsi.marketplace.access.entity

import java.util.Date
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "T_PICTURE")
data class PictureEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne()
    @JoinColumn(name = "product_fk")
    val product: ProductEntity = ProductEntity(),

    val url: String = "",
    val hash: String = "",
    var isDeleted: Boolean = false,
    val created: Date = Date(),
    var deleted: Date? = null,
)
