package com.wutsi.blog.app.form

import com.wutsi.blog.product.dto.ProductType

data class CreateProductForm(
    val type: ProductType = ProductType.UNKNOWN,
    val title: String = "",
    val description: String? = null,
    val categoryId: Long? = null,
    val price: Long = 0,
)
