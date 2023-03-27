package com.wutsi.application.marketplace.settings.product.entity

import java.io.Serializable

data class CategoryEntity(
    var productId: Long = -1,
    var category0Id: Long? = null,
    var category1Id: Long? = null,
) : Serializable
