package com.wutsi.marketplace.access.entity

import com.wutsi.enums.DiscountType
import java.util.Date
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "T_DISCOUNT")
data class DiscountEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_fk")
    val store: StoreEntity = StoreEntity(),

    var name: String = "",
    var rate: Int = 0,
    var starts: Date? = null,
    var ends: Date? = null,
    var allProducts: Boolean = false,
    var isDeleted: Boolean = false,
    var type: DiscountType = DiscountType.SALES,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "T_DISCOUNT_PRODUCT",
        joinColumns = arrayOf(JoinColumn(name = "discount_fk")),
        inverseJoinColumns = arrayOf(JoinColumn(name = "product_fk")),
    )
    val products: MutableList<ProductEntity> = mutableListOf(),

    val created: Date = Date(),
    var updated: Date = Date(),
    var deleted: Date? = null,
)
