package com.wutsi.application.marketplace.settings.product.entity

import com.wutsi.enums.ProductType
import java.io.Serializable

data class PictureEntity(
    val url: String? = null,
    var type: ProductType = ProductType.UNKNOWN,
) : Serializable
